package com.eddiez.plantirrigsys.datamodel

data class UserDataModel (
    val id: Int? = null,
    val userName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val longitude: Float? = null,
    val latitude: Float? = null,
    val photoUrl: String? = null,
)