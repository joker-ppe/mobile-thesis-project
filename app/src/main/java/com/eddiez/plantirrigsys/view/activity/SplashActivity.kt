package com.eddiez.plantirrigsys.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent: Intent

        userViewModel.accessToken.observe(this) {
            if (it.isNotEmpty()) {
                Log.d("access_token", it)
                intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                intent = Intent(this@SplashActivity, LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                this@SplashActivity.startActivity(intent)
            }, 3000)
        }


    }
}