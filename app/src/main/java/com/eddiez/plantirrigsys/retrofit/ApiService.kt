package com.eddiez.plantirrigsys.retrofit

import com.eddiez.plantirrigsys.dataModel.LoginDataModel
import com.eddiez.plantirrigsys.dataModel.RSADataModel
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body data: UserDataModel): Response<UserDataModel>

    @POST("auth/login")
    suspend fun login(@Body data: LoginDataModel): Response<LoginDataModel>

    @GET("users/profile")
    suspend fun getProfile(@Header("Authorization") authHeader: String): Response<UserDataModel>

    @GET("users/encrypt")
    suspend fun encrypt(
        @Header("Authorization") authHeader: String,
        @Query("textData") textData: String
    ): Response<RSADataModel>

    @GET("users/decrypt")
    suspend fun decrypt(
        @Header("Authorization") authHeader: String,
        @Query("encryptedData") encryptedData: String
    ): Response<RSADataModel>

    @GET("schedules")
    suspend fun getSchedulesOfUser(@Header("Authorization") authHeader: String): Response<List<ScheduleDataModel>>

    @POST("schedules")
    suspend fun createSchedule(
        @Header("Authorization") authHeader: String,
        @Body data: ScheduleDataModel
    ): Response<ScheduleDataModel>

    @PATCH("schedules/id/{id}")
    suspend fun updateSchedule(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int,
        @Body data: ScheduleDataModel
    ): Response<ScheduleDataModel>

    @DELETE("schedules/id/{id}")
    suspend fun deleteSchedule(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<ScheduleDataModel>

    @GET("schedules/public")
    suspend fun getPublicSchedule(@Header("Authorization") authHeader: String): Response<List<ScheduleDataModel>>

    @PATCH("schedules/id/{id}/numberOfViews")
    suspend fun increaseNumberOfViews(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<ScheduleDataModel>

    @PATCH("schedules/id/{id}/numberOfCopies")
    suspend fun increaseNumberOfCopies(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<ScheduleDataModel>
}