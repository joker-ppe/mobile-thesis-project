package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.SlotInDateDataModel
import com.eddiez.plantirrigsys.databinding.LayoutListSlotItemBinding
import com.eddiez.plantirrigsys.view.viewholder.SlotItemViewHolder
import com.marcinorlowski.fonty.Fonty

class SlotItemAdapter(
    private val items: List<SlotInDateDataModel>
) :
    RecyclerView.Adapter<SlotItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotItemViewHolder {
        val binding =
            LayoutListSlotItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Fonty.setFonts(binding.root)

        return SlotItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}