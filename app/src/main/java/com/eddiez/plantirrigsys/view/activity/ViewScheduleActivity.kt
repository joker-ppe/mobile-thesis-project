package com.eddiez.plantirrigsys.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.SlotDataModel
import com.eddiez.plantirrigsys.databinding.ActivityViewScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants

class ViewScheduleActivity : BaseActivity() {
    private lateinit var binding: ActivityViewScheduleBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(AppConstants.SCHEDULE, ScheduleDataModel::class.java)
        } else {
            intent.extras?.getSerializable(AppConstants.SCHEDULE) as ScheduleDataModel?
        }

        setupView(schedule)

        setupAppBar(schedule)
    }

    private fun setupAppBar(schedule: ScheduleDataModel?) {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemClone -> {

                    // increase the number of copies
                    increaseCopies(schedule?.id!!)

                    schedule.id = null
                    schedule.title = "${schedule.title} (Clone)"
                    schedule.imageData = null
                    schedule.isPublic = false
                    schedule.latitude = null
                    schedule.longitude = null

                    val intent = Intent(this, CreateScheduleActivity::class.java)
                    intent.putExtra(AppConstants.SCHEDULE, schedule)
                    intent.putExtra(AppConstants.CLONE, true)
                    startActivity(intent)

                    finish()

                    true
                }

                else -> false
            }
        }
    }

    private fun increaseCopies(id: Int) {
        userViewModel.accessToken.observe(this) { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.increaseNumberOfCopies(accessToken = token, id = id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(schedule: ScheduleDataModel?) {
        if (schedule != null) {

            // increment the number of views
            increaseViews(schedule.id!!)

            binding.topAppBar.title = schedule.title

            binding.tvOwner.text = "${schedule.user?.firstName} ${schedule.user?.lastName}"

            Glide.with(this)
                .asBitmap()
                .load(schedule.imageData)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgData)

            binding.tvDescription.text = schedule.description

            binding.tvScheduleDays.text = schedule.numberOfDates.toString()
            binding.tvScheduleDaysUnit.text = if (schedule.numberOfDates!! > 1) " days" else " day"

            binding.tvSlotsNumber.text = schedule.slots?.size.toString()
            binding.tvSlotsUnit.text = if (schedule.slots?.size!! > 1) " slots" else " slot"

            createLayoutSlots(schedule.slots)

            binding.tvTemperatureThresholdValue.text =
                if (schedule.temperatureThreshold != -1f) "${schedule.temperatureThreshold}°C" else "None"
            binding.tvMoistureThresholdValue.text =
                if (schedule.moistureThreshold != -1f) "${schedule.moistureThreshold}%" else "None"


        } else {
            finish()
        }
    }

    private fun createLayoutSlots(slots: List<SlotDataModel>) {
        for (i in slots.indices) {
            val slot = slots[i]

            val linearLayout = LinearLayout(this@ViewScheduleActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 20, 0, 20)
            }

            val tvFrom = TextView(this@ViewScheduleActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "Slot ${i + 1} from"
                setTextAppearance(R.style.TextStyleSlots)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            val tvFromTime = TextView(this@ViewScheduleActivity).apply {
                val currentTv = this
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "${slot.startTime}"
                setTextAppearance(R.style.TextTimeStyleSlots)
                setBackgroundResource(R.drawable.textview_border)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 10, 0, 10)
            }

            val tvTo = TextView(this@ViewScheduleActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "to"
                setTextAppearance(R.style.TextStyleSlots)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

            val tvToTime = TextView(this@ViewScheduleActivity).apply {
                val currentTv = this
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = "${slot.endTime}"
                setTextAppearance(R.style.TextTimeStyleSlots)
                setBackgroundResource(R.drawable.textview_border)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 10, 0, 10)
            }

            linearLayout.addView(tvFrom)
            linearLayout.addView(tvFromTime)
            linearLayout.addView(tvTo)
            linearLayout.addView(tvToTime)

            binding.bgSlots.addView(linearLayout)
        }
    }

    private fun increaseViews(id: Int) {
        userViewModel.accessToken.observe(this) { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.increaseNumberOfViews(accessToken = token, id = id)
            }
        }
    }


}