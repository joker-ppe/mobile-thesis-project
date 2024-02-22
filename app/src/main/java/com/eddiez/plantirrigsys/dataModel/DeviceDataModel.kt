package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class DeviceDataModel(
    val mqttTopic: String?,
    val uuid: String?,
    val name: String?,
    val macAddress: String?,
    val seriNumber: String?,
    val time: Long?
): Serializable
