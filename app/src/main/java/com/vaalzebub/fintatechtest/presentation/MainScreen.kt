package com.vaalzebub.fintatechtest.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import org.koin.androidx.compose.koinViewModel


@Destination<RootGraph>(start = true)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val messageState by viewModel.messageUiState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        if (state.isLoading && !state.isLoggedIn) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.Center)
            )
        } else {
            Content(state, messageState, viewModel::onEvent)
        }
    }
}

@Composable
fun Content(state: MainUiState, messageState: MessageUiState, onEvent: (MainEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopRow(state, onEvent)
        Button(modifier = Modifier.fillMaxWidth(), onClick = { onEvent(MainEvent.Subscribe) }) {
            Text(text = "Subscribe")
        }
        WebSocketMessageView(
            instrumentName = state.currentInstrument?.name ?: "symbol",
            messageState = messageState
        )
        Box(Modifier.fillMaxSize()) { Chart(state.chartData, state.chartMin, state.chartMax) }

    }
}

@Composable
fun WebSocketMessageView(instrumentName: String, messageState: MessageUiState) {
    val price = messageState.socketMessage.getPrice().toString()
    val time = messageState.socketMessage.getTime()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(text = instrumentName)
        Text(text = price)
        Text(text = time)
    }
}

@Stable
@Composable
private fun Chart(items: List<Line>, chartMin: Double, chartMax: Double) {
    if (items.isNotEmpty()) {

        LineChart(
            animationDelay = 0,
            modifier = Modifier.fillMaxSize(),
            minValue = chartMin,
            maxValue = chartMax,
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall,
                count = 5,
                position = IndicatorPosition.Horizontal.End,
                padding = 32.dp,
                contentBuilder = { indicator ->
                    "%.5f".format(indicator)
                }),
            popupProperties = PopupProperties(
                textStyle = TextStyle(color = Color.White),
                contentBuilder = { value ->
                    "%.5f".format(value)
                }
            ),
            data = items,
        )
    }
}


@Composable
private fun TopRow(
    state: MainUiState,
    onEvent: (MainEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(

            value = state.currentInstrument?.name ?: "",
            label = { Text("Select symbol") },
            readOnly = true,
            onValueChange = {}
        )
        AppSelectBox(
            options = state.instruments,
            currentValue = state.currentInstrument,
            menuWidth = 0.75f,
            getter = { value ->
                value?.name ?: ""
            },
            onEvent = { value ->
                value?.let {
                    onEvent(MainEvent.OnChangeInstrument(it))
                }
            }
        )
    }
}

@Composable
fun <T> AppSelectBox(
    options: List<T>,
    currentValue: T,
    width: Float = 1f,
    menuWidth: Float = 0.5f,
    getter: (T) -> (String),
    onEvent: (T) -> (Unit),
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .padding(4.dp)
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand")
        }
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(menuWidth),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { value ->
                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    onClick = {
                        expanded = false
                        onEvent(value)
                    },
                    enabled = value != currentValue,
                    text = { Text(getter(value)) }
                )
            }
        }
    }
}