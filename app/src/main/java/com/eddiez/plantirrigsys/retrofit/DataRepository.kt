package com.eddiez.plantirrigsys.retrofit

import com.eddiez.plantirrigsys.dataModel.LoginDataModel
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import javax.inject.Inject

class DataRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun register(data: UserDataModel) = apiService.register(data)
    suspend fun login(data: LoginDataModel) = apiService.login(data)
    suspend fun getProfile(accessToken: String) = apiService.getProfile("Bearer $accessToken")
    suspend fun encrypt(accessToken: String, textData: String) = apiService.encrypt("Bearer $accessToken", textData)
    suspend fun decrypt(accessToken: String, encryptedData: String) = apiService.decrypt("Bearer $accessToken", encryptedData)
    suspend fun getSchedules(accessToken: String) = apiService.getSchedulesOfUser("Bearer $accessToken")
    suspend fun createSchedule(accessToken: String, data: ScheduleDataModel) = apiService.createSchedule("Bearer $accessToken", data)
    suspend fun updateSchedule(accessToken: String, id: Int, data: ScheduleDataModel) = apiService.updateSchedule("Bearer $accessToken", id, data)
    suspend fun deleteSchedule(accessToken: String, id: Int) = apiService.deleteSchedule("Bearer $accessToken", id)
    suspend fun getPublicSchedule(accessToken: String) = apiService.getPublicSchedule("Bearer $accessToken")
    suspend fun increaseNumberOfViews(accessToken: String, id: Int) = apiService.increaseNumberOfViews("Bearer $accessToken", id)
    suspend fun increaseNumberOfCopies(accessToken: String, id: Int) = apiService.increaseNumberOfCopies("Bearer $accessToken", id)
}