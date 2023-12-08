package com.eddiez.plantirrigsys.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eddiez.plantirrigsys.R
import com.marcinorlowski.fonty.Fonty

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this)
    }
}