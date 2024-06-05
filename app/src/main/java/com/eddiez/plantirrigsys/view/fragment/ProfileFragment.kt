package com.eddiez.plantirrigsys.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.databinding.FragmentProfileBinding
import com.eddiez.plantirrigsys.view.activity.ChatActivity
import com.eddiez.plantirrigsys.view.activity.EditProfileActivity
import com.eddiez.plantirrigsys.view.activity.LoginActivity
import com.eddiez.plantirrigsys.view.activity.NotificationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bgLogout.setOnClickListener {
            context?.let { context ->
                MaterialAlertDialogBuilder(context)
                    .setTitle(resources.getString(R.string.title_dialog_logout))
                    .setMessage(resources.getString(R.string.supporting_text_dialog_logout))
                    .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                        // Respond to positive button press
                        userViewModel.clearDataLocal {
                            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                                // Optionally add extras to the intent
                                // intent.putExtra("key", value)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }
                    }
                    .show()
            }
        }

        binding.bgChatAI.setOnClickListener {
            val intend = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intend)
        }

        binding.bgNotification.setOnClickListener {
            val intend = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intend)
        }

        binding.bgEditProfile.setOnClickListener {
            val intend = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intend)
        }

        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        userViewModel.userData.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvName.text = (it.firstName + " " + it.lastName).replace("null", "").trim()
                binding.tvEmail.text = it.email

                Glide.with(requireContext())
                    .load(it.photoUrl)
                    .placeholder(R.drawable.avatar_ai)
                    .into(binding.imgAvatar)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.accessToken.value?.let {
            userViewModel.getProfile(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}