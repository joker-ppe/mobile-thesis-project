package com.eddiez.plantirrigsys.datamodel

data class DayDataModel(
    val id: Int? = null,
    val title: String? = null,
    val days: List<SlotDataModel>? = null
)
