package com.flipsidegroup.active10.presentation.tips.presenter

import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.tips.view.TipsView


class TipsPresenterImpl internal constructor(
    private val localRepository: LocalRepository
) :
    BasePresenter<TipsView>(),
    TipsPresenter {

    override fun getTips() {
        localRepository.getTipsContent(object : AppDatabase.OnDataLoadedListener<List<Tip>> {
            override fun onDataLoaded(data: List<Tip>) {
                view?.onTipsListReceived(data)
            }
        })
    }
}