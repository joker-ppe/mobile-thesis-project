package com.eddiez.plantirrigsys.datamodel

data class ScheduleDataModel(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val plantName: String? = null,
    val imageData: String? = null,
    val numberOfViews: Int? = null,
    val numberOfCopies: Int? = null,
    val isPublic: Boolean? = null,
    val isActive: Boolean? = null,
    val createAt: String? = null,
    val updateAt: String? = null,
    val days: List<DayDataModel>? = null,

    val moistureThreshold: Float? = null,
    val temperatureThreshold: Float? = null,
    val ecThreshold: Float? = null,
    val pHThreshold: Float? = null,
    val nThreshold: Float? = null,
    val pThreshold: Float? = null,
    val kThreshold: Float? = null,
)
