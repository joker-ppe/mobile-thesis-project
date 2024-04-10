package com.eddiez.plantirrigsys.view.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.ChatDataModel
import com.eddiez.plantirrigsys.dataModel.ChatMessageDataModel
import com.eddiez.plantirrigsys.databinding.ActivityChatBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.Utils
import com.eddiez.plantirrigsys.view.adapter.ChatAdapter
import com.eddiez.plantirrigsys.viewModel.ChatViewModel

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var adapter: ChatAdapter
    private lateinit var userEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        innitView()
        observeData()

        // Request focus
        binding.edtMessage.requestFocus()

        // Show keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtMessage, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun observeData() {
        // Observe data
        userViewModel.userData.observe(this) {
            if (it != null) {
                val layoutManager = LinearLayoutManager(this)
                layoutManager.setStackFromEnd(true)
                binding.rvChat.layoutManager = layoutManager
//                binding.rvChat.adapter = adapter

                chatViewModel.responseFull.observe(this) { chatViewModel ->
                    adapter = if (chatViewModel != null) {
                        // Do something
                        // parse data
                        ChatAdapter(
                            Utils.parseHistoryChat(chatViewModel.chatHistory!!),
                            it.photoUrl
                        )
                        //                        binding.rvChat.adapter = adapter
                    } else {
                        ChatAdapter(mutableListOf(), it.photoUrl)
                    }
                    binding.rvChat.adapter = adapter
                    // Scroll to bottom
                    if (adapter.itemCount > 0) {
                        binding.rvChat.smoothScrollToPosition(adapter.itemCount - 1)
                    }



                    binding.bgMessage.visibility = View.VISIBLE
                    userEmail = it.email!!
                }

                chatViewModel.responseResult.observe(this) { result ->
                    if (result != null) {
                        // Do something
                        adapter.updateBotResponse(ChatMessageDataModel(result, false))
                    } else {
                        adapter.updateBotResponse(ChatMessageDataModel("Xảy ra lỗi khi phản hồi. Xin vui lòng thử lại", false))
                    }

                    binding.imgSend.isEnabled = true
                }

                chatViewModel.getHistoryChat(it.email!!)
            } else {
                // Do something
                binding.bgMessage.visibility = View.GONE
            }
        }

        userViewModel.accessToken.observe(this) {
            if (it != null) {
                userViewModel.getProfile(it)
            } else {
                Toast.makeText(this, "Access token is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun innitView() {

        binding.bgMessage.visibility = View.GONE

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.imgSend.setOnClickListener {
            // Send message
            val message = binding.edtMessage.text.toString()
            if (message.isNotEmpty()) {
                adapter.addChatMessage(ChatMessageDataModel(message, true))

                // Scroll to bottom
                binding.rvChat.smoothScrollToPosition(adapter.itemCount - 1)

                Handler(Looper.getMainLooper()).postDelayed({
                    // add loading
                    adapter.addChatMessage(ChatMessageDataModel(AppConstants.BOT_THINKING, false))

                    // Scroll to bottom
                    binding.rvChat.smoothScrollToPosition(adapter.itemCount - 1)
                    // send data to ask
                    chatViewModel.sendChatMessage(
                        ChatDataModel(
                            message,
                            null,
                            userEmail,
                            null
                        )
                    )
                }, 500) // Delay of 0.5 second

                binding.edtMessage.setText("")

                binding.imgSend.isEnabled = false
            }
        }


    }
}