package com.flipsidegroup.active10.presentation.howitworks.presenter

import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.howitworks.view.HowItWorksView


class HowItWorksPresenterImpl internal constructor(
    private val localRepository: LocalRepository
) :
    BasePresenter<HowItWorksView>(),
    HowItWorksPresenter {

    override fun getHowItWorks() {
        localRepository.getHowItWorksContent(object :
            AppDatabase.OnDataLoadedListener<List<HowItWorks>> {
            override fun onDataLoaded(data: List<HowItWorks>) {
                view?.onHowItWorksListReceived(data)
            }
        })
    }
}