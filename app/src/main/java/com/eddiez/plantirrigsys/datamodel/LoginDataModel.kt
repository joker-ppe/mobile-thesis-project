package com.eddiez.plantirrigsys.datamodel

data class LoginDataModel(
    val accessToken: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val email: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null,
)
