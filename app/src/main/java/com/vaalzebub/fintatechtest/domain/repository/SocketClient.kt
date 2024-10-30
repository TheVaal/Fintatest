package com.vaalzebub.fintatechtest.domain.repository

import com.vaalzebub.fintatechtest.BuildConfig
import com.vaalzebub.fintatechtest.domain.utils.SessionManager
import com.vaalzebub.fintatechtest.domain.utils.WebSocketListener
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

class SocketClient(private val manager: SessionManager) {
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    suspend fun connect(instrumentId: String, listener: WebSocketListener) {

        client.wss(BuildConfig.API_WSS + manager.fetchAuthToken()) {
            listener.onConnected()
            send(
                Frame.Text(
                    "{\n" +
                            "  \"type\": \"l1-subscription\",\n" +
                            "  \"id\": \"1\",\n" +
                            "  \"instrumentId\": \"${instrumentId}\",\n" +
                            "  \"provider\": \"simulation\",\n" +
                            "  \"subscribe\": true,\n" +
                            "  \"kinds\": [\n" +
                            "    \"ask\",\n" +
                            "    \"bid\",\n" +
                            "    \"last\"\n" +
                            "  ]\n" +
                            "}"
                )
            )
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        listener.onMessage(frame.readText())
                    }
                }
            } catch (e: Exception) {
                listener.onDisconnected()
            }
        }
    }

}