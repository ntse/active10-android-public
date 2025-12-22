package com.flipsidegroup.active10.presentation.circularwalk

import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class CircularWalkDetailsPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
) : BasePresenter<CircularWalkDetailsView>(), CircularWalkDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                getViewContent().await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { content ->
                    view?.showContent(content)
                }

            view?.hideLoading()
        }
    }


    private fun getViewContent() = screenRepository
        .getScreenContentBySlug(ScreenRepository.LOGIN_CIRCULAR_WALK_DETAILS)
        .subscribeOn(Schedulers.io())

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

}