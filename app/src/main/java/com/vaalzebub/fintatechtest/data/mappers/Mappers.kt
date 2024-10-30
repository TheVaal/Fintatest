package com.vaalzebub.fintatechtest.data.mappers

import com.vaalzebub.fintatechtest.data.source.HistoryPriceDto
import com.vaalzebub.fintatechtest.data.source.ItemDto
import com.vaalzebub.fintatechtest.domain.model.HistoryPrice
import com.vaalzebub.fintatechtest.domain.model.ItemModel

fun ItemDto.toModel() = ItemModel(
    id = id,
    name = name
)

fun HistoryPriceDto.toModel() = HistoryPrice(
    time = time,
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume
)