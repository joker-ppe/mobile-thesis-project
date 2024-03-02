package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class DateDataModel(
    val index: Int? = null,
    val date: String? = null,
    val slots: List<SlotInDateDataModel>? = null
) :Serializable