package com.eddiez.plantirrigsys.retrofit

import com.eddiez.plantirrigsys.datamodel.LoginDataModel
import com.eddiez.plantirrigsys.datamodel.UserDataModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body data: UserDataModel): Response<UserDataModel>

    @POST("auth/login")
    suspend fun login(@Body data: LoginDataModel): Response<LoginDataModel>
}