package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityMainBinding
import com.eddiez.plantirrigsys.view.fragment.HomeFragment
import com.eddiez.plantirrigsys.view.fragment.ProfileFragment
import com.eddiez.plantirrigsys.view.fragment.ScheduleFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var homeFragment: HomeFragment? = null
    private var scheduleFragment: ScheduleFragment? = null
    private var profileFragment: ProfileFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.itemHome -> {
                    if (homeFragment == null) {
                        homeFragment = HomeFragment.newInstance()
                    }
                    selectedFragment = homeFragment
                }

                R.id.itemSchedule -> {
                    if (scheduleFragment == null) {
                        scheduleFragment = ScheduleFragment.newInstance()
                    }
                    selectedFragment = scheduleFragment
                }

                R.id.itemProfile -> {
                    if (profileFragment == null) {
                        profileFragment = ProfileFragment.newInstance()
                    }
                    selectedFragment = profileFragment
                }
            }

            switchFragment(selectedFragment)

            true  // Return true to indicate the click event is handled
        }

        // Set default fragment at startup
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId =
                R.id.itemSchedule // Replace with default fragment's associated ID
        }
    }

    private fun switchFragment(newFragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment.isVisible) {
                transaction.hide(fragment)
            }
        }

        newFragment?.let { fragment ->
            if (fragment.isAdded) {
                transaction.show(fragment)
            } else {
                transaction.add(R.id.fragment_container, fragment)
            }
        }

        transaction.commit()
    }

}