package com.eddiez.plantirrigsys.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings.Secure
import com.eddiez.plantirrigsys.R
import com.google.android.libraries.places.api.Places
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    companion object {
        private lateinit var apiKey: String
        fun getApiKey(): String {
            return apiKey
        }

        @SuppressLint("HardwareIds")
        fun getUniqueDeviceId(context: Context): String {
            return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.my_api_map_key))
        }

        Fonty
            .context(this)
            .normalTypeface("rubik.ttf")
            .italicTypeface("rubik_italic.ttf")
            .boldTypeface("rubik_bold.ttf")
            .build()

        apiKey = resources.getString(R.string.api_key)

//        Fonty
//            .context(this)
//            .normalTypeface("rubik_italic.ttf")
//            .italicTypeface("rubik_italic.ttf")
//            .boldTypeface("rubik_bold.ttf")
//            .build()
    }


}