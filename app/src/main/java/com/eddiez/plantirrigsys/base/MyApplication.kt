package com.eddiez.plantirrigsys.base

import android.app.Application
import com.eddiez.plantirrigsys.R
import com.google.android.libraries.places.api.Places
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

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

//        Fonty
//            .context(this)
//            .normalTypeface("rubik_italic.ttf")
//            .italicTypeface("rubik_italic.ttf")
//            .boldTypeface("rubik_bold.ttf")
//            .build()
    }
}