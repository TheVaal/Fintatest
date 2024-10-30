package com.vaalzebub.fintatechtest.presentation

import com.vaalzebub.fintatechtest.domain.model.ItemModel

sealed class MainEvent {
    data class OnChangeInstrument(val item: ItemModel):MainEvent()
    data object OnConnected : MainEvent()
    data object OnDisconnected : MainEvent()
    data class OnReceive(val message: String) : MainEvent()
    data object UpdateInstruments:MainEvent()
    data object Subscribe:MainEvent()
}