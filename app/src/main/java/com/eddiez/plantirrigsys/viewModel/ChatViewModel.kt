package com.eddiez.plantirrigsys.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.dataModel.ChatDataModel
import com.eddiez.plantirrigsys.retrofit.apiChat.DataChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dataRepository: DataChatRepository,
) : ViewModel() {

    val responseFull = MutableLiveData<ChatDataModel?>()
    val responseResult = MutableLiveData<String?>()
    fun sendChatMessage(data: ChatDataModel) = viewModelScope.launch {
        try {
            val response = dataRepository.sendChatMessage(data)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                val responseBody = response.body() as ChatDataModel
                responseResult.postValue(responseBody.result)
            } else {
                // Handle API error response
                responseResult.postValue(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHistoryChat(sessionId: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getHistoryChat(sessionId)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                responseFull.postValue(response.body())
            } else {
                // Handle API error response
                responseFull.postValue(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}