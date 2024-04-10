package com.eddiez.plantirrigsys.retrofit.apiSystem

import com.eddiez.plantirrigsys.dataModel.CabinetDataModel
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
    suspend fun register(@Query("apiKey") apiKey: String, @Body data: UserDataModel): Response<UserDataModel>

    @POST("auth/login")
    suspend fun login(@Query("apiKey") apiKey: String, @Body data: LoginDataModel): Response<LoginDataModel>

    @GET("users/profile")
    suspend fun getProfile(@Header("Authorization") authHeader: String): Response<UserDataModel>

    @GET("devices/id/{id}")
    suspend fun getCabinet(@Path("id") id: Int, @Query("apiKey") apiKey: String): Response<CabinetDataModel>

    @DELETE("users/cabinet")
    suspend fun removeCabinet(@Header("Authorization") authHeader: String): Response<UserDataModel>

    @GET("users/encrypt")
    suspend fun encrypt(
        @Header("Authorization") authHeader: String,
        @Query("apiKey") apiKey: String,
        @Query("textData") textData: String
    ): Response<RSADataModel>

    @GET("users/decrypt")
    suspend fun decrypt(
        @Header("Authorization") authHeader: String,
        @Query("apiKey") apiKey: String,
        @Query("encryptedData") encryptedData: String
    ): Response<RSADataModel>

    @PATCH("users/cabinet/{id}")
    suspend fun connectCabinet(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int,
        @Query("accessToken") accessToken: String,
    ): Response<CabinetDataModel>

    @GET("schedules")
    suspend fun getSchedulesOfUser(@Header("Authorization") authHeader: String): Response<List<ScheduleDataModel>>

    @GET("schedules/inUse")
    suspend fun getScheduleInUse(@Header("Authorization") authHeader: String): Response<ScheduleDataModel>

    @PATCH("schedules/inUse/{id}")
    suspend fun setScheduleInUse(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<ScheduleDataModel>

    @DELETE("schedules/inUse")
    suspend fun removeScheduleInUse(@Header("Authorization") authHeader: String): Response<UserDataModel>

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