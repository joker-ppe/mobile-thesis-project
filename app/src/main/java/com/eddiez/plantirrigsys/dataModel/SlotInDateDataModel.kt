package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class SlotInDateDataModel(
    val index: Int? = null,
    val status: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
): Serializable
