package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityScanQrBinding
import com.eddiez.plantirrigsys.viewmodel.UserViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.AndroidEntryPoint

class ScanQrActivity : BaseActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(this, options)

        binding.btnScan.setOnClickListener {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    // Task completed successfully
                    val rawValue: String? = barcode.rawValue
//                    Toast.makeText(this, rawValue, Toast.LENGTH_SHORT).show()

                    binding.tvResult.text = rawValue
                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }


        viewModel.accessToken.observe(this) {
            binding.tvResult.text = it
        }
    }
}