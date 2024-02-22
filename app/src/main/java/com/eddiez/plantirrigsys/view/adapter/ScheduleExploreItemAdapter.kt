package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.LayoutExploreScheduleItemBinding
import com.eddiez.plantirrigsys.view.viewholder.ScheduleExploreItemViewHolder
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import com.marcinorlowski.fonty.Fonty

class ScheduleExploreItemAdapter(
    private val items: List<ScheduleDataModel>,
    private val viewModel: ScheduleViewModel
) :
    RecyclerView.Adapter<ScheduleExploreItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleExploreItemViewHolder {
        val binding =
            LayoutExploreScheduleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Fonty.setFonts(binding.root)

        return ScheduleExploreItemViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ScheduleExploreItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}