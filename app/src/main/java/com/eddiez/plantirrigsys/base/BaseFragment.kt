package com.eddiez.plantirrigsys.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eddiez.plantirrigsys.dataModel.ErrorDataModel
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.LoginActivity
import com.eddiez.plantirrigsys.viewModel.ScheduleViewModel
import com.eddiez.plantirrigsys.viewModel.UserViewModel
import com.google.gson.Gson
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    val userViewModel: UserViewModel by activityViewModels()
    val scheduleViewModel: ScheduleViewModel by activityViewModels()
    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this.view as ViewGroup)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleViewModel.accessTokenExpired.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }

        userViewModel.accessTokenExpired.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }

        userViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
//                Log.e(TAG, error.toString())
//                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

                if (error == AppConstants.ERROR_QR_CODE) {
                    Toast.makeText(requireContext(), "Invalid QR code", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val gson = Gson()
                        val errorData = gson.fromJson(error, ErrorDataModel::class.java)
                        if (errorData != null) {
                            Toast.makeText(requireContext(), errorData.message, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}