package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.presentation.common.view.BaseView
import io.reactivex.Completable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber

fun CoroutineScope.blockWithCustomLaunch(
    block: suspend CoroutineScope.() -> Unit
): () -> Unit = {
    this.launch {
        runCatching {
            block()
        }.onFailure { err ->
            Timber.e(err)
        }
    }
}

suspend fun Completable.awaitWithLoading(view: BaseView?) {
    view?.showLoading()
    this.await()
    view?.hideLoading()
}