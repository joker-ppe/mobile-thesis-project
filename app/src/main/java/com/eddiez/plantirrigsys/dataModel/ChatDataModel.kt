package com.eddiez.plantirrigsys.dataModel

import com.google.gson.annotations.SerializedName

data class ChatDataModel(
    val query: String?,
    val result: String?,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("chat_history")
    val chatHistory: List<List<Any>>?,
)
