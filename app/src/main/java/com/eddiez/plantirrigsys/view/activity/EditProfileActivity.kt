package com.eddiez.plantirrigsys.view.activity

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseActivity
import com.eddiez.plantirrigsys.dataModel.UserDataModel
import com.eddiez.plantirrigsys.databinding.ActivityEditProfileBinding
import com.eddiez.plantirrigsys.utilities.FirebaseStorageHelper
import com.eddiez.plantirrigsys.utilities.Utils
import java.io.InputStream

class EditProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private var selectedImageUri: Uri? = null
    private var firstTimeOnScreen = true
    private var currentPhotoUrl: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                Glide.with(this)
                    .load(uri)
                    .into(binding.imgAvatar)

                // Save the selected image URI
                selectedImageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (bitmap != null) {
                Log.d("Camera", "Picture taken")

                val uri = Utils.bitmapToUri(bitmap, this)

                Glide.with(this)
                    .load(uri)
                    .into(binding.imgAvatar)

                // Save the selected image URI
                selectedImageUri = uri
            } else {
                Log.d("Camera", "No picture taken")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        observeData()

        innitEvent()
    }

    private fun innitEvent() {
        binding.btnSave.setOnClickListener {
            val name = binding.filledTextFieldName.editText?.text.toString()

            if (name.isEmpty()) {
                binding.filledTextFieldName.error = "Vui lòng nhập họ tên"
                return@setOnClickListener
            }

            binding.btnSave.text = "Đang cập nhật..."

            val nameStr = name.split(" ")
            val firstName = nameStr[0]
            val lastName = if (nameStr.size > 1) {
                name.replace(nameStr[0], "").trim()
            } else {
                ""
            }

            if (selectedImageUri == null) {
                sendDataToServer(UserDataModel(firstName = firstName, lastName = lastName, photoUrl = currentPhotoUrl))
                return@setOnClickListener
            }

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
                        sendDataToServer(UserDataModel(firstName = firstName, lastName = lastName, photoUrl = url))
                    },
                    { exception ->
                        // Handle failure
                        // For example, you can show an error message to the user
                        Toast.makeText(
                            this@EditProfileActivity,
                            exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }

        binding.imgAvatar.setOnClickListener {
            // Registers a photo picker activity launcher in single-select mode.
            val popupMenu =
                PopupMenu(this, binding.imgAvatar) // 'view' is the anchor view for the popup menu
            popupMenu.menuInflater.inflate(
                R.menu.popup_image,
                popupMenu.menu
            ) // 'R.menu.popup_menu' is the menu resource file

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.take_picture -> {
                        // User selected Option 1
                        takePicture.launch(null)
                        true
                    }

                    R.id.select_image -> {
                        // User selected Option 2
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun sendDataToServer(userData: UserDataModel) {
        userViewModel.accessToken.value?.let {
            userViewModel.updateProfile(it, userData)
        }
    }

    private fun observeData() {
        userViewModel.accessToken.observe(this) {
            if (it.isNotEmpty()) {
                if (firstTimeOnScreen) {
                    userViewModel.getProfile(it)
                }
            }
        }

        userViewModel.userData.observe(this) {
            it?.let {

                if (!firstTimeOnScreen) {
                    this.finish()
                }

                currentPhotoUrl = it.photoUrl

                val name = Editable.Factory.getInstance()
                    .newEditable((it.firstName + " " + it.lastName).replace("null", "").trim())
                binding.filledTextFieldName.editText?.text = name

                val email = Editable.Factory.getInstance().newEditable(it.email)
                binding.filledTextFieldEmail.editText?.text = email

                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.avatar_ai)
                    .into(binding.imgAvatar)

                if (firstTimeOnScreen) {
                    firstTimeOnScreen = false
                }
            }
        }
    }
}