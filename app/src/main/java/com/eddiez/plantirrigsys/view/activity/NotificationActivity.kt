package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityNotificationBinding
import com.eddiez.plantirrigsys.view.adapter.NotificationAdapter
import com.eddiez.plantirrigsys.viewModel.NotificationViewModel

class NotificationActivity : BaseActivity() {

    private val notificationViewHolder: NotificationViewModel by viewModels()

    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        notificationViewHolder.notifications.observe(this) { notifications ->
            if (notifications != null) {
                val adapter = NotificationAdapter(notifications)
                binding.rvNotification.adapter = adapter

                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.rvNotification.layoutManager = LinearLayoutManager(this)

        binding.swipeRefreshLayout.isRefreshing = true

        userViewModel.accessToken.observe(this) {
            if (it.isNotEmpty()) {
                notificationViewHolder.getNotifications(it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Logic to refresh the RecyclerView goes here
            userViewModel.accessToken.value?.let {
                notificationViewHolder.getNotifications(it)
            }
        }
    }
}