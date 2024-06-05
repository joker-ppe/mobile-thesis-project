package com.eddiez.plantirrigsys.dataModel

data class NotificationDataModel(
    val id: Int,
    val title: String,
    val body: String,
    val type: Int,
    val userId: String,
    val createdAt: String,
)
