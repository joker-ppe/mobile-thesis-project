package com.eddiez.plantirrigsys.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentHistoryListBinding
import com.eddiez.plantirrigsys.utilities.Utils
import com.eddiez.plantirrigsys.view.adapter.DayItemAdapter

class HistoryListFragment : BaseFragment() {

    private lateinit var binding: FragmentHistoryListBinding
    private lateinit var schedule: ScheduleDataModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        innitView(schedule)

        setupCalendar(schedule)
    }

    private fun setupCalendar(schedule: ScheduleDataModel) {
        for (date in schedule.listDateData!!) {
            // Get slot in date
            val slots =
                date.slots
            if (slots != null) {
                for (i in slots.indices) {
                    slots[i].startTime = schedule.slots!![i].startTime
                    slots[i].endTime = schedule.slots[i].endTime
                }
//                setupSlotInDate(slots)
            } else {
//                setupSlotInDate(null)
            }
        }
        val adapter = DayItemAdapter(schedule.listDateData)
        binding.rvDays.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun innitView(schedule: ScheduleDataModel) {
        val startedDate = schedule.startedDate?.let { Utils.convertDate(it) }
        val stoppedDate = schedule.stoppedDate?.let { Utils.convertDate(it) }

        if (startedDate != null && stoppedDate != null) {
            binding.tvDateRange.text = "${Utils.formatDateToShortMonthStyle(startedDate)} - ${
                Utils.formatDateToShortMonthStyle(stoppedDate)
            }"
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(schedule: ScheduleDataModel) =
            HistoryListFragment().apply {
                this.schedule = schedule
            }
    }
}