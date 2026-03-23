package com.google.firebase.messaging

class FirebaseMessaging private constructor() {

    val token: Task<String> = Task("")

    companion object {
        private val instance = FirebaseMessaging()

        @JvmStatic
        fun getInstance(): FirebaseMessaging = instance
    }
}

class Task<T>(val result: T) {
    val isComplete: Boolean = true

    fun addOnCompleteListener(listener: (Task<T>) -> Unit): Task<T> {
        listener(this)
        return this
    }
}
