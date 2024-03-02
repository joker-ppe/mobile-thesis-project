package com.eddiez.plantirrigsys.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.dataModel.SlotDataModel
import com.eddiez.plantirrigsys.databinding.ActivityCreateScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.FirebaseStorageHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.InputStream


class CreateScheduleActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateScheduleBinding
    private var selectedImageUri: Uri? = null
    private var idSchedule: Int? = null
    private var isClone = false
    private var isInUse = false
    private var latitude: Double = AppConstants.LATITUDE_DEFAULT
    private var longitude: Double = AppConstants.LONGITUDE_DEFAULT

    private var blockTriggerEventCbStatus = false

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                Glide.with(this)
                    .load(uri)
                    .into(binding.imgData)

                // Save the selected image URI
                selectedImageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private val mapsResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the returned data here
                val data: Intent? = result.data
                if (data != null) {
                    latitude = data.getDoubleExtra(AppConstants.SCHEDULE_LATITUDE, 0.0)
                    longitude = data.getDoubleExtra(AppConstants.SCHEDULE_LONGITUDE, 0.0)

                    binding.tvLatitudeValue.text = String.format("%.3f", latitude)
                    binding.tvLongitudeValue.text = String.format("%.3f", longitude)

                    blockTriggerEventCbStatus = true
                    binding.cbPublic.isChecked = true
                    blockTriggerEventCbStatus = false

                    binding.bgLocation.visibility = View.VISIBLE
                }
            }
        }

    private val listenerCbStatus = CompoundButton.OnCheckedChangeListener() { _, isChecked ->
        run {
            if (!blockTriggerEventCbStatus) {
                if (isChecked) {

                    val title = binding.filledTextFieldTitle.editText?.text.toString()
                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter title first", Toast.LENGTH_SHORT).show()

                        binding.cbPublic.isChecked = false
                    } else {
                        binding.cbPublic.isChecked = false

                        val intent = Intent(this, MapsActivity::class.java)
                        intent.putExtra(AppConstants.SCHEDULE_NAME, title)
                        intent.putExtra(AppConstants.SCHEDULE_LATITUDE, latitude)
                        intent.putExtra(AppConstants.SCHEDULE_LONGITUDE, longitude)

                        mapsResultLauncher.launch(intent)
                    }

                } else {
                    binding.bgLocation.visibility = View.GONE
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(AppConstants.SCHEDULE, ScheduleDataModel::class.java)
        } else {
            intent.extras?.getSerializable(AppConstants.SCHEDULE) as ScheduleDataModel?
        }

        if (schedule != null) {
            idSchedule = schedule.id
        }

        isClone = intent.extras?.getBoolean(AppConstants.CLONE, false) ?: false
        isInUse = intent.extras?.getBoolean(AppConstants.IN_USE, false) ?: false

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        innitView(schedule)

        scheduleViewModel.newSchedule.observe(this) { schedule ->
            if (schedule != null) {
//                Toast.makeText(this, "Create/Update schedule successfully", Toast.LENGTH_SHORT).show()
//                viewModel.newSchedule.postValue(schedule)
                scheduleViewModel.newSchedule.postValue(null)

                finish()
            }
        }

        userViewModel.accessToken.observe(this) {}
    }

    private fun innitView(schedule: ScheduleDataModel?) {

        binding.filledTextFieldNumberDays.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không làm gì ở đây
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val value = s.toString().toIntOrNull()
                    if (value == null || value < 1 || value > 100) {
                        binding.filledTextFieldNumberDays.error = "Days from 1 to 100"
                    } else {
                        binding.filledTextFieldNumberDays.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Không làm gì ở đây
            }
        })

        binding.ftfTemperatureThreshold.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString().toFloatOrNull()
                if (value == null || value < 0 || value > 60) {
                    binding.ftfTemperatureThreshold.error = "Value from 0 to 60"
                } else {
                    binding.ftfTemperatureThreshold.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.cbTemperatureThreshold.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.ftfTemperatureThreshold.visibility = View.VISIBLE
                } else {
                    binding.ftfTemperatureThreshold.visibility = View.GONE
                }
            }
        }

        binding.cbMoistureThreshold.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.ftfMoistureThreshold.visibility = View.VISIBLE
                } else {
                    binding.ftfMoistureThreshold.visibility = View.GONE
                }
            }
        }

        binding.imgData.setOnClickListener {
            // Registers a photo picker activity launcher in single-select mode.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        if (schedule != null) {

            if (isClone) {
                binding.topAppBar.title = "Clone Schedule"
                binding.btnSave.text = "Save"
                binding.tvDelete.visibility = View.GONE
            } else {
                binding.topAppBar.title = "Update Schedule"
                binding.btnSave.text = "Update"
                binding.tvDelete.visibility = View.VISIBLE

                if (isInUse) {
                    binding.topAppBar.title = "Update Schedule Using"
                }
            }


            binding.tvDelete.setOnClickListener {
                val title = "<span style='color:red'>${schedule.title}</span>"
                val message = "Are you sure you want to delete ${title}?"

                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.title_dialog_delete_schedule))
                    .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
                    .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                        // Respond to positive button press
                        userViewModel.accessToken.value?.let { token ->
                            if (token.isNotEmpty()) {
                                // 0 => Create new schedule
                                scheduleViewModel.deleteSchedule(token, schedule.id!!)
                            } else {
                                Toast.makeText(this, "Access token is empty", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                    .show()
            }

            binding.filledTextFieldTitle.editText?.text =
                Editable.Factory.getInstance().newEditable(schedule.title)
            binding.filledTextFieldDescription.editText?.text =
                Editable.Factory.getInstance().newEditable(schedule.description)

            binding.cbPublic.isChecked = schedule.isPublic == true

            if (schedule.isPublic == true) {
                binding.bgLocation.visibility = View.VISIBLE

                if (schedule.latitude == null) {
                    schedule.latitude = latitude
                }
                if (schedule.longitude == null) {
                    schedule.longitude = longitude
                }

                binding.tvLatitudeValue.text = String.format("%.3f", schedule.latitude)
                binding.tvLongitudeValue.text = String.format("%.3f", schedule.longitude)

                // update local value
                latitude = schedule.latitude ?: latitude
                longitude = schedule.longitude ?: longitude

                binding.bgLocation.visibility = View.VISIBLE
            }

            Glide.with(this)
                .asBitmap()
                .load(schedule.imageData)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.image_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgData)

            binding.filledTextFieldNumberDays.editText?.text =
                Editable.Factory.getInstance().newEditable(schedule.numberOfDates.toString())
            binding.filledTextFieldNumberSlots.editText?.text =
                Editable.Factory.getInstance().newEditable(schedule.slots?.size.toString())

            binding.cbMoistureThreshold.isChecked = schedule.moistureThreshold != -1f
            if (schedule.moistureThreshold != -1f) {
                binding.ftfMoistureThreshold.visibility = View.VISIBLE
                binding.ftfMoistureThreshold.editText?.text = Editable.Factory.getInstance()
                    .newEditable(schedule.moistureThreshold.toString())
            } else {
                binding.ftfMoistureThreshold.visibility = View.GONE
            }

            binding.cbTemperatureThreshold.isChecked = schedule.temperatureThreshold != -1f
            if (schedule.temperatureThreshold != -1f) {
                binding.ftfTemperatureThreshold.visibility = View.VISIBLE
                binding.ftfTemperatureThreshold.editText?.text = Editable.Factory.getInstance()
                    .newEditable(schedule.temperatureThreshold.toString())
            } else {
                binding.ftfTemperatureThreshold.visibility = View.GONE
            }

            createLayoutSlots(0, schedule.slots)
        } else {
            binding.ftfTemperatureThreshold.visibility = View.GONE
            binding.ftfMoistureThreshold.visibility = View.GONE

            binding.tvDelete.visibility = View.GONE
        }

        if (isInUse) {
//            binding.filledTextFieldTitle.isEnabled = false
//            binding.filledTextFieldDescription.isEnabled = false
            binding.filledTextFieldNumberDays.isEnabled = false
            binding.filledTextFieldNumberSlots.isEnabled = false
//            binding.cbPublic.isEnabled = false
//            binding.cbMoistureThreshold.isEnabled = false
//            binding.cbTemperatureThreshold.isEnabled = false
//            binding.imgData.isEnabled = false
//            binding.bgLocation.isEnabled = false
//            binding.btnSave.isEnabled = false
            binding.tvDelete.visibility = View.GONE
        }

        binding.filledTextFieldNumberSlots.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không làm gì ở đây
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val value = s.toString().toIntOrNull()
                    if (value == null || value < 1 || value > 3) {
                        binding.filledTextFieldNumberSlots.error = "Slots from 1 to 3"

                        // xóa hết các view cũ
                        binding.bgSlots.removeAllViews()
                    } else {
                        binding.filledTextFieldNumberSlots.error = null

                        // xóa hết các view cũ
                        binding.bgSlots.removeAllViews()
                        // tạo layout mới
                        createLayoutSlots(value, null)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Không làm gì ở đây
            }
        })

        // btn Save
        binding.btnSave.setOnClickListener {

            binding.spinKit.visibility = View.VISIBLE
            binding.btnSave.text = "Uploading..."

            // Upload the image when the Save button is clicked
            val inputStream: InputStream? = selectedImageUri?.let { uri ->
                contentResolver.openInputStream(uri)
            }
            val fileName: String? =
                selectedImageUri?.lastPathSegment // Get the file name from the URI
            if (inputStream != null && fileName != null) {
                FirebaseStorageHelper.uploadImageToFirebaseStorage(
                    inputStream,
                    fileName,
                    { url ->
                        // Handle success
                        // For example, you can save the URL to your database
                        sendDataToServer(url)
                    },
                    { exception ->
                        // Handle failure
                        // For example, you can show an error message to the user
                        Toast.makeText(
                            this@CreateScheduleActivity,
                            exception.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (schedule != null) {
                            sendDataToServer(schedule.imageData)
                        } else {
                            sendDataToServer(null)
                        }
                    }
                )
            } else {

                if (schedule != null) {
                    sendDataToServer(schedule.imageData)
                } else {
                    sendDataToServer(null)
                }
            }
        }

        binding.cbPublic.setOnCheckedChangeListener(listenerCbStatus)

        binding.bgLocation.setOnClickListener {
            val title = binding.filledTextFieldTitle.editText?.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter title first", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra(AppConstants.SCHEDULE_NAME, title)
                intent.putExtra(AppConstants.SCHEDULE_LATITUDE, latitude)
                intent.putExtra(AppConstants.SCHEDULE_LONGITUDE, longitude)

                mapsResultLauncher.launch(intent)
            }
        }
    }


    private fun sendDataToServer(url: String?) {
        var moistureThreshold = -1f
        if (binding.cbMoistureThreshold.isChecked) {
            moistureThreshold = binding.ftfMoistureThreshold.editText?.text.toString()
                .toFloatOrNull() ?: -1f
        }

        var temperatureThreshold = -1f
        if (binding.cbTemperatureThreshold.isChecked) {
            temperatureThreshold = binding.ftfTemperatureThreshold.editText?.text.toString()
                .toFloatOrNull() ?: -1f
        }

        var slots: List<SlotDataModel> = mutableListOf()
        val bgSlots = binding.bgSlots
        for (i in 0 until bgSlots.childCount) {
            val child = bgSlots.getChildAt(i) as LinearLayout
            val tvFromTime = child.getChildAt(1) as TextView
            val tvToTime = child.getChildAt(3) as TextView

            val startTime = tvFromTime.text.toString()
            val endTime = tvToTime.text.toString()

            slots = slots.plus(SlotDataModel(startTime = startTime, endTime = endTime))
        }

        if (slots.isEmpty()) {
            slots = slots.plus(SlotDataModel(startTime = "06:00", endTime = "06:10"))
        }

        var title = binding.filledTextFieldTitle.editText?.text.toString()
        if (title.isEmpty()) {
            title = "Untitled"
        }

        var numberOfDates = binding.filledTextFieldNumberDays.editText?.text.toString()
            .toIntOrNull() ?: 1
        if (numberOfDates < 1 || numberOfDates > 100) {
            numberOfDates = 1
        }

        val data = ScheduleDataModel(
            id = idSchedule,
            title = title,
            description = binding.filledTextFieldDescription.editText?.text.toString(),
            imageData = url,
            isPublic = binding.cbPublic.isChecked,
            numberOfDates = numberOfDates,
            slots = slots,
            longitude = longitude,
            latitude = latitude,
            moistureThreshold = moistureThreshold,
            temperatureThreshold = temperatureThreshold,
        )

        Log.d("Debug", data.toString())

        Log.d("Debug", "Access token value: ${userViewModel.accessToken.value}")

        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                // 0 => Create new schedule
                if (idSchedule == null) {
                    scheduleViewModel.createSchedule(token, data)
                } else { // => Update schedule
                    scheduleViewModel.updateSchedule(token, idSchedule!!, data)
                }

//                finish()
            } else {
                Toast.makeText(this, "Access token is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLayoutSlots(value: Int, slots: List<SlotDataModel>?) {
        if (slots != null) {
            for (i in slots.indices) {
                val slot = slots[i]

                val linearLayout = LinearLayout(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 20, 0, 20)
                }

                val tvFrom = TextView(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "Slot ${i + 1} from"
                    setTextAppearance(R.style.TextStyleSlots)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }

                val tvFromTime = TextView(this@CreateScheduleActivity).apply {
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

                    isEnabled = !isInUse

                    setOnClickListener {
                        val timeStart = currentTv.text
                        val hourStart = timeStart.split(':')[0].toInt()
                        val minuteStart = timeStart.split(':')[1].toInt()

                        val picker = MaterialTimePicker.Builder()
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTimeFormat(TimeFormat.CLOCK_24H).setHour(hourStart)
                            .setMinute(minuteStart)
                            .setTitleText("Select start time for slot ${i + 1}").build()
                        picker.addOnPositiveButtonClickListener {
                            // Định dạng thời gian và đặt vào TextView
                            val selectedTime =
                                String.format("%02d:%02d", picker.hour, picker.minute)
                            currentTv.text = "$selectedTime"
                        }
                        picker.isCancelable = false
                        picker.show(supportFragmentManager, "tag${i + 1}")
                    }
                }

                val tvTo = TextView(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "to"
                    setTextAppearance(R.style.TextStyleSlots)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }

                val tvToTime = TextView(this@CreateScheduleActivity).apply {
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

                    isEnabled = !isInUse

                    setOnClickListener {
                        val timeStart = currentTv.text
                        val hourStart = timeStart.split(':')[0].toInt()
                        val minuteStart = timeStart.split(':')[1].toInt()

                        val picker = MaterialTimePicker.Builder()
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTimeFormat(TimeFormat.CLOCK_24H).setHour(hourStart)
                            .setMinute(minuteStart)
                            .setTitleText("Select end time for slot ${i + 1}").build()
                        picker.addOnPositiveButtonClickListener {
                            val timeStart = tvFromTime.text
                            var hourStart = timeStart.split(':')[0].toIntOrNull()
                            var minuteStart = timeStart.split(':')[1].toIntOrNull()

                            if (hourStart != null && minuteStart != null) {
                                if (picker.hour < hourStart) {
                                    // Định dạng thời gian và đặt vào TextView

                                    minuteStart += 10

                                    if (minuteStart >= 60) {
                                        hourStart++
                                        minuteStart -= 60
                                    }

                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            hourStart,
                                            minuteStart
                                        )
                                    currentTv.text = "$selectedTime"
                                } else if (hourStart == picker.hour && picker.minute < minuteStart) {

                                    minuteStart += 10

                                    if (minuteStart >= 60) {
                                        hourStart++
                                        minuteStart -= 60
                                    }

                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            hourStart,
                                            minuteStart
                                        )
                                    currentTv.text = "$selectedTime"
                                } else {
                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            picker.hour,
                                            picker.minute
                                        )
                                    currentTv.text = "$selectedTime"
                                }
                            } else {
                                // Định dạng thời gian và đặt vào TextView
                                val selectedTime =
                                    String.format(
                                        "%02d:%02d",
                                        picker.hour,
                                        picker.minute
                                    )
                                currentTv.text = "$selectedTime"
                            }


                        }
                        picker.isCancelable = false
                        picker.show(supportFragmentManager, "tag${i + 1}")
                    }
                }

                linearLayout.addView(tvFrom)
                linearLayout.addView(tvFromTime)
                linearLayout.addView(tvTo)
                linearLayout.addView(tvToTime)

                binding.bgSlots.addView(linearLayout)
            }
        } else {
            for (i in 0..<value) {
                val linearLayout = LinearLayout(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 20, 0, 20)
                }

                val tvFrom = TextView(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "Slot ${i + 1} from"
                    setTextAppearance(R.style.TextStyleSlots)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }

                val tvFromTime = TextView(this@CreateScheduleActivity).apply {
                    val currentTv = this
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "${6 + (i) * (24 / value)}:00"
                    setTextAppearance(R.style.TextTimeStyleSlots)
                    setBackgroundResource(R.drawable.textview_border)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 10, 0, 10)

                    setOnClickListener {
                        val timeStart = currentTv.text
                        val hourStart = timeStart.split(':')[0].toInt()
                        val minuteStart = timeStart.split(':')[1].toInt()

                        val picker = MaterialTimePicker.Builder()
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTimeFormat(TimeFormat.CLOCK_24H).setHour(hourStart)
                            .setMinute(minuteStart)
                            .setTitleText("Select start time for slot ${i + 1}").build()
                        picker.addOnPositiveButtonClickListener {
                            // Định dạng thời gian và đặt vào TextView
                            val selectedTime =
                                String.format("%02d:%02d", picker.hour, picker.minute)
                            currentTv.text = "$selectedTime"
                        }
                        picker.isCancelable = false
                        picker.show(supportFragmentManager, "tag${i + 1}")
                    }
                }

                val tvTo = TextView(this@CreateScheduleActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "to"
                    setTextAppearance(R.style.TextStyleSlots)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }

                val tvToTime = TextView(this@CreateScheduleActivity).apply {
                    val currentTv = this
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "${6 + (i) * (24 / value)}:10"
                    setTextAppearance(R.style.TextTimeStyleSlots)
                    setBackgroundResource(R.drawable.textview_border)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 10, 0, 10)

                    setOnClickListener {
                        val timeStart = currentTv.text
                        val hourStart = timeStart.split(':')[0].toInt()
                        val minuteStart = timeStart.split(':')[1].toInt()

                        val picker = MaterialTimePicker.Builder()
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTimeFormat(TimeFormat.CLOCK_24H).setHour(hourStart)
                            .setMinute(minuteStart)
                            .setTitleText("Select end time for slot ${i + 1}").build()
                        picker.addOnPositiveButtonClickListener {
                            val timeStart = tvFromTime.text
                            var hourStart = timeStart.split(':')[0].toIntOrNull()
                            var minuteStart = timeStart.split(':')[1].toIntOrNull()

                            if (hourStart != null && minuteStart != null) {
                                if (picker.hour < hourStart) {
                                    // Định dạng thời gian và đặt vào TextView

                                    minuteStart += 10

                                    if (minuteStart >= 60) {
                                        hourStart++
                                        minuteStart -= 60
                                    }

                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            hourStart,
                                            minuteStart
                                        )
                                    currentTv.text = "$selectedTime"
                                } else if (hourStart == picker.hour && picker.minute < minuteStart) {

                                    minuteStart += 10

                                    if (minuteStart >= 60) {
                                        hourStart++
                                        minuteStart -= 60
                                    }

                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            hourStart,
                                            minuteStart
                                        )
                                    currentTv.text = "$selectedTime"
                                } else {
                                    val selectedTime =
                                        String.format(
                                            "%02d:%02d",
                                            picker.hour,
                                            picker.minute
                                        )
                                    currentTv.text = "$selectedTime"
                                }
                            } else {
                                // Định dạng thời gian và đặt vào TextView
                                val selectedTime =
                                    String.format(
                                        "%02d:%02d",
                                        picker.hour,
                                        picker.minute
                                    )
                                currentTv.text = "$selectedTime"
                            }


                        }
                        picker.isCancelable = false
                        picker.show(supportFragmentManager, "tag${i + 1}")
                    }
                }

                linearLayout.addView(tvFrom)
                linearLayout.addView(tvFromTime)
                linearLayout.addView(tvTo)
                linearLayout.addView(tvToTime)

                binding.bgSlots.addView(linearLayout)
            }
        }


    }
}