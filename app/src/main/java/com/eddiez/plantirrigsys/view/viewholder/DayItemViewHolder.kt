package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.DateDataModel
import com.eddiez.plantirrigsys.dataModel.SlotInDateDataModel
import com.eddiez.plantirrigsys.databinding.LayoutListDaysInScheduleBinding
import com.eddiez.plantirrigsys.view.adapter.SlotItemAdapter

class DayItemViewHolder(
    private val binding: LayoutListDaysInScheduleBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(item: DateDataModel) {

        binding.tvDate.text = item.date

        setupSlotInDate(item.slots)
    }

    private fun setupSlotInDate(slots: List<SlotInDateDataModel>?) {
        if (slots != null) {
            val adapter = SlotItemAdapter(slots)
            binding.rvSlots.adapter = adapter
        } else {
            binding.rvSlots.adapter = SlotItemAdapter(listOf())
        }
    }
}