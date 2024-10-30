package com.vaalzebub.fintatechtest.data.source

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class WebSocketMessage(
    val instrumentId: String = "",
    val provider: String = "",
    val type: String = "",
    val ask: BaseKind? = null,
    val last: BaseKind? = null,
    val bid: BaseKind? = null,
){
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    fun getPrice(): Double{
        ask?.let{
            return it.price
        }
        last?.let{
            return it.price
        }
        bid?.let{
            return it.price
        }
        return 0.0
    }

    fun getTime(): String{
        ask?.let{
            return formatTime(it.timestamp)
        }
        last?.let{
            return formatTime(it.timestamp)
        }
        bid?.let{
            return formatTime(it.timestamp)
        }
        return LocalDateTime.now().format(formatter)
    }

    private fun formatTime(timestamp: String):String{
        val dateTime = OffsetDateTime.parse(
            timestamp,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
        ).toLocalDateTime()

        return dateTime.format(formatter)
    }
}

class BaseKind(
    val price: Double,
    val timestamp: String
)