package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class SlotDataModel(
    val id: Int? = null,
    val startTime: String? = null,
    val endTime: String? = null,
): Serializable
