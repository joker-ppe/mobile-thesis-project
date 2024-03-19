package com.eddiez.plantirrigsys.viewModel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eddiez.plantirrigsys.dataModel.CabinetDataModel
import com.eddiez.plantirrigsys.dataModel.LoginDataModel
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import com.eddiez.plantirrigsys.retrofit.DataRepository
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.DataStoreHelper
import com.eddiez.plantirrigsys.utilities.OnMessageReceived
import com.eddiez.plantirrigsys.utilities.RabbitMqClient
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    private val gson = Gson()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        // Handle your exception here
        Log.e("HomeFragment", "Caught $exception")
    }

    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    val userData = MutableLiveData<UserDataModel?>()
    val registrationResponse = MutableLiveData<UserDataModel>()
    val loginResponse = MutableLiveData<LoginDataModel>()
    val errorMessage = MutableLiveData<String>()
    val accessTokenExpired = MutableLiveData<Boolean>()

    val encryptedData = MutableLiveData<String?>()
    val decryptedData = MutableLiveData<String?>()

    val messageReceived = MutableLiveData<String>()
    val temperatureReceived = MutableLiveData<Float?>()
    val humidityReceived = MutableLiveData<Float?>()
    val lightReceived = MutableLiveData<Float?>()
    val actionReceived = MutableLiveData<String>()

    val accessToken = MediatorLiveData<String>().apply {
        addSource(
            dataStoreHelper.readData(AppConstants.ACCESS_TOKEN, "").asLiveData()
        ) { accessToken ->
            value = accessToken
        }
    }
//    val userDataJson = dataStoreHelper.readData(AppConstants.USER_DATA, "").asLiveData()

