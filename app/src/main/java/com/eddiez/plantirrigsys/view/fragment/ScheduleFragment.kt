package com.eddiez.plantirrigsys.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.Utils
import com.eddiez.plantirrigsys.utilities.Utils.convertAndFormatDate
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

        scheduleViewModel.scheduleInUse.observe(viewLifecycleOwner) {
            if (it != null) {
                setupScheduleInUse(it)
            } else {
                binding.layoutScheduleInUse.root.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private fun setupScheduleInUse(item: ScheduleDataModel) {
        binding.layoutScheduleInUse.root.visibility = View.VISIBLE

        binding.layoutScheduleInUse.tvTitle.text = item.title

        val numberOfDay = item.numberOfDates
        if (numberOfDay != null) {

            // count %
            var numberDateDone = 0
            val currentDate = Utils.getCurrentDateString()

            Log.d("ScheduleFragment", "currentDate: $currentDate")

            item.listDateData!!.find { it.date == currentDate }?.let {
                numberDateDone = it.index!!
            }

            binding.layoutScheduleInUse.tvDays.text = "$numberDateDone/$numberOfDay"
            if (numberOfDay > 1) {
                binding.layoutScheduleInUse.tvDaysUnit.text = " days - "
            } else {
                binding.layoutScheduleInUse.tvDaysUnit.text = " day - "
            }
        }

        val updatedTime = item.updateContentAt
        if (updatedTime != null) {
            val dateTime = convertAndFormatDate(updatedTime)

            binding.layoutScheduleInUse.tvUpdateAt.text = dateTime
        }

        if (item.isPublic == true) {
            binding.layoutScheduleInUse.tvStatus.text = "Public"
            binding.layoutScheduleInUse.tvStatus.setTextColor(binding.root.resources.getColor(android.R.color.holo_green_dark, null))

            binding.layoutScheduleInUse.iconView.visibility = View.VISIBLE
            binding.layoutScheduleInUse.iconCopy.visibility = View.VISIBLE
            binding.layoutScheduleInUse.tvNumberOfViews.visibility = View.VISIBLE
            binding.layoutScheduleInUse.tvNumberOfCopies.visibility = View.VISIBLE

            binding.layoutScheduleInUse.tvNumberOfViews.text = item.numberOfViews.toString()
            binding.layoutScheduleInUse.tvNumberOfCopies.text = item.numberOfCopies.toString()
        } else {
            binding.layoutScheduleInUse.tvStatus.text = "Private"
            binding.layoutScheduleInUse.tvStatus.setTextColor(binding.root.resources.getColor(android.R.color.holo_orange_dark, null))

            binding.layoutScheduleInUse.iconView.visibility = View.GONE
            binding.layoutScheduleInUse.iconCopy.visibility = View.GONE
            binding.layoutScheduleInUse.tvNumberOfViews.visibility = View.GONE
            binding.layoutScheduleInUse.tvNumberOfCopies.visibility = View.GONE
        }

        val imageUrl = item.imageData
        if (!imageUrl.isNullOrEmpty()) {

            Glide.with(binding.root)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.layoutScheduleInUse.imgSchedule)
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), CreateScheduleActivity::class.java)
            intent.putExtra(AppConstants.SCHEDULE, item)
            intent.putExtra(AppConstants.IN_USE, true)
            startActivity(intent)
        }
    }

    private fun refreshData() {
        // Refresh data in your RecyclerView
        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getSchedules(token)

                scheduleViewModel.getScheduleInUse(token)
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