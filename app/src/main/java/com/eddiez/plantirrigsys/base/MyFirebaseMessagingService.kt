package com.eddiez.plantirrigsys.base

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the message here
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new token here
    }
}