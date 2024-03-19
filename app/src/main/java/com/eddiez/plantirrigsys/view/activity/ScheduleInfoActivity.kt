package com.eddiez.plantirrigsys.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.ActivityScheduleInfoBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.fragment.HistoryCalendarFragment
import com.eddiez.plantirrigsys.view.fragment.HistoryListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

class ScheduleInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityScheduleInfoBinding
    private var historyCalendarFragment: HistoryCalendarFragment? = null
    private var historyListFragment: HistoryListFragment? = null
    private var schedule: ScheduleDataModel? = null
    private var slotIdIrrigating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slotIdIrrigating = intent.getIntExtra(AppConstants.SLOT_ID_IRRIGATING, 0)

        val schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(AppConstants.SCHEDULE, ScheduleDataModel::class.java)
        } else {
            intent.extras?.getSerializable(AppConstants.SCHEDULE) as ScheduleDataModel?
        }

        if (schedule != null) {
            // setup toolbar
            setupTab()

            innitView(schedule)

            this.schedule = schedule

            // Set default fragment at startup
            if (savedInstanceState == null) {
                if (historyCalendarFragment == null) {
                    historyCalendarFragment = HistoryCalendarFragment.newInstance(schedule)
                }
                switchFragment(historyCalendarFragment)
            }
        } else {
            finish()
        }

        observeData()
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
                        if (historyCalendarFragment == null) {
                            historyCalendarFragment = HistoryCalendarFragment.newInstance(schedule!!)
                        }
                        selectedFragment = historyCalendarFragment
                    }
                    1 -> {
                        if (historyListFragment == null) {
                            historyListFragment = HistoryListFragment.newInstance(schedule!!)
                        }
                        selectedFragment = historyListFragment
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

    @SuppressLint("SetTextI18n")
    private fun innitView(schedule: ScheduleDataModel) {
        binding.topAppBar.title = schedule.title

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemRemove -> {

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn ngưng sử dụng lịch này không?")
                        .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                            // Respond to positive button press
                            userViewModel.accessToken.value?.let { token ->
                                if (token.isNotEmpty()) {
                                    scheduleViewModel.removeScheduleInUse(token)
                                }
                            }
                        }
                        .setCancelable(false)
                        .show()
                    true
                }

                else -> false
            }
        }
    }


    private fun observeData() {
        userViewModel.accessToken.observe(this) {}
        scheduleViewModel.scheduleInUse.observe(this) {
            if (it == null) {
                finish()
            }
        }
    }


}