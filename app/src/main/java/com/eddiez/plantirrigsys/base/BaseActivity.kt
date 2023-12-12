package com.eddiez.plantirrigsys.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this)
    }
}