package com.eddiez.plantirrigsys.view.viewholder

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.dataModel.ChatMessageDataModel
import com.eddiez.plantirrigsys.databinding.LayoutUserMessageBinding
import com.eddiez.plantirrigsys.utilities.AppConstants

class ChatViewHolder(
    private val binding: LayoutUserMessageBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(item: ChatMessageDataModel, urlPhoto: String?) {

        if (item.isUser) {
            binding.tvUserMessage.text = item.message
            binding.bgUserMessage.visibility = View.VISIBLE
            binding.bgBotMessage.visibility = View.GONE

            if (urlPhoto != null) {
                Glide.with(binding.root)
                    .load(urlPhoto)
                    .placeholder(R.drawable.avatar_ai)
                    .error(R.drawable.avatar_ai)
                    .into(binding.imgAvatarUser)
            }

        } else {

            if (item.message == AppConstants.BOT_THINKING) {
                binding.spinKit.visibility = View.VISIBLE
                binding.tvBotMessage.visibility = View.GONE
            } else {
                binding.tvBotMessage.visibility = View.VISIBLE
                binding.spinKit.visibility = View.GONE
            }

            binding.tvBotMessage.text = item.message
            binding.bgBotMessage.visibility = View.VISIBLE
            binding.bgUserMessage.visibility = View.GONE
        }
    }
}