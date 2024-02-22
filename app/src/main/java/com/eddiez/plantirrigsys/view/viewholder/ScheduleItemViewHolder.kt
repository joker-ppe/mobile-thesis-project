package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.LayoutScheduleItemBinding
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import java.util.Locale

class ScheduleItemViewHolder(
    private val binding: LayoutScheduleItemBinding,
    private val viewModel: ScheduleViewModel
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: ScheduleDataModel) {
        binding.tvTitle.text = item.title

        val numberOfDay = item.numberOfDates
        if (numberOfDay != null) {
            binding.tvDays.text = "$numberOfDay"
            if (numberOfDay > 1) {
                binding.tvDaysUnit.text = " days - "
            } else {
                binding.tvDaysUnit.text = " day - "
            }
        }

        val updatedTime = item.updateContentAt
        if (updatedTime != null) {
            val dateTime = convertAndFormatDate(updatedTime)

            binding.tvUpdateAt.text = dateTime
        }

        if (item.isPublic == true) {
            binding.tvStatus.text = "Public"
            binding.tvStatus.setTextColor(binding.root.resources.getColor(android.R.color.holo_green_dark, null))

            binding.iconView.visibility = View.VISIBLE
            binding.iconCopy.visibility = View.VISIBLE
            binding.tvNumberOfViews.visibility = View.VISIBLE
            binding.tvNumberOfCopies.visibility = View.VISIBLE

            binding.tvNumberOfViews.text = item.numberOfViews.toString()
            binding.tvNumberOfCopies.text = item.numberOfCopies.toString()
        } else {
            binding.tvStatus.text = "Private"
            binding.tvStatus.setTextColor(binding.root.resources.getColor(android.R.color.holo_orange_dark, null))

            binding.iconView.visibility = View.GONE
            binding.iconCopy.visibility = View.GONE
            binding.tvNumberOfViews.visibility = View.GONE
            binding.tvNumberOfCopies.visibility = View.GONE
        }

        val imageUrl = item.imageData
        if (!imageUrl.isNullOrEmpty()) {

            Glide.with(binding.root)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSchedule)
        }

        binding.root.setOnClickListener {
            viewModel.currentSchedule.postValue(item)
        }


    }

    private fun convertAndFormatDate(dateString: String): String? {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        originalFormat.timeZone =
            TimeZone.getTimeZone("UTC") // Adjust this if your original date is not in UTC
        val date = originalFormat.parse(dateString)

        val desiredFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        desiredFormat.timeZone = TimeZone.getDefault() // Device's default timezone

        return date?.let { desiredFormat.format(it) }
    }
}