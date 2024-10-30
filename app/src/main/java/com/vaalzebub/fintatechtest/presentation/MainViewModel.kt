package com.vaalzebub.fintatechtest.presentation

import android.util.Log
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.vaalzebub.fintatechtest.data.source.WebSocketMessage
import com.vaalzebub.fintatechtest.domain.model.HistoryPrice
import com.vaalzebub.fintatechtest.domain.model.ItemModel
import com.vaalzebub.fintatechtest.domain.repository.SocketClient
import com.vaalzebub.fintatechtest.domain.usecase.ApiUseCase
import com.vaalzebub.fintatechtest.domain.utils.WebSocketListenerImpl
import com.vaalzebub.fintatechtest.domain.utils.onError
import com.vaalzebub.fintatechtest.domain.utils.onSuccess
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val apiUseCase: ApiUseCase, private val socket: SocketClient) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.onStart {
        load()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        MainUiState()
    )
    private var state: MainUiState
        get() = _uiState.value
        set(newState) {
            _uiState.update { newState }
        }
    private val _messageUiState = MutableStateFlow(MessageUiState())

    /*
    * Websocket messages happen too often and call recomposition on chart
    * so best option is to separate states
    */
    val messageUiState = _messageUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        MessageUiState()
    )
    private var messageState: MessageUiState
        get() = _messageUiState.value
        set(newState) {
            _messageUiState.update { newState }
        }
    private var _job = Job()
    private val job: CompletableJob
        get() {
            _job.cancelChildren()
            _job.cancel()
            _job = Job()
            return _job
        }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO + Job()) {
            apiUseCase.authorize().collectLatest { data ->
                data.onSuccess { result ->
                    state = state.copy(
                        isLoading = false,
                        isLoggedIn = result
                    )

                    loadInstruments()
                }.onError { error ->

                    state = state.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = error.toString()
                    )
                }

            }
        }
    }

    private suspend fun loadInstruments() {
        if (!state.isLoggedIn) return

        state = state.copy(isLoading = true)
        apiUseCase.getInstruments().collectLatest { data ->
            data.onSuccess { result ->
                state = state.copy(
                    isLoading = false,
                    instruments = result,
                )
            }.onError { error ->

                state = state.copy(
                    isLoading = false,
                    instruments = emptyList(),
                    error = error.toString()
                )
            }
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnChangeInstrument -> {
                onChangeInstrument(event.item)
            }

            MainEvent.UpdateInstruments -> {
                viewModelScope.launch(Dispatchers.IO + Job()) {
                    loadInstruments()
                }
            }

            MainEvent.Subscribe -> {
                subscribe()
            }

            is MainEvent.OnConnected -> {
                messageState = messageState.copy(socketConnected = true)
            }

            MainEvent.OnDisconnected -> {
                messageState = messageState.copy(socketConnected = false)
            }

            is MainEvent.OnReceive -> {
                Log.e("Vaalbub", event.message)
                val message = Gson().fromJson(event.message, WebSocketMessage::class.java)
                messageState = messageState.copy(socketMessage = message)
            }


        }
    }

    private fun subscribe() {
        state.currentInstrument?.let {
            if (messageState.socketConnected) {
                job.cancel()
            }
            viewModelScope.launch(Dispatchers.IO + job) {
                socket.connect(
                    instrumentId = it.id,
                    listener = WebSocketListenerImpl(
                        onEvent = this@MainViewModel::onEvent
                    )
                )
            }
        }
    }


    private fun onChangeInstrument(item: ItemModel) {
        state = state.copy(isLoading = true)
        if (messageState.socketConnected) {
            job.cancel()
        }
        viewModelScope.launch(Dispatchers.IO + job) {
            apiUseCase.getPrices(item.id).collectLatest { result ->
                result.onSuccess { data ->
                    val low = data.map { it.low }
                    val high = data.map { it.high }
                    val open = data.map { it.open }
                    val close = data.map { it.close }

                    val list = low + high + open + close
                    state = state.copy(
                        isLoading = false,

                        chartData = chartData(data),
                        chartMin = list.minOfOrNull { it } ?: 0.0,
                        chartMax = list.maxOfOrNull { it } ?: 0.0,
                        currentInstrument = item
                    )
                }.onError { error ->
                    state = state.copy(
                        isLoading = false,
                        chartData = emptyList(),
                        error = error.toString()
                    )
                }
            }
        }
    }

    private fun chartData(items: List<HistoryPrice>): List<Line> {
        return listOf(
            Line(
                label = "low",
                values = items.map { it.low },
                color = Brush.linearGradient(listOf(Color.Green, Color.Green)),
                curvedEdges = true,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 15f
                    )
                )
            ),
            Line(
                label = "high",
                values = items.map { it.high },
                color = Brush.linearGradient(listOf(Color.Magenta, Color.Magenta)),
                curvedEdges = false,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 15f
                    )
                )
            ),
            Line(
                label = "open",
                values = items.map { it.open },
                color = Brush.linearGradient(listOf(Color.Blue, Color.Blue)),
                curvedEdges = true,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 15f
                    )
                )
            ),
            Line(
                label = "closed",
                values = items.map { it.close },
                color = Brush.linearGradient(listOf(Color.Cyan, Color.Cyan)),
                curvedEdges = true,
                drawStyle = DrawStyle.Stroke(
                    width = 3.dp,
                    strokeStyle = StrokeStyle.Dashed(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 15f
                    )
                )
            ),
        )
    }

}