//    val fullName = MediatorLiveData<String>().apply {
//        addSource(userDataJson) { userJson ->
//            value = combineNames(userJson)
//        }
//    }

    val connectedCabinet = MutableLiveData<CabinetDataModel?>()

    private fun parseCabinetData(cabinetJson: String?): CabinetDataModel {
        return gson.fromJson(cabinetJson, CabinetDataModel::class.java)
    }

    private fun combineNames(userJson: String?): String {
        val user = gson.fromJson(userJson, UserDataModel::class.java)
        return if (user != null) {
            "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
        } else {
            ""
        }
    }

    fun register(apiKey: String, data: UserDataModel) = viewModelScope.launch {
        try {
            val response = dataRepository.register(apiKey, data)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                registrationResponse.postValue(response.body())
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "${
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

    fun login(apiKey: String, data: UserDataModel) = viewModelScope.launch {
        try {
            val response =
                dataRepository.login(
                    apiKey,
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
                        errorMessage.postValue(AppConstants.ERROR_LOGIN)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle other exceptions like network errors, etc.
            // Post other exceptions like network errors
            errorMessage.postValue(e.message ?: "An unknown error occurred")
        }
    }

    fun getProfile(accessToken: String) = viewModelScope.launch {
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
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    fun getCabinet(apiKey: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.getCabinet(apiKey, id)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Cabinet Data", response.body().toString())
                connectedCabinet.postValue(response.body())
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "${
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

    fun consumeTemperature(cabinetId: Int, userId: Int, deviceId: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.temperature"
            val queueName = "user.$userId.$deviceId.temperature"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.consumeMessage(exchangeName, queueName, object : OnMessageReceived {
                override fun onMessageReceived(message: String) {
                    temperatureReceived.postValue(message.toFloatOrNull()) // Update LiveData with the new message
                }
            })
        }
    }

    fun consumeHumidity(cabinetId: Int, userId: Int, deviceId: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.humidity"
            val queueName = "user.$userId.$deviceId.humidity"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.consumeMessage(exchangeName, queueName, object : OnMessageReceived {
                override fun onMessageReceived(message: String) {
                    humidityReceived.postValue(message.toFloatOrNull()) // Update LiveData with the new message
                }
            })
        }
    }

    fun consumeLight(cabinetId: Int, userId: Int, deviceId: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.light"
            val queueName = "user.$userId.$deviceId.light"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.consumeMessage(exchangeName, queueName, object : OnMessageReceived {
                override fun onMessageReceived(message: String) {
                    lightReceived.postValue(message.toFloatOrNull()) // Update LiveData with the new message
                }
            })
        }
    }

    fun consumeMessage(cabinetId: Int, userId: Int, deviceId: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.messages"
            val queueName = "user.$userId.$deviceId.messages"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.consumeMessage(exchangeName, queueName, object : OnMessageReceived {
                override fun onMessageReceived(message: String) {
                    messageReceived.postValue(message) // Update LiveData with the new message
                }
            })
        }
    }

    fun sendActionToCabinet(cabinetId: Int, message: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.action"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.sendMessage(exchangeName, message)
        }
    }

    fun consumeAction(cabinetId: Int, userId: Int, deviceId: String) {
        scope.launch {
            val exchangeName = "cabinet.$cabinetId.action.reply"
            val queueName = "user.$userId.$deviceId.action.reply"
            val rabbitMqClient = RabbitMqClient()
            runBlocking { rabbitMqClient.connect() }
            rabbitMqClient.consumeMessage(exchangeName, queueName, object : OnMessageReceived {
                override fun onMessageReceived(message: String) {
                    actionReceived.postValue(message) // Update LiveData with the new message
                }
            })
        }
    }

    fun connectCabinet(accessToken: String, id: Int) = viewModelScope.launch {
        try {
            val response = dataRepository.connectCabinet(accessToken, id)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Connect Cabinet", response.body().toString())
                connectedCabinet.postValue(response.body())
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "${
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

    fun removeCabinet(accessToken: String) = viewModelScope.launch {
        try {
            val response = dataRepository.removeCabinet(accessToken)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Remove Cabinet", response.body().toString())
                connectedCabinet.postValue(null)
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "${
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

    fun saveData(data: LoginDataModel?) {
        viewModelScope.launch {
            if (data != null) {
                dataStoreHelper.saveData(AppConstants.ACCESS_TOKEN, data.accessToken)

//                accessToken.postValue(data.accessToken)
//                val gson = Gson()
//                val dataJson = gson.toJson(data)
//
//                dataStoreHelper.saveData(AppConstants.USER_DATA, dataJson)
            }
        }
    }

//    fun saveCabinetData(data: CabinetDataModel?) {
//        viewModelScope.launch {
//            if (data != null) {
//                dataStoreHelper.saveData(AppConstants.CABINET_ID, data.id)
//            } else {
//                dataStoreHelper.saveData(AppConstants.CABINET_ID, 0)
//            }
//        }
//    }
//
//    fun removeCabinetData() {
//        viewModelScope.launch {
//            dataStoreHelper.saveData(AppConstants.CABINET_ID, 0)
//        }
//    }

    fun clearDataLocal(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreHelper.clearData()
            onComplete() // Called after clearData() completes
        }
    }

    fun encryptData(accessToken: String, apiKey: String, textData: String) = viewModelScope.launch {
        try {
            val response = dataRepository.encrypt(accessToken, apiKey, textData)
            if (response.isSuccessful && response.body() != null) {
                // Handle successful response
                Log.d("Encrypted Data", response.body().toString())
                encryptedData.postValue(response.body()!!.encryptedData)
            } else {
                // Handle API error response
                errorMessage.postValue(
                    "${
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

    fun decryptData(accessToken: String, apiKey: String, encryptedData: String) =
        viewModelScope.launch {
            try {
                val response = dataRepository.decrypt(accessToken, apiKey, encryptedData)
                if (response.isSuccessful && response.body() != null) {
                    // Handle successful response
                    Log.d("Decrypted Data", response.body().toString())
                    decryptedData.postValue(response.body()!!.decryptedData)
                } else {
                    // Handle API error response
                    errorMessage.postValue(
                        AppConstants.ERROR_QR_CODE
                    )
//                    errorMessage.postValue(
//                        "Error: ${response.code()} - ${
//                            response.errorBody()?.string()
//                        }"
//                    )
                }
            } catch (e: Exception) {
                // Handle other exceptions like network errors, etc.
                // Post other exceptions like network errors
                errorMessage.postValue(e.message ?: "An unknown error occurred")
            }
        }
}