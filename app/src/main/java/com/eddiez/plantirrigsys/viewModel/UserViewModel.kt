package com.eddiez.plantirrigsys.viewModel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.dataModel.LoginDataModel
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.DataStoreHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    val gson = Gson()

    val userData = MutableLiveData<UserDataModel?>()
    val registrationResponse = MutableLiveData<UserDataModel>()
    val loginResponse = MutableLiveData<LoginDataModel>()
    val errorMessage = MutableLiveData<String>()
    val accessTokenExpired = MutableLiveData<Boolean>()

    val encryptedData = MutableLiveData<String?>()
    val decryptedData = MutableLiveData<String?>()

    val accessToken = dataStoreHelper.readData(AppConstants.ACCESS_TOKEN, "").asLiveData()
    val userDataJson = dataStoreHelper.readData(AppConstants.USER_DATA, "").asLiveData()

    val fullName = MediatorLiveData<String>().apply {
        addSource(userDataJson) { userJson ->
            value = combineNames(userJson)
        }
    }

    private fun combineNames(userJson: String?): String {
        val user = gson.fromJson(userJson, UserDataModel::class.java)
        return "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
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

                val gson = Gson()
                val dataJson = gson.toJson(data)

                dataStoreHelper.saveData(AppConstants.USER_DATA, dataJson)
            }
        }
    }

    fun clearDataLocal(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreHelper.clearData()
            onComplete() // Called after clearData() completes
        }
    }

    fun encryptData(accessToken: String, textData: String) = viewModelScope.launch {
        try {
            val response = dataRepository.encrypt(accessToken, textData)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Encrypted Data", response.body().toString())
                encryptedData.postValue(response.body()!!.encryptedData)
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

    fun decryptData(accessToken: String, encryptedData: String) = viewModelScope.launch {
        try {
            val response = dataRepository.decrypt(accessToken, encryptedData)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Decrypted Data", response.body().toString())
                decryptedData.postValue(response.body()!!.decryptedData)
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
}