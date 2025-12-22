package com.flipsidegroup.active10.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Timber.d("Notification: ${p0.data.toString()}")

        p0.data
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete){
                val refreshedToken = it.result
                Timber.d("Refreshed Firebase token: $refreshedToken")
            }
        }
    }
}