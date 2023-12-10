package com.eddiez.plantirrigsys.retrofit

import com.eddiez.plantirrigsys.datamodel.LoginDataModel
import com.eddiez.plantirrigsys.datamodel.UserDataModel
import javax.inject.Inject

class DataRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun register(data: UserDataModel) = apiService.register(data)
    suspend fun login(data: LoginDataModel) = apiService.login(data)
}