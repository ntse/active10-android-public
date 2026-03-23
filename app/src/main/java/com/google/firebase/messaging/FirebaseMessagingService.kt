package com.google.firebase.messaging

import android.app.Service
import android.content.Intent
import android.os.IBinder

open class FirebaseMessagingService : Service() {

    open fun onMessageReceived(message: RemoteMessage) = Unit

    open fun onNewToken(token: String) = Unit

    override fun onBind(intent: Intent?): IBinder? = null
}
