package com.eddiez.plantirrigsys.retrofit.apiChat

import com.eddiez.plantirrigsys.dataModel.ChatDataModel
import javax.inject.Inject
import javax.inject.Named

class DataChatRepository @Inject constructor(@Named("ProvideApiChatService") private val apiService: ApiChatService) {
    suspend fun sendChatMessage(data: ChatDataModel) = apiService.sendChatMessage(data)
    suspend fun getHistoryChat(sessionId: String) = apiService.getChatHistory(sessionId)
}