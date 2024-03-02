package com.eddiez.plantirrigsys.view.fragment

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
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.base.MyApplication
import com.eddiez.plantirrigsys.dataModel.CabinetDataModel
import com.eddiez.plantirrigsys.databinding.FragmentHomeBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.ScanQrActivity
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

    private val temperatureEntries = ArrayList<Entry>()
    private val humidityEntries = ArrayList<Entry>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textClock = binding.tvTimeValue
        textClock.format12Hour = null
        textClock.format24Hour = "HH:mm:ss"

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

        binding.imgScanQrCode.setOnClickListener {
            // Navigate to ScanQrActivity
            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            scanQrActivityResultLauncher.launch(intent)
        }

        binding.imgRemoveCabinet.setOnClickListener {
            // Remove cabinet
            userViewModel.connectedCabinet.value?.let { connectedCabinet ->
                val msg =
                    "Are you sure to remove cabinet <strong>${connectedCabinet.name}</strong>?"

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

        userViewModel.accessToken.observe(viewLifecycleOwner) {
            if (it != null) {
                userViewModel.getProfile(it)
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


            val msg = "<strong>Name</strong>: ${cabinetDataModel.name}<br>" +
                    "<strong>MAC Address</strong>: ${cabinetDataModel.macAddress}<br>" +
                    "<strong>Serial Number</strong>: ${cabinetDataModel.seriNumber}"

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
                            cabinetDataModel.mqttTopic!!
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

                // Fetch temperature and humidity data
                userViewModel.consumeTemperature(
                    "cabinet." + it.id + ".temperature",
                    MyApplication.getUniqueDeviceId(requireContext()) + "." + it.userId + ".temperature"
                )
                userViewModel.consumeHumidity(
                    "cabinet." + it.id + ".humidity",
                    MyApplication.getUniqueDeviceId(requireContext()) + "." + it.userId + ".humidity"
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

        binding.tvStatus.text = "Đang tưới ... \nDự kiến kết thúc: 12:00 PM"
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}