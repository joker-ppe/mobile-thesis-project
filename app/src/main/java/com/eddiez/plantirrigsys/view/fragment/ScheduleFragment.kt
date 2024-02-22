package com.eddiez.plantirrigsys.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.CreateScheduleActivity
import com.eddiez.plantirrigsys.view.activity.ExploreScheduleActivity
import com.eddiez.plantirrigsys.view.adapter.ScheduleItemAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : BaseFragment() {

    private lateinit var binding: FragmentScheduleBinding
    private var listSchedule = listOf<ScheduleDataModel>()

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
        if (binding.fabMenu.isOpened) {
            binding.fabMenu.close(false)
        }
        binding.fabMenu.visibility = View.GONE

        binding.rvSchedule.layoutManager = LinearLayoutManager(context)

        lifecycle.addObserver(scheduleViewModel)

        scheduleViewModel.currentSchedule.observe(viewLifecycleOwner) {
            if (it != null) {
                val intent = Intent(requireContext(), CreateScheduleActivity::class.java)
                intent.putExtra(AppConstants.SCHEDULE, it)
                startActivity(intent)
            }
        }

        userViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getSchedules(accessToken = token)
            } else {

            }
        }

        scheduleViewModel.schedules.observe(viewLifecycleOwner) {
            if (it != null) {
                // check current list
                if (listSchedule.isNotEmpty() && it.isNotEmpty()) {
                    if (listSchedule[0].updateContentAt != it[0].updateContentAt) {
                        listSchedule = it

                        setupRecyclerView(listSchedule)
                    }
                } else {
                    listSchedule = it

                    setupRecyclerView(listSchedule)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false

            if (binding.fabMenu.isOpened) {
                binding.fabMenu.close(false)
            }
            binding.fabMenu.visibility = View.VISIBLE
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.fabMenu.isOpened) {
                binding.fabMenu.close(false)
            }
            binding.fabMenu.visibility = View.GONE
            // Logic to refresh the RecyclerView goes here
            refreshData()
        }

        binding.fabCreate.setOnClickListener {
            val intent = Intent(requireContext(), CreateScheduleActivity::class.java)
            startActivity(intent)

            if (binding.fabMenu.isOpened) {
                binding.fabMenu.close(false)
            }
        }

        binding.fabExplore.setOnClickListener {
            val intent = Intent(requireContext(), ExploreScheduleActivity::class.java)
            startActivity(intent)

            if (binding.fabMenu.isOpened) {
                binding.fabMenu.close(false)
            }
        }
    }

    private fun refreshData() {
        // Refresh data in your RecyclerView
        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getSchedules(token)
            }
        }
    }

    private fun setupRecyclerView(items: List<ScheduleDataModel>) {
        // When data is loaded, call this to hide the refresh indicator
        binding.imgDefault.visibility = View.GONE

        binding.rvSchedule.adapter = ScheduleItemAdapter(items, scheduleViewModel)

        if (items.isEmpty()) {
            binding.bgEmpty.visibility = View.VISIBLE
            binding.mySchedule.visibility = View.INVISIBLE
        } else {
            binding.bgEmpty.visibility = View.GONE
            binding.mySchedule.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        // Logic to refresh the RecyclerView goes here
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Remove the observer when the view is destroyed
        lifecycle.removeObserver(scheduleViewModel)

        scheduleViewModel.newSchedule.removeObservers(viewLifecycleOwner)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ScheduleFragment()
    }
}