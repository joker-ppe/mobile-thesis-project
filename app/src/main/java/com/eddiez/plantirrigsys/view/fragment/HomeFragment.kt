package com.eddiez.plantirrigsys.view.fragment

import android.app.Activity
import android.content.Intent
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
import com.eddiez.plantirrigsys.dataModel.DeviceDataModel
import com.eddiez.plantirrigsys.databinding.FragmentHomeBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.utilities.RabbitMqClient
import com.eddiez.plantirrigsys.view.activity.ScanQrActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        // Handle your exception here
        Log.e("HomeFragment", "Caught $exception")
    }

    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

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
                userViewModel.decryptData(token, encodedQrCode)
            } else {
                Toast.makeText(requireContext(), "Access token is empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel.accessToken.observe(this) {}
        userViewModel.decryptedData.observe(this) {
            // Use decryptedData here
            Log.d("HomeFragment", "Decrypted data: $it")

            val gson = Gson()
            val deviceDataModel: DeviceDataModel = gson.fromJson(it, DeviceDataModel::class.java)

            Log.d("HomeFragment", "uuid: ${deviceDataModel.uuid}")
            Log.d("HomeFragment", "mqttTopic: ${deviceDataModel.mqttTopic}")
            Log.d("HomeFragment", "name: ${deviceDataModel.name}")



            val msg = "<strong>Name</strong>: ${deviceDataModel.name}<br>" +
                    "<strong>MAC Address</strong>: ${deviceDataModel.macAddress}<br>" +
                    "<strong>Serial Number</strong>: ${deviceDataModel.seriNumber}"

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.title_dialog_connect_cabin))
                .setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT))
                .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                    // Respond to positive button press
                    scope.launch {
                        val rabbitMqClient = RabbitMqClient()

                        // Connect to the RabbitMQ server
                        runBlocking { rabbitMqClient.connect() }

                        // Send a message to a specific queue
                        deviceDataModel.mqttTopic?.let { mqttTopic ->
                            rabbitMqClient.sendMessage(
                                mqttTopic,
                                it!!
                            )
                        }

                        // Close the connection and channel when you're done
                        rabbitMqClient.close()
                    }
                }
                .setCancelable(false)
                .show()
        }
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

        userViewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            // Use fullName here
            binding.tvName.text = fullName
        }

        binding.imgScanQrCode.setOnClickListener {
            // Navigate to ScanQrActivity
            val intent = Intent(requireContext(), ScanQrActivity::class.java)
            scanQrActivityResultLauncher.launch(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}