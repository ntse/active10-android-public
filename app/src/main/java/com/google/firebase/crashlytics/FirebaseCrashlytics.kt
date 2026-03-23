package com.google.firebase.crashlytics

class FirebaseCrashlytics private constructor() {

    fun log(message: String) = Unit

    fun recordException(throwable: Throwable) = Unit

    companion object {
        private val instance = FirebaseCrashlytics()

        @JvmStatic
        fun getInstance(): FirebaseCrashlytics = instance
    }
}
