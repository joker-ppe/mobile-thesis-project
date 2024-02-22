package com.eddiez.plantirrigsys.utilities

import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream

object FirebaseStorageHelper {
    fun uploadImageToFirebaseStorage(inputStream: InputStream, fileName: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/$fileName")
        val uploadTask = imageRef.putStream(inputStream)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}