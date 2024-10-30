package com.vaalzebub.fintatechtest.domain.utils

import com.vaalzebub.fintatechtest.presentation.MainEvent

interface WebSocketListener {

    fun onConnected()
    fun onMessage(message: String)
    fun onDisconnected()

}

class WebSocketListenerImpl(
    private val onEvent: (MainEvent) -> Unit

) : WebSocketListener {
    override fun onConnected() {
        onEvent(MainEvent.OnConnected)

    }

    override fun onMessage(message: String) {
        onEvent(MainEvent.OnReceive(message))
    }


    override fun onDisconnected() {
        onEvent(MainEvent.OnDisconnected)
    }

}