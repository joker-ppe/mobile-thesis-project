package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.dataModel.NotificationDataModel
import com.eddiez.plantirrigsys.databinding.LayoutUserNotificationBinding
import com.eddiez.plantirrigsys.utilities.Utils

class NotificationViewHolder(
    private val binding: LayoutUserNotificationBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(item: NotificationDataModel) {

        if (item.type == 2) {
            binding.imgType.setImageResource(R.drawable.warning)
        } else {
            binding.imgType.setImageResource(R.drawable.bell)
        }

        binding.tvContent.text = "[${item.title}] ${item.body}"

        binding.tvTime.text = Utils.convertAndFormatDate(item.createdAt)
    }
}