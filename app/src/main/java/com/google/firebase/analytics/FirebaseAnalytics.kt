package com.google.firebase.analytics

import android.content.Context
import android.os.Bundle

class FirebaseAnalytics private constructor() {

    fun logEvent(eventName: String, bundle: Bundle?) = Unit

    fun setUserId(userId: String?) = Unit

    companion object {
        private val instance = FirebaseAnalytics()

        @JvmStatic
        fun getInstance(context: Context): FirebaseAnalytics = instance
    }
}
