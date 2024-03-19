package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.DateDataModel
import com.eddiez.plantirrigsys.databinding.LayoutListDaysInScheduleBinding
import com.eddiez.plantirrigsys.view.viewholder.DayItemViewHolder
import com.marcinorlowski.fonty.Fonty

class DayItemAdapter(
    private val items: List<DateDataModel>
) :
    RecyclerView.Adapter<DayItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        val binding =
            LayoutListDaysInScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Fonty.setFonts(binding.root)

        return DayItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}