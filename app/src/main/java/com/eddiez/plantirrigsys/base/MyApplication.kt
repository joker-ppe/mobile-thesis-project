package com.eddiez.plantirrigsys.base

import android.app.Application
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Fonty
            .context(this)
            .normalTypeface("rubik.ttf")
            .italicTypeface("rubik_italic.ttf")
            .boldTypeface("rubik_bold.ttf")
            .build()
    }
}