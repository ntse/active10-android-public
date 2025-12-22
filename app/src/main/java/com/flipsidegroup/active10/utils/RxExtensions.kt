package com.flipsidegroup.active10.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.exceptions.CompositeException
import retrofit2.HttpException
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private const val DEFAULT_DELAY_IN_SECONDS = 2L
private const val DEFAULT_MAX_RETRIES = 2

fun <T> Single<T>.retryWithDelay(
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    delayInSeconds: Long = DEFAULT_DELAY_IN_SECONDS,
): Single<T> {
    return this.retryWhen { errors ->
        val retryCount = AtomicInteger(0)
        errors.takeWhile { error ->
            if (error is HttpException && error.code() == 403) {
                Timber.w(error, "Not retrying due to 403 Forbidden error")
                throw error
            } else if (error is CompositeException && error.exceptions.find { it is HttpException && it.code() == 403 } != null) {
                Timber.w(error, "Not retrying due to 403 Forbidden error")
                throw error
            } else if (retryCount.getAndIncrement() < maxRetries) {
                Timber.w(error, "Retrying after $delayInSeconds seconds (attempt ${retryCount.get()})")
                true
            } else {
                throw error
            }
        }.flatMap { Flowable.timer(delayInSeconds, TimeUnit.SECONDS) }
    }
}

fun Completable.retryWithDelay(
    maxRetries: Int = DEFAULT_MAX_RETRIES,
    delayInSeconds: Long = DEFAULT_DELAY_IN_SECONDS,
): Completable {
    return this.retryWhen { errors ->
        val retryCount = AtomicInteger(0)
        errors.takeWhile { error ->
            if (error is HttpException && error.code() == 403) {
                Timber.w(error, "Not retrying due to 403 Forbidden error")
                throw error
            } else if (error is CompositeException && error.exceptions.find { it is HttpException && it.code() == 403 } != null) {
                Timber.w(error, "Not retrying due to 403 Forbidden error")
                throw error
            } else if (retryCount.getAndIncrement() < maxRetries) {
                Timber.w(error, "Retrying after $delayInSeconds seconds (attempt ${retryCount.get()})")
                true
            } else {
                throw error
            }
        }.flatMap { Flowable.timer(delayInSeconds, TimeUnit.SECONDS) }
    }
}