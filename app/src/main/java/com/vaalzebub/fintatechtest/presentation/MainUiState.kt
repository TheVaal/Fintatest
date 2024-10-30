package com.vaalzebub.fintatechtest.presentation

import com.vaalzebub.fintatechtest.data.source.WebSocketMessage
import com.vaalzebub.fintatechtest.domain.model.ItemModel
import ir.ehsannarmani.compose_charts.models.Line

data class MainUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val error: String = "",
    val instruments: List<ItemModel> = emptyList(),
    val currentInstrument:ItemModel? = null,
    val chartData: List<Line> = emptyList(),
    val chartMin:Double=0.0,
    val chartMax:Double= 0.0
)

data class MessageUiState(
    val socketMessage:WebSocketMessage = WebSocketMessage(),
    val socketConnected: Boolean = false,

)