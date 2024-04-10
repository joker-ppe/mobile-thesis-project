package com.eddiez.plantirrigsys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eddiez.plantirrigsys.dataModel.ChatMessageDataModel
import com.eddiez.plantirrigsys.databinding.LayoutUserMessageBinding
import com.eddiez.plantirrigsys.view.viewholder.ChatViewHolder
import com.marcinorlowski.fonty.Fonty

class ChatAdapter(
    private val items: MutableList<ChatMessageDataModel>,
    private val photoUrl: String?,
) :
    RecyclerView.Adapter<ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            LayoutUserMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Fonty.setFonts(binding.root)

        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(items[position], photoUrl)
    }

    fun addChatMessage(chatMessage: ChatMessageDataModel) {
        this.items.add(chatMessage)
        notifyItemInserted(items.size - 1)
    }

    fun updateBotResponse(chatMessage: ChatMessageDataModel) {
        this.items[items.size - 1] = chatMessage
        notifyItemChanged(items.size - 1)
    }

    override fun getItemCount() = items.size
}