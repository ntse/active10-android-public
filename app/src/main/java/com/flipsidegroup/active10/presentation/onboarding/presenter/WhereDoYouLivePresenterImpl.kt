package com.flipsidegroup.active10.presentation.onboarding.presenter

import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.onboarding.view.WhereDoYouLiveView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await


class WhereDoYouLivePresenterImpl internal constructor(
    private val screenRepository: ScreenRepository,
) : BasePresenter<WhereDoYouLiveView>(), WhereDoYouLivePresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun getContent() {
        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.LOGIN_CURRENT_LOCATION)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.onContentReceived(it) }
        }
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

}