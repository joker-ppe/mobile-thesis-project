package com.eddiez.plantirrigsys.viewModel

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel(), DefaultLifecycleObserver {

    val schedules = MutableLiveData<List<ScheduleDataModel>>()
    val schedulesToChoose = MutableLiveData<List<ScheduleDataModel>>()
    val publicSchedules = MutableLiveData<List<ScheduleDataModel>>()
    val currentSchedule = MutableLiveData<ScheduleDataModel>()
    val scheduleInUse = MutableLiveData<ScheduleDataModel?>()
    val newSchedule = MutableLiveData<ScheduleDataModel>()
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

    fun getSchedulesToChoose(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getSchedules(accessToken)
            if (response.isSuccessful && response.body() != null) {
                schedulesToChoose.postValue(response.body())
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

    fun getScheduleInUse(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getScheduleInUse(accessToken)
            if (response.isSuccessful && response.body() != null) {
                scheduleInUse.postValue(response.body())
            } else {
                scheduleInUse.postValue(null)
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

    fun setScheduleInUse(accessToken: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.setScheduleInUse(accessToken, id)
            if (response.isSuccessful && response.body() != null) {
                scheduleInUse.postValue(response.body())
            } else {
                scheduleInUse.postValue(null)
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

    fun removeScheduleInUse(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.removeScheduleInUse(accessToken)
            if (response.isSuccessful && response.body() != null) {
                scheduleInUse.postValue(null)
            } else {
                scheduleInUse.postValue(null)
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

    fun createSchedule(accessToken: String, data: ScheduleDataModel) = viewModelScope.launch {
        try {
            val response = dataRepository.createSchedule(accessToken, data)
            if (response.isSuccessful && response.body() != null) {
                newSchedule.postValue(response.body())
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

    fun updateSchedule(accessToken: String, id: Int, data: ScheduleDataModel) = viewModelScope.launch {
        try {
            val response = dataRepository.updateSchedule(accessToken, id, data)
            if (response.isSuccessful && response.body() != null) {
                newSchedule.postValue(response.body())
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

    fun deleteSchedule(accessToken: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.deleteSchedule(accessToken, id)
            if (response.isSuccessful && response.body() != null) {
                newSchedule.postValue(response.body())
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

    fun getPublicSchedule(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.getPublicSchedule(accessToken)
            if (response.isSuccessful && response.body() != null) {
                publicSchedules.postValue(response.body())
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

    fun increaseNumberOfViews(accessToken: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.increaseNumberOfViews(accessToken, id)
            if (response.isSuccessful && response.body() != null) {
                Log.d("Success increaseNumberOfViews", response.body().toString())
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

    fun increaseNumberOfCopies(accessToken: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.increaseNumberOfCopies(accessToken, id)
            if (response.isSuccessful && response.body() != null) {
                Log.d("Success increaseNumberOfCopies", response.body().toString())
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