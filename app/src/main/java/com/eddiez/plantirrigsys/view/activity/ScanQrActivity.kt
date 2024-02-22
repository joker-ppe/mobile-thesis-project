package com.eddiez.plantirrigsys.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityScanQrBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class ScanQrActivity : BaseActivity() {

    private lateinit var binding: ActivityScanQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()
        moduleInstall
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.
                }
            }
            .addOnFailureListener {
                // Handle failure…
            }

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(this, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                val rawValue: String? = barcode.rawValue
//                    Toast.makeText(this, rawValue, Toast.LENGTH_SHORT).show()

                val resultIntent = Intent()
                resultIntent.putExtra(AppConstants.QRCODE_DATA, rawValue)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnCanceledListener {
                val resultIntent = Intent()
                resultIntent.putExtra(AppConstants.QRCODE_DATA, "")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("ScanQrActivity", "Error scanning QR code", e)

                val resultIntent = Intent()
                resultIntent.putExtra(AppConstants.QRCODE_DATA, "")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }


//        userViewModel.accessToken.observe(this) {
//            binding.tvResult.text = it
//        }
    }
}