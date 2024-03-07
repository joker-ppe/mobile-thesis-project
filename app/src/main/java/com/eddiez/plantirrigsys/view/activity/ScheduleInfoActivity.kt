package com.eddiez.plantirrigsys.view.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.SlotInDateDataModel
import com.eddiez.plantirrigsys.databinding.ActivityScheduleInfoBinding
import com.eddiez.plantirrigsys.databinding.CalendarDayLayoutBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.Utils
import com.eddiez.plantirrigsys.view.adapter.SlotItemAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.marcinorlowski.fonty.Fonty
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class ScheduleInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityScheduleInfoBinding
    private var idSchedule: Int? = null
    private var slotIdIrrigating = 0
    private var selectedDate: LocalDate? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        slotIdIrrigating = intent.getIntExtra(AppConstants.SLOT_ID_IRRIGATING, 0)

        val schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(AppConstants.SCHEDULE, ScheduleDataModel::class.java)
        } else {
            intent.extras?.getSerializable(AppConstants.SCHEDULE) as ScheduleDataModel?
        }

        if (schedule != null) {
            idSchedule = schedule.id

            innitView(schedule)

            setupCalendar(schedule)
        }

        observeData()
    }

    private fun observeData() {
        userViewModel.accessToken.observe(this) {}
        scheduleViewModel.scheduleInUse.observe(this) {
            if (it == null) {
                finish()
            }
        }
    }

    private fun setupCalendar(schedule: ScheduleDataModel) {
        val startedDate = schedule.startedDate?.let { Utils.convertDate(it) }
        val stoppedDate = schedule.stoppedDate?.let { Utils.convertDate(it) }

        val calendarView = binding.calendarView

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        val titlesContainer = binding.titlesContainer.root
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
                textView.textSize = 13f
                textView.setTextColor(Color.BLACK)
                textView.setTypeface(null, Typeface.BOLD)
            }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            @SuppressLint("SetTextI18n")
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                // Set the calendar day for this container.
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTypeface(null, Typeface.NORMAL)
                    container.textView.setTextColor(Color.BLACK)
                    container.textView.visibility = View.VISIBLE
                } else {
                    container.textView.setTypeface(null, Typeface.NORMAL)
                    container.textView.setTextColor(Color.GRAY)
                    container.textView.visibility = View.INVISIBLE
                }

                if (startedDate != null && stoppedDate != null) {
                    if (data.date.dayOfMonth == startedDate.dayOfMonth && data.date.month == startedDate.month && data.date.year == startedDate.year) {
                        container.textView.setTextColor(Color.BLACK)
                        container.textView.setTypeface(null, Typeface.BOLD)
                        if (startedDate == stoppedDate) {
                            container.root.setBackgroundResource(R.drawable.bg_rounded_corners_all)
                        } else {
                            container.root.setBackgroundResource(R.drawable.bg_rounded_corners_start)
                        }
                    } else if (data.date.dayOfMonth == stoppedDate.dayOfMonth && data.date.month == stoppedDate.month && data.date.year == stoppedDate.year) {
                        container.textView.setTextColor(Color.BLACK)
                        container.textView.setTypeface(null, Typeface.BOLD)
                        container.root.setBackgroundResource(R.drawable.bg_rounded_corners_end)
                    } else if (data.date.isAfter(startedDate) && data.date.isBefore(stoppedDate) && data.position == DayPosition.MonthDate) {
                        container.textView.setTextColor(Color.BLACK)
                        container.textView.setTypeface(null, Typeface.BOLD)
                        container.root.setBackgroundResource(R.drawable.bg_rounded_corners_none)
                    }
                }

                if (data.date.dayOfMonth == LocalDate.now().dayOfMonth && data.date.month == LocalDate.now().month && data.date.year == LocalDate.now().year) {
                    container.textView.setTextColor(Color.RED)
                    container.textView.setTypeface(null, Typeface.BOLD)
                }

                container.root.setOnClickListener {
                    // Check the day position as we do not want to select in or out dates.
                    if (data.position == DayPosition.MonthDate) {
                        binding.tvDate.text = "${
                            data.date.dayOfWeek.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        }, ${data.date.dayOfMonth} ${
                            data.date.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        } ${data.date.year}"
                        // Keep a reference to any previous selection
                        // in case we overwrite it and need to reload it.
                        val currentSelection = selectedDate
                        if (currentSelection == data.date) {
                            // If the user clicks the same date, clear selection.
                            selectedDate = null
                            // Reload this date so the dayBinder is called
                            // and we can REMOVE the selection background.
                            calendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = data.date
                            // Reload the newly selected date so the dayBinder is
                            // called and we can ADD the selection background.
                            calendarView.notifyDateChanged(data.date)
                            if (currentSelection != null) {
                                // We need to also reload the previously selected
                                // date so we can REMOVE the selection background.
                                calendarView.notifyDateChanged(currentSelection)
                            }
                        }
                    }
                }

                if (data.position == DayPosition.MonthDate) {
                    // Show the month dates. Remember that views are reused!

                    if (data.date == selectedDate) {
                        // If this is the selected date, show a round background and change the text color.
                        container.imgArrow.visibility = View.VISIBLE

                    } else {
                        container.imgArrow.visibility = View.INVISIBLE
                    }

                    // Get slot in date
                    val slots =
                        schedule.listDateData!!.find { Utils.convertDate(it.date!!) == selectedDate }?.slots
                    if (slots != null) {
                        for (i in slots.indices) {
                            slots[i].startTime = schedule.slots!![i].startTime
                            slots[i].endTime = schedule.slots[i].endTime
                        }
                        setupSlotInDate(slots)
                    } else {
                        setupSlotInDate(null)
                    }
                } else {
                    // Hide in and out dates
                    container.imgArrow.visibility = View.INVISIBLE
                    setupSlotInDate(null)
                }
            }
        }

        calendarView.monthScrollListener = { calendarMonth ->
            val monthString =
                calendarMonth.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = calendarMonth.yearMonth.year
            binding.tvScheduleMonth.text = "$monthString $year"
        }

        Fonty.setFonts(calendarView)
    }

    private fun setupSlotInDate(slots: List<SlotInDateDataModel>?) {
        if (slots != null) {
            val adapter = SlotItemAdapter(slots)
            binding.rvSlots.adapter = adapter
        } else {
            binding.rvSlots.adapter = SlotItemAdapter(listOf())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun innitView(schedule: ScheduleDataModel) {
        binding.topAppBar.title = schedule.title

        binding.tvScheduleName.text = schedule.title

        Glide.with(this)
            .load(schedule.imageData)
            .placeholder(R.drawable.image_default)
            .error(R.drawable.image_default)
            .into(binding.imgSchedule)

        val startedDate = schedule.startedDate?.let { Utils.convertDate(it) }
        val stoppedDate = schedule.stoppedDate?.let { Utils.convertDate(it) }

        if (startedDate != null && stoppedDate != null) {
            binding.tvDateRange.text = "${Utils.formatDateToShortMonthStyle(startedDate)} - ${
                Utils.formatDateToShortMonthStyle(stoppedDate)
            }"
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemRemove -> {

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to stop using this schedule?")
                        .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                            // Respond to positive button press
                            userViewModel.accessToken.value?.let { token ->
                                if (token.isNotEmpty()) {
                                    scheduleViewModel.removeScheduleInUse(token)
                                }
                            }
                        }
                        .setCancelable(false)
                        .show()
                    true
                }

                else -> false
            }
        }
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
//        val textView = view.findViewById<TextView>(R.id.calendarDayText)

        // With ViewBinding
        val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
        val root = CalendarDayLayoutBinding.bind(view).root
        val imgArrow = CalendarDayLayoutBinding.bind(view).imgArrow


    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        // Alternatively, you can add an ID to the container layout and use findViewById()
        val titlesContainer = view as ViewGroup
    }
}