package com.flipsidegroup.active10.presentation.couch

import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class CouchAdvertPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
) : BasePresenter<CouchAdvertView>(), CouchAdvertPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug(ScreenRepository.DOWNLOAD_COUCH25K_VIEW)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showContent(it) }
        }
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}