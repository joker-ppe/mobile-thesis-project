package com.eddiez.plantirrigsys.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eddiez.plantirrigsys.view.activity.LoginActivity
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import com.eddiez.plantirrigsys.viewModel.UserViewModel
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels()
    val scheduleViewModel: ScheduleViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel.accessTokenExpired.observe(this) {
            if (it) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this)
    }
}