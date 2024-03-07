package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.SlotInDateDataModel
import com.eddiez.plantirrigsys.databinding.LayoutListSlotItemBinding

class SlotItemViewHolder(
    private val binding: LayoutListSlotItemBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: SlotInDateDataModel) {

       binding.tvIndex.text = item.index.toString()
        binding.tvStart.text = "From: ${item.startTime}"
        binding.tvFinish.text = "To: ${item.endTime}"

        if (item.status == "DONE") {
            binding.imgStatus.visibility = View.VISIBLE
        } else {
            binding.imgStatus.visibility = View.INVISIBLE
        }
    }


}