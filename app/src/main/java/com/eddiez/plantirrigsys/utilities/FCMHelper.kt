package com.eddiez.plantirrigsys.utilities

import com.google.firebase.messaging.FirebaseMessaging

class FCMHelper private constructor() {
    companion object {
        fun subscribeToTopic(topic: String, onComplete: () -> Unit) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Handle failure
                    } else {
                        // Handle success
                        onComplete()
                    }
                }
        }

        fun unsubscribeFromTopic(topic: String, onComplete: () -> Unit) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Handle failure
                    } else {
                        // Handle success
                        onComplete()
                    }
                }
        }
    }
}