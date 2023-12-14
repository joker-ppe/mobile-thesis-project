package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityExploreScheduleBinding

class ExploreScheduleActivity : BaseActivity() {
    private lateinit var binding: ActivityExploreScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}