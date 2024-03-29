package com.eddiez.plantirrigsys.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.dataModel.ErrorDataModel
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.LoginActivity
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import com.eddiez.plantirrigsys.viewModel.UserViewModel
import com.google.gson.Gson
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels()
    val scheduleViewModel: ScheduleViewModel by viewModels()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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

        userViewModel.accessTokenExpired.observe(this) {
            if (it) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }

        userViewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
//                Log.e(TAG, error.toString())
//                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

                if (error == AppConstants.ERROR_QR_CODE) {
                    Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show()
                } else if (error == AppConstants.ERROR_LOGIN) {
                    userViewModel.userData.value?.let {
                        userViewModel.register(
                            MyApplication.getApiKey(),
                            it
                        )
                    }
                } else {
                    try {
                        val gson = Gson()
                        val errorData = gson.fromJson(error, ErrorDataModel::class.java)
                        if (errorData != null) {
                            Toast.makeText(this, errorData.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this)

        // Set the status bar color to white
        window.statusBarColor = resources.getColor(R.color.status_color, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (window != null) {
                if (window.insetsController != null) {
                    window.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}