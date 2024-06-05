package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.NotificationDataModel
import com.eddiez.plantirrigsys.databinding.LayoutUserNotificationBinding
import com.eddiez.plantirrigsys.view.viewholder.NotificationViewHolder
import com.marcinorlowski.fonty.Fonty

class NotificationAdapter(
    private val items: List<NotificationDataModel>,
) :
    RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            LayoutUserNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Fonty.setFonts(binding.root)

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}