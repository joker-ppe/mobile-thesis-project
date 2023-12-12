package com.eddiez.plantirrigsys.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.datamodel.ScheduleDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    val accessToken = dataStoreHelper.readData(AppConstants.ACCESS_TOKEN, "").asLiveData()
    val schedules = MutableLiveData<List<ScheduleDataModel>>()
    val accessTokenExpired = MutableLiveData<Boolean>()
    fun getSchedules(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getSchedules(accessToken)
            if (response.isSuccessful && response.body() != null) {
                schedules.postValue(response.body())
            } else {
                // Handle errors
                response.errorBody()?.string()?.let { Log.e("Error", it) }

                if (response.code() == 401) {
                    accessTokenExpired.postValue(true)
                }
            }
        }catch (e: Exception) {
            Log.e("Error",e.message.toString())
        }
    }
}