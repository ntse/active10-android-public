package com.google.firebase.perf.util

/**
 * Minimal Optional replacement to avoid bringing in Firebase Perf just for Optional.
 */
class Optional<T> private constructor(private val value: T?) {
    val isAvailable: Boolean
        get() = value != null

    fun get(): T = value ?: throw NoSuchElementException("No value present")

    companion object {
        fun <T> fromNullable(value: T?): Optional<T> = Optional(value)
    }
}
