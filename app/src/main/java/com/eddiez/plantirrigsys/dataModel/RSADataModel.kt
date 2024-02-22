package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class RSADataModel(
    val encryptedData: String? = null,
    val decryptedData: String? = null
) : Serializable
