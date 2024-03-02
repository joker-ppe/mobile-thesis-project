package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class CabinetDataModel(
    val id: Int?,
    val mqttTopic: String?,
    val uuid: String?,
    val name: String?,
    val description: String?,
    val macAddress: String?,
    val seriNumber: String?, // from qr code
    val serialNumber: String?, // from server
    val systemInfo: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val time: Long?,

    val userId: Int?,
): Serializable
