package com.eddiez.plantirrigsys.retrofit

import com.eddiez.plantirrigsys.dataModel.LoginDataModel
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import javax.inject.Inject

class DataRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun register(apiKey: String, data: UserDataModel) = apiService.register(apiKey, data)
    suspend fun login(apiKey: String, data: LoginDataModel) = apiService.login(apiKey, data)
    suspend fun getProfile(accessToken: String) = apiService.getProfile("Bearer $accessToken")
    suspend fun getCabinet(apiKey: String, id: Int) = apiService.getCabinet(id, apiKey)
    suspend fun removeCabinet(accessToken: String) =
        apiService.removeCabinet("Bearer $accessToken")
    suspend fun encrypt(accessToken: String, apiKey: String, textData: String) =
        apiService.encrypt("Bearer $accessToken", apiKey, textData)

    suspend fun decrypt(accessToken: String, apiKey: String, encryptedData: String) =
        apiService.decrypt("Bearer $accessToken", apiKey, encryptedData)

    suspend fun connectCabinet(accessToken: String, id: Int, topic: String) =
        apiService.connectCabinet("Bearer $accessToken", id, topic, accessToken)

    suspend fun getSchedules(accessToken: String) =
        apiService.getSchedulesOfUser("Bearer $accessToken")

    suspend fun getScheduleInUse(accessToken: String) = apiService.getScheduleInUse("Bearer $accessToken")

    suspend fun createSchedule(accessToken: String, data: ScheduleDataModel) =
        apiService.createSchedule("Bearer $accessToken", data)

    suspend fun updateSchedule(accessToken: String, id: Int, data: ScheduleDataModel) =
        apiService.updateSchedule("Bearer $accessToken", id, data)

    suspend fun deleteSchedule(accessToken: String, id: Int) =
        apiService.deleteSchedule("Bearer $accessToken", id)

    suspend fun getPublicSchedule(accessToken: String) =
        apiService.getPublicSchedule("Bearer $accessToken")

    suspend fun increaseNumberOfViews(accessToken: String, id: Int) =
        apiService.increaseNumberOfViews("Bearer $accessToken", id)

    suspend fun increaseNumberOfCopies(accessToken: String, id: Int) =
        apiService.increaseNumberOfCopies("Bearer $accessToken", id)
}