package com.eddiez.plantirrigsys.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.databinding.FragmentScheduleBinding
import com.eddiez.plantirrigsys.datamodel.ScheduleDataModel
import com.eddiez.plantirrigsys.view.activity.LoginActivity
import com.eddiez.plantirrigsys.view.adapter.ScheduleItemAdapter
import com.eddiez.plantirrigsys.viewmodel.ScheduleViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : BaseFragment() {

    private lateinit var binding: FragmentScheduleBinding
    private val viewModel: ScheduleViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.isRefreshing = true

        binding.rvSchedule.layoutManager = LinearLayoutManager(context)

        viewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                viewModel.getSchedules(accessToken = token)
            }
        }

        viewModel.schedules.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.accessTokenExpired.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    // Optionally add extras to the intent
                    // intent.putExtra("key", value)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Logic to refresh the RecyclerView goes here
            refreshData()
        }
    }

    private fun refreshData() {
        // Refresh data in your RecyclerView
        viewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                viewModel.getSchedules(token)
            }
        }
    }

    private fun setupRecyclerView(items: List<ScheduleDataModel>) {
        // When data is loaded, call this to hide the refresh indicator
        binding.swipeRefreshLayout.isRefreshing = false

        binding.rvSchedule.adapter = ScheduleItemAdapter(items)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ScheduleFragment()
    }
}