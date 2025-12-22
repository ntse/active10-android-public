package com.flipsidegroup.active10.presentation.walksneardetails.presenter

import com.flipsidegroup.active10.data.persistance.walksnear.WalksNearRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.walksneardetails.view.WalksNearDetailsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import javax.inject.Inject

class WalksNearDetailsPresenterImpl @Inject constructor(
    private val walksNearRepository: WalksNearRepository,
) : BasePresenter<WalksNearDetailsView>(), WalksNearDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadWalk(id: String) {
        walksNearRepository.getWalk(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                view?.updateWalk(result)
            }, { error ->
                Timber.e(error, "Error loading walk")
            }).addToDisposables()
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}