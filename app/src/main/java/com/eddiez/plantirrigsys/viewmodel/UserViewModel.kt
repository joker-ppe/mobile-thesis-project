package com.eddiez.plantirrigsys.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.datamodel.LoginDataModel
import com.eddiez.plantirrigsys.datamodel.UserDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

//    val data = liveData {
//        emit(dataRepository.fetchData())
//    }
    val userData = MutableLiveData<UserDataModel>()
    val registrationResponse = MutableLiveData<UserDataModel>()
    val loginResponse = MutableLiveData<LoginDataModel>()
    val errorMessage = MutableLiveData<String>()

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
                dataRepository.login(LoginDataModel(userName = data.userName, password = data.password))
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                loginResponse.postValue(response.body())
            } else {
                // Handle API error response
//                errorMessage.postValue(
//                    "Error: ${response.code()} - ${
//                        response.errorBody()?.string()
//                    }"
//                )

                if (response.code() == 403) {
                    if (response.errorBody()?.string()?.toString()?.contains("User not found") == true) {
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
}