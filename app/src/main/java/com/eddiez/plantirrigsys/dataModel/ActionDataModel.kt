package com.eddiez.plantirrigsys.dataModel

data class ActionDataModel(
    val uuid: String,
    val userId: Int?,
    val scheduleId: Int?,
    val dayIndex: Int?,
    val slotIndex: Int?,
    val slotAction: String?,
    val completeAction: Boolean?,
)
