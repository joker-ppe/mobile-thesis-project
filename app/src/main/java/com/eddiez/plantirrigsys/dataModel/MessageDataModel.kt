package com.eddiez.plantirrigsys.dataModel

data class MessageDataModel(
    val userId: Int?,
    val scheduleId: Int?,
    val dayIndex: Int?,
    val day: String?,
    val completeDays: Int?,
    val totalDays: Int?,
    val slotIndex: Int?,
    val startTime: String?,
    val endTime: String?,
    val slotStatus: String?,
    val completeSlots: Int?,
    val completeRate: Int?,
    val totalSlots: Int?,
    val allowAction: Boolean?,
)