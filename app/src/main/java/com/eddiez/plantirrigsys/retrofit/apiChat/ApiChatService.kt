package com.eddiez.plantirrigsys.retrofit.apiChat

import com.eddiez.plantirrigsys.dataModel.ChatDataModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiChatService {
    @POST("chat")
    suspend fun sendChatMessage(@Body data: ChatDataModel): Response<ChatDataModel>

    @GET("chat_history/{sessionId}")
    suspend fun getChatHistory(@Path("sessionId") sessionId: String): Response<ChatDataModel>
}