package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.LayoutScheduleItemBinding
import com.eddiez.plantirrigsys.view.viewholder.ScheduleItemViewHolder
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import com.marcinorlowski.fonty.Fonty

class ScheduleItemAdapter(
    private val items: List<ScheduleDataModel>,
    private val viewModel: ScheduleViewModel
) :
    RecyclerView.Adapter<ScheduleItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemViewHolder {
        val binding =
            LayoutScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        Fonty.setFonts(binding.root)

        return ScheduleItemViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ScheduleItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}