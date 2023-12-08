package com.eddiez.plantirrigsys.base

import android.app.Application
import com.marcinorlowski.fonty.Fonty

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Fonty
            .context(this)
            .normalTypeface("kollektif.ttf")
            .italicTypeface("kollektif_italic.ttf")
            .boldTypeface("kollektif_bold.ttf")
            .build()
    }
}