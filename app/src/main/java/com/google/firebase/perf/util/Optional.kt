package com.google.firebase.perf.util

class Optional<T> private constructor(
    private val value: T?,
    val isAvailable: Boolean,
) {
    fun get(): T = value ?: throw NoSuchElementException("Optional value is not available")

    companion object {
        @JvmStatic
        fun <T> of(value: T): Optional<T> = Optional(value, true)

        @JvmStatic
        fun <T> absent(): Optional<T> = Optional(null, false)
    }
}
