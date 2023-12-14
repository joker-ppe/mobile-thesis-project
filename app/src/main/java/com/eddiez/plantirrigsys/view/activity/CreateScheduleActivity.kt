package com.eddiez.plantirrigsys.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.databinding.ActivityCreateScheduleBinding

class CreateScheduleActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateScheduleBinding
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            binding.imgData.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgData.setOnClickListener {
            // Registers a photo picker activity launcher in single-select mode.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

//            val mimeType = "image/gif"
//            pickMedia.launch(
//                PickVisualMediaRequest(
//                    ActivityResultContracts.PickVisualMedia.SingleMimeType(
//                        mimeType
//                    )
//                )
//            )
        }
    }
}