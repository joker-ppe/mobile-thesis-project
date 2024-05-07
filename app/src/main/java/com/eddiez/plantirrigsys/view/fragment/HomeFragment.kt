package com.eddiez.plantirrigsys.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.base.MyApplication
import com.eddiez.plantirrigsys.dataModel.ActionDataModel
import com.eddiez.plantirrigsys.dataModel.CabinetDataModel
import com.eddiez.plantirrigsys.dataModel.MessageDataModel
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentHomeBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.Utils
import com.eddiez.plantirrigsys.view.activity.ScanQrActivity
import com.eddiez.plantirrigsys.view.activity.ScheduleInfoActivity
import com.eddiez.plantirrigsys.view.adapter.ScheduleInUseListAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val gson = Gson()

    private var numberEntryTemperature = 0
    private var numberEntryHumidity = 0
    private var numberEntryLight = 0

    private var slotIdIrrigating = 0
    private var idCabinet = -1
    private var userId = -1
    private var scheduleId = -1
    private var dayIndex = -1
    private var slotIndex = -1

    private var uuidAction = ""

    private val temperatureEntries = ArrayList<Entry>()
    private val humidityEntries = ArrayList<Entry>()
    private val lightEntries = ArrayList<Entry>()

    private var isWatering = false
    private var loadingDialog: AlertDialog? = null

//    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
//        // Handle your exception here
//        Log.e("HomeFragment", "Caught $exception")
//    }

//    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val scanQrActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val scanResult = data?.getStringExtra(AppConstants.QRCODE_DATA)

            if (!scanResult.isNullOrEmpty()) {
                decodeQrCode(scanResult)
            } else {
                Toast.makeText(requireContext(), "Failed to scan QR code", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun decodeQrCode(encodedQrCode: String) {
        // Decode the QR code here
        // Use the decoded QR code here
        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                // 0 => Create new schedule
                userViewModel.decryptData(token, MyApplication.getApiKey(), encodedQrCode)
            } else {
                Toast.makeText(requireContext(), "Access token is empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupUICabinetConnected(it: CabinetDataModel?) {
        binding.bgConnectedCabinet.visibility = View.VISIBLE
        binding.bgScanQrCode.visibility = View.GONE

        binding.tvNameValue.text = it?.name

        // setup chart line
        updateChartTemperature()
        updateChartHumidity()
        updateChartLight()
    }

    private fun updateChartLight() {
        // Update chart temperature here
        val chart = binding.lineChartLight

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)

        chart.setDrawGridBackground(false)

        chart.legend.isEnabled = false

        val xAxis = chart.xAxis
        val yAxis = chart.axisLeft
        val rightYAxis = chart.axisRight

        // Hide the labels on the XAxis
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)
        yAxis.setDrawGridLines(false)
        rightYAxis.setDrawGridLines(false)
    }

    private fun updateChartTemperature() {
        // Update chart temperature here
        val chart = binding.lineChartTemperature

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)

        chart.setDrawGridBackground(false)

        chart.legend.isEnabled = false

        val xAxis = chart.xAxis
        val yAxis = chart.axisLeft
        val rightYAxis = chart.axisRight

        // Hide the labels on the XAxis
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)
        yAxis.setDrawGridLines(false)
        rightYAxis.setDrawGridLines(false)
    }

    private fun updateChartHumidity() {
        // Update chart temperature here
        val chart = binding.lineChartHumidity

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)

        chart.setDrawGridBackground(false)

        chart.legend.isEnabled = false

        val xAxis = chart.xAxis
        val yAxis = chart.axisLeft
        val rightYAxis = chart.axisRight

        // Hide the labels on the XAxis
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)
        yAxis.setDrawGridLines(false)
        rightYAxis.setDrawGridLines(false)
    }

    private fun addDataToTemperatureChart(temperature: Float) {
        // Add a new entry to the entries list every time temperatureReceived has data
        numberEntryTemperature++
        val entry = Entry((numberEntryTemperature).toFloat(), temperature)
        temperatureEntries.add(entry)

        if (temperatureEntries.size > 20) {
            temperatureEntries.removeAt(0)
        }

        // Create a LineDataSet with the entries and a label
        val dataSet = LineDataSet(temperatureEntries, "Temperature")
        dataSet.color = Color.RED // Set line color to blue
        dataSet.lineWidth = 1f // Set line width to 2dp
        dataSet.setCircleColor(Color.RED) // Set circle color to blue
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Hide the labels
        dataSet.setDrawValues(false)

        // Create a LineData object with the LineDataSet
        val lineData = LineData(dataSet)

        // Set the LineData to the chart
        binding.lineChartTemperature.data = lineData

        // Notify the data set and the chart that the underlying data has changed
//        dataSet.notifyDataSetChanged()
//        binding.lineChartTemperature.notifyDataSetChanged()

        // Refresh the chart
        binding.lineChartTemperature.invalidate()
    }

    private fun addDataToHumidityChart(humidity: Float) {
        // Add a new entry to the entries list every time temperatureReceived has data
        numberEntryHumidity++
        val entry = Entry((numberEntryHumidity).toFloat(), humidity)
        humidityEntries.add(entry)

        if (humidityEntries.size > 20) {
            humidityEntries.removeAt(0)
        }

        // Create a LineDataSet with the entries and a label
        val dataSet = LineDataSet(humidityEntries, "Humidity")
        dataSet.color = Color.BLUE // Set line color to blue
        dataSet.lineWidth = 1f // Set line width to 2dp
        dataSet.setCircleColor(Color.BLUE) // Set circle color to blue
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Hide the labels
        dataSet.setDrawValues(false)

        // Create a LineData object with the LineDataSet
        val lineData = LineData(dataSet)

        // Set the LineData to the chart
        binding.lineChartHumidity.data = lineData

        // Notify the data set and the chart that the underlying data has changed
//        dataSet.notifyDataSetChanged()
//        binding.lineChartTemperature.notifyDataSetChanged()

        // Refresh the chart
        binding.lineChartHumidity.invalidate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textClock = binding.tvTimeValue
        textClock.format12Hour = null
        textClock.format24Hour = "HH:mm:ss"

        observeViewModels()
        innitEvents()
    }

    private fun innitEvents() {
        binding.imgScanQrCode.setOnClickListener {
            // Navigate to ScanQrActivity
            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            scanQrActivityResultLauncher.launch(intent)
        }

        binding.imgRemoveCabinet.setOnClickListener {
            // Remove cabinet
            userViewModel.connectedCabinet.value?.let { connectedCabinet ->
                val msg =
                    "Bạn có chắc chắn muốn ngắt kết nối tủ <strong>${connectedCabinet.name}</strong> không?"

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT))
//                .setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT))
                    .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                        userViewModel.accessToken.value.let { accessToken ->
                            if (accessToken != null) {
                                userViewModel.removeCabinet(accessToken)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Access token is empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        binding.btnAddSchedule.setOnClickListener {
            userViewModel.accessToken.value?.let { token ->
                if (token.isNotEmpty()) {
                    scheduleViewModel.getSchedulesToChoose(token)
                }
            }
        }

        binding.imgShowInfoSchedule.setOnClickListener {
            scheduleViewModel.scheduleInUse.value?.let { scheduleInUse ->
                val intent = Intent(requireContext(), ScheduleInfoActivity::class.java)
                intent.putExtra(AppConstants.SCHEDULE, scheduleInUse)
                intent.putExtra(AppConstants.SLOT_ID_IRRIGATING, slotIdIrrigating)
                startActivity(intent)
            }
        }

        binding.imgActionSlot.setOnClickListener {
            if (loadingDialog == null) {
                loadingDialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(R.layout.dialog_loading)
                    .setCancelable(false)
                    .create()
            }
            var message = ""
            var action = ""
            if (isWatering) {
                message = "Bạn có chắc chắn muốn ngừng tưới nước sớm không??"
                action = "STOP"
            } else {
                message = "Bạn có chắc chắn muốn bắt đầu tưới nước sớm không??"
                action = "START"
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm")
                .setMessage(message)
                .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                    if (idCabinet != -1 && userId != -1 && scheduleId != -1 && dayIndex != -1 && slotIndex != -1) {
                        uuidAction =
                            "${MyApplication.getUniqueDeviceId(requireContext())}.${System.currentTimeMillis()}"
                        val data = ActionDataModel(
                            uuidAction,
                            userId,
                            scheduleId,
                            dayIndex,
                            slotIndex,
                            action,
                            false
                        )
                        userViewModel.sendActionToCabinet(idCabinet, gson.toJson(data))

                        if (!loadingDialog!!.isShowing) {
                            loadingDialog!!.show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Data is empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (loadingDialog!!.isShowing) {
                            loadingDialog!!.dismiss()
                        }
                    }
                }
                .setCancelable(false)
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModels() {
        userViewModel.userData.observe(viewLifecycleOwner) { it ->
            // Use fullName here
            binding.tvName.text = it!!.firstName + " " + it.lastName

            if (it.deviceId != null) {
                userViewModel.getCabinet(MyApplication.getApiKey(), it.deviceId)
            } else {
                binding.bgConnectedCabinet.visibility = View.GONE
                binding.bgScanQrCode.visibility = View.VISIBLE
            }
        }

        scheduleViewModel.schedulesToChoose.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No schedule to choose. Create one first",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val adapter = ScheduleInUseListAdapter(requireContext(), it)
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Choose a schedule")
                        .setAdapter(adapter) { dialog, position ->
                            // 'position' is the index of the selected item
                            val selectedItem = it[position]

                            userViewModel.accessToken.value?.let { token ->
                                if (token.isNotEmpty()) {
                                    scheduleViewModel.setScheduleInUse(token, selectedItem.id!!)
                                }
                            }
                        }
                        .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }

        userViewModel.accessToken.observe(viewLifecycleOwner) {
            if (it != null) {
                userViewModel.getProfile(it)

                scheduleViewModel.getScheduleInUse(it)
            } else {
                Toast.makeText(requireContext(), "Access token is empty", Toast.LENGTH_SHORT).show()
            }
        }
//        userViewModel.userDataJson.observe(viewLifecycleOwner) {}
        userViewModel.decryptedData.observe(viewLifecycleOwner) {
            // Use decryptedData here
            Log.d("HomeFragment", "Decrypted data: $it")


            val cabinetDataModel: CabinetDataModel = gson.fromJson(it, CabinetDataModel::class.java)

            Log.d("HomeFragment", "uuid: ${cabinetDataModel.uuid}")
            Log.d("HomeFragment", "mqttTopic: ${cabinetDataModel.mqttTopic}")
            Log.d("HomeFragment", "name: ${cabinetDataModel.name}")


            val msg = "<strong>Tên</strong>: ${cabinetDataModel.name}<br>" +
                    "<strong>Địa chỉ MAC</strong>: ${cabinetDataModel.macAddress}<br>" +
                    "<strong>Số hiệu</strong>: ${cabinetDataModel.seriNumber}"

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.title_dialog_connect_cabin))
                .setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT))
                .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->

                    userViewModel.accessToken.value?.let { accessToken ->
                        userViewModel.connectCabinet(
                            accessToken,
                            cabinetDataModel.uuid!!.toInt(),
                        )
                    }
                    // Respond to positive button press

                }
                .setCancelable(false)
                .show()
        }
        userViewModel.connectedCabinet.observe(viewLifecycleOwner) {
            if (it != null) {
                // Use connectedCabinet here
                Log.d("HomeFragment", "Connected cabinet: ${it.name}")

                idCabinet = it.id!!

                // Fetch temperature and humidity data
                userViewModel.consumeTemperature(
                    it.id,
                    it.userId!!,
                    MyApplication.getUniqueDeviceId(requireContext())
                )
                userViewModel.consumeHumidity(
                    it.id,
                    it.userId,
                    MyApplication.getUniqueDeviceId(requireContext())
                )
                userViewModel.consumeLight(
                    it.id,
                    it.userId,
                    MyApplication.getUniqueDeviceId(requireContext())
                )
                userViewModel.consumeMessage(
                    it.id,
                    it.userId,
                    MyApplication.getUniqueDeviceId(requireContext())
                )
                userViewModel.consumeAction(
                    it.id,
                    it.userId,
                    MyApplication.getUniqueDeviceId(requireContext())
                )
                // update UI
                setupUICabinetConnected(it)
            } else {
                //
                binding.bgConnectedCabinet.visibility = View.GONE
                binding.bgScanQrCode.visibility = View.VISIBLE
            }
        }

        userViewModel.temperatureReceived.observe(viewLifecycleOwner) {
            // Use temperatureReceived here
            Log.d("HomeFragment", "Temperature received: $it")

            if (it != null) {
                binding.tvTemperatureValue.text = it.toString()
                addDataToTemperatureChart(it)
            }
        }

        userViewModel.humidityReceived.observe(viewLifecycleOwner) {
            // Use humidityReceived here
            Log.d("HomeFragment", "Humidity received: $it")

            if (it != null) {
                binding.tvHumidityValue.text = it.toString()
                addDataToHumidityChart(it)
            }
        }

        userViewModel.lightReceived.observe(viewLifecycleOwner) {
            // Use lightReceived here
            Log.d("HomeFragment", "Light received: $it")

            if (it != null) {
                binding.tvLightValue.text = it.toString()
                addDataToLightChart(it)
            }
        }

        userViewModel.messageReceived.observe(viewLifecycleOwner) {
            // Use messageReceived here
            Log.d("HomeFragment", "Message received: $it")

            if (it != null) {
                try {
                    val messageData = gson.fromJson(it, MessageDataModel::class.java)

                    // save data
                    userId = messageData.userId!!
                    scheduleId = messageData.scheduleId!!
                    dayIndex = messageData.dayIndex!!
                    slotIndex = messageData.slotIndex!!

                    // calculate percent
                    val percentCompleteDays = Math.round(
                        messageData.completeDays!!.toFloat() * 100 / messageData.totalDays!!
                    )

                    val percentCompleteSlots = Math.round(
                        messageData.completeSlots!!.toFloat() * 100 / messageData.totalSlots!!
                    )

                    binding.tvProgress.text =
                        "${messageData.completeDays}/${messageData.totalDays} ngày - $percentCompleteDays%\n" +
                                "${messageData.completeSlots}/${messageData.totalSlots} lần - $percentCompleteSlots%"

                    if (messageData.allowAction == true) {
                        binding.imgActionSlot.visibility = View.VISIBLE
                    } else {
                        binding.imgActionSlot.visibility = View.GONE
                    }
                    if (messageData.slotStatus == "IN_PROGRESS") {
//                        val doneSecond = Utils.calculateTotalSeconds(
//                            Utils.convertStringToTime(messageData.startTime!!)!!,
//                            LocalTime.now()
//                        )
//                        val totalSecond = Utils.calculateTotalSeconds(
//                            Utils.convertStringToTime(messageData.startTime)!!,
//                            Utils.convertStringToTime(messageData.endTime!!)!!
//                        )
//                        var percent = Math.round(doneSecond.toFloat() * 100 / totalSecond)
//                        if (percent > 100) {
//                            percent = 100
//                        }

                        val percent = messageData.completeRate!!

                        binding.tvStatus.text =
                            "Đang tưới: $percent%\nKết thúc lúc: ${messageData.endTime}"

                        binding.imgActionSlot.setImageResource(R.drawable.stop)

                        isWatering = true
                    } else {
                        binding.tvStatus.text =
                            "Lượt tiếp theo:\n${messageData.startTime} - ${messageData.day}"

                        binding.imgActionSlot.setImageResource(R.drawable.play)

                        isWatering = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvStatus.text = "Không thể lấy dữ liệu"
                    binding.imgActionSlot.visibility = View.GONE
                }
            } else {
                binding.tvStatus.text = "Chưa áp dụng lịch tưới"
                binding.imgActionSlot.visibility = View.GONE
            }
        }

        scheduleViewModel.scheduleInUse.observe(viewLifecycleOwner) {
            if (it != null) {
                setupScheduleInUse(it)
            } else {
                binding.bgScheduleInfo.visibility = View.GONE
                binding.btnAddSchedule.visibility = View.VISIBLE
            }
        }

        userViewModel.actionReceived.observe(viewLifecycleOwner) {
            if (loadingDialog == null) {
                loadingDialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(R.layout.dialog_loading)
                    .setCancelable(false)
                    .create()
            }
            if (loadingDialog!!.isShowing) {
                loadingDialog!!.dismiss()
            }
            if (it != null) {
                try {
                    val actionDataModel = gson.fromJson(it, ActionDataModel::class.java)
                    val dialogView =
                        if (actionDataModel.uuid == uuidAction
                            && actionDataModel.completeAction == true
                            && actionDataModel.userId == userId
                            && actionDataModel.scheduleId == scheduleId
                            && actionDataModel.dayIndex == dayIndex
                            && actionDataModel.slotIndex == slotIndex
                        ) {
                            R.layout.dialog_success
                        } else {
                            R.layout.dialog_fail
                        }

                    MaterialAlertDialogBuilder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
//                        .setPositiveButton(resources.getString(android.R.string.ok), null)
                        .show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    MaterialAlertDialogBuilder(requireContext())
                        .setView(R.layout.dialog_fail)
                        .setCancelable(true)

                        .show()
                }
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setView(R.layout.dialog_fail)
                    .setCancelable(true)
                    .show()
            }
        }
    }

    private fun addDataToLightChart(it: Float) {
        // Add a new entry to the entries list every time temperatureReceived has data
        numberEntryLight++
        val entry = Entry((numberEntryLight).toFloat(), it)
        lightEntries.add(entry)

        if (lightEntries.size > 20) {
            lightEntries.removeAt(0)
        }

        // Create a LineDataSet with the entries and a label
        val dataSet = LineDataSet(lightEntries, "Light")
        dataSet.color = Color.parseColor("#FFBF00") // Set line color to blue
        dataSet.lineWidth = 1f // Set line width to 2dp
        dataSet.setCircleColor(Color.parseColor("#FFBF00")) // Set circle color to blue
        dataSet.setDrawCircles(true)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Hide the labels
        dataSet.setDrawValues(false)

        // Create a LineData object with the LineDataSet
        val lineData = LineData(dataSet)

        // Set the LineData to the chart
        binding.lineChartLight.data = lineData

        // Notify the data set and the chart that the underlying data has changed
        // Refresh the chart
        binding.lineChartLight.invalidate()
    }

    @SuppressLint("SetTextI18n")
    private fun setupScheduleInUse(item: ScheduleDataModel) {
        binding.bgScheduleInfo.visibility = View.VISIBLE
        binding.btnAddSchedule.visibility = View.GONE

        binding.tvScheduleTitle.text = item.title

        Glide.with(requireContext())
            .load(item.imageData)
            .placeholder(R.drawable.image_default)
            .error(R.drawable.image_default)
            .into(binding.imgSchedule)

        var numberDateDone = 0
        val currentDate = Utils.getCurrentDateString()

        Log.d("ScheduleFragment", "currentDate: $currentDate")

        item.listDateData!!.find { it.date == currentDate }?.let {
            numberDateDone = it.index!!
        }

        Log.d("ScheduleFragment", "numberDateDone: $numberDateDone")

        val percentDate = Math.round(numberDateDone.toFloat() * 100 / item.numberOfDates!!)

        val numberOfSlotsDone =
            item.listDateData.sumOf { it.slots?.count { slot -> slot.status != "NOT_YET" } ?: 0 }

        val numberOfSlots = item.slots!!.size * item.numberOfDates

        val percentSlot = Math.round(numberOfSlotsDone.toFloat() * 100 / numberOfSlots)

        val day = if (item.numberOfDates > 1) "ngày" else "ngày"

        binding.tvProgress.text =
            "$numberDateDone/${item.numberOfDates} $day - $percentDate%\n$numberOfSlotsDone/${numberOfSlots} lần - $percentSlot%"
    }

    override fun onResume() {
        super.onResume()

        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getScheduleInUse(token)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}