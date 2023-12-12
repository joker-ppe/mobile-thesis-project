package com.eddiez.plantirrigsys.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.datamodel.LoginDataModel
import com.eddiez.plantirrigsys.datamodel.UserDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    val userData = MutableLiveData<UserDataModel?>()
    val registrationResponse = MutableLiveData<UserDataModel>()
    val loginResponse = MutableLiveData<LoginDataModel>()
    val errorMessage = MutableLiveData<String>()
    val accessTokenExpired = MutableLiveData<Boolean>()

    val accessToken = dataStoreHelper.readData(AppConstants.ACCESS_TOKEN, "").asLiveData()
    val userName = dataStoreHelper.readData(AppConstants.USER_NAME, "").asLiveData()
    private val firstName = dataStoreHelper.readData(AppConstants.FIRST_NAME, "").asLiveData()
    private val lastName = dataStoreHelper.readData(AppConstants.LAST_NAME, "").asLiveData()
    val photoUrl = dataStoreHelper.readData(AppConstants.PHOTO_URL, "").asLiveData()

    val fullName = MediatorLiveData<String>().apply {
        addSource(firstName) { first ->
            value = combineNames(first, lastName.value)
        }
        addSource(lastName) { last ->
            value = combineNames(firstName.value, last)
        }
    }

    private fun combineNames(first: String?, last: String?): String {
        return "${first.orEmpty()} ${last.orEmpty()}".trim()
    }

    fun register(data: UserDataModel) = viewModelScope.launch {
        try {
            val response = dataRepository.register(data)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                registrationResponse.postValue(response.body())
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "Error: ${response.code()} - ${
                        response.errorBody()?.string()
                    }"
                )
            }
        } catch (e: Exception) {
            // Handle other exceptions like network errors, etc.
            // Post other exceptions like network errors
            errorMessage.postValue(e.message ?: "An unknown error occurred")
        }
    }

    fun login(data: UserDataModel) = viewModelScope.launch {
        try {
            val response =
                dataRepository.login(
                    LoginDataModel(
                        userName = data.userName,
                        password = data.password
                    )
                )
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                loginResponse.postValue(response.body())
            } else {
                // Handle API error response
                if (response.code() == 403) {
                    if (response.errorBody()?.string()?.toString()
                            ?.contains("User not found") == true
                    ) {
                        errorMessage.postValue("Login: User not found")
                    }
                }
            }
        } catch (e: Exception) {
            // Handle other exceptions like network errors, etc.
            // Post other exceptions like network errors
            errorMessage.postValue(e.message ?: "An unknown error occurred")
        }
    }

    fun getProfile(accessToken: String)= viewModelScope.launch {
        try {
            val response = dataRepository.getProfile(accessToken)
            if (response.isSuccessful && response.body() != null) {
                userData.postValue(response.body())
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

    fun saveData(data: LoginDataModel?) {
        viewModelScope.launch {
            if (data != null) {
                dataStoreHelper.saveData(AppConstants.ACCESS_TOKEN, data.accessToken)
                dataStoreHelper.saveData(AppConstants.USER_NAME, data.userName)
                dataStoreHelper.saveData(AppConstants.FIRST_NAME, data.firstName)
                dataStoreHelper.saveData(AppConstants.LAST_NAME, data.lastName)
                dataStoreHelper.saveData(AppConstants.PHOTO_URL, data.photoUrl)
            }
        }
    }

    fun clearDataLocal(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreHelper.clearData()
            onComplete() // Called after clearData() completes
        }
    }
}