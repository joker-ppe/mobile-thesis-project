package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityExploreScheduleBinding
import com.eddiez.plantirrigsys.view.fragment.ListScheduleFragment
import com.eddiez.plantirrigsys.view.fragment.MapScheduleFragment
import com.google.android.material.tabs.TabLayout

class ExploreScheduleActivity : BaseActivity() {
    private lateinit var binding: ActivityExploreScheduleBinding
    private var listScheduleFragment: ListScheduleFragment? = null
    private var mapScheduleFragment: MapScheduleFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // setup toolbar
        setupTab()

        // Set default fragment at startup
        if (savedInstanceState == null) {
            if (listScheduleFragment == null) {
                listScheduleFragment = ListScheduleFragment.newInstance()
            }
            switchFragment(listScheduleFragment)
        }
    }

    private fun setupTab() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var selectedFragment: Fragment? = null
                // Handle tab select
                when (tab?.position) {
                    0 -> {
                        if (listScheduleFragment == null) {
                            listScheduleFragment = ListScheduleFragment.newInstance()
                        }
                        selectedFragment = listScheduleFragment
                    }
                    1 -> {
                        if (mapScheduleFragment == null) {
                            mapScheduleFragment = MapScheduleFragment.newInstance()
                        }
                        selectedFragment = mapScheduleFragment
                    }
                }

                switchFragment(selectedFragment)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

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