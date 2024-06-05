package com.eddiez.plantirrigsys.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.dataModel.NotificationDataModel
import com.eddiez.plantirrigsys.retrofit.apiSystem.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val notifications = MutableLiveData<List<NotificationDataModel>?>()

    fun getNotifications(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getNotification(accessToken)
            if (response.isSuccessful && response.body() != null) {
                notifications.postValue(response.body())
            } else {
                // Handle errors
                response.errorBody()?.string()?.let { Log.e("Error", it) }

//                if (response.code() == 401) {
//                    accessTokenExpired.postValue(true)
//                }
                notifications.postValue(null)
            }
        }catch (e: Exception) {
            Log.e("Error",e.message.toString())
        }
    }
}