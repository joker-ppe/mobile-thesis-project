package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.databinding.LayoutScheduleItemBinding
import com.eddiez.plantirrigsys.datamodel.ScheduleDataModel
import java.util.Locale

class ScheduleItemViewHolder(private val binding: LayoutScheduleItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(item: ScheduleDataModel) {
        binding.tvTitle.text = item.title

        val numberOfDay = item.days?.size
        if (numberOfDay != null) {
            binding.tvDays.text = "$numberOfDay"
            if (numberOfDay > 1) {
                binding.tvDaysUnit.text = " days"
            } else {
                binding.tvDaysUnit.text = " day"
            }
        }

        val updatedTime = item.updateAt
        if (updatedTime != null) {
            val dateTime = convertAndFormatDate(updatedTime)

            binding.tvUpdateAt.text = dateTime
        }
    }

    private fun convertAndFormatDate(dateString: String): String? {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        originalFormat.timeZone = TimeZone.getTimeZone("UTC") // Adjust this if your original date is not in UTC
        val date = originalFormat.parse(dateString)

        val desiredFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        desiredFormat.timeZone = TimeZone.getDefault() // Device's default timezone

        return date?.let { desiredFormat.format(it) }
    }
}