package com.flipsidegroup.active10.presentation.walkpresentation.presenter

import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.walkpresentation.view.WalkPresentationView
import com.flipsidegroup.active10.utils.WalkDataGenerator

class WalkPresentationPresenterImp internal constructor(
    private val settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository
) : BasePresenter<WalkPresentationView>(), WalkPresentationPresenter {

    override fun getWalkData(start: Long, end: Long) {
        if (settingsUtils.getSettingsHolder().generateRandomWalkData == true) {
            val stepData = WalkDataGenerator.getGeneratedDataByInterval(start, end)
            view?.onWalkDataReceived(stepData)
        } else {
            localRepository.getSortedActivitiesOnDays(start, end,
                object : AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                    override fun onDataLoaded(data: List<StepOverview>) {
                        view?.onWalkDataReceived(data.toMutableList())
                    }
                })
        }
    }
}