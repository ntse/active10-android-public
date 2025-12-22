package com.flipsidegroup.active10.presentation.userDetails.presenter

import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.userDetails.view.UserDetailsView
import javax.inject.Inject

class UserDetailsPresenterImpl @Inject constructor(private val localRepository: LocalRepository): BasePresenter<UserDetailsView>(), UserDetailsPresenter {

    override fun getAboutYouDescription() {
        view?.showLoading()
        localRepository.getOnboarding(object : AppDatabase.OnDataLoadedListener<Onboarding?> {
            override fun onDataLoaded(data: Onboarding?) {
                view?.onAboutYouDescriptionReceived(data)
            }
        })
    }
}