package com.flipsidegroup.active10.presentation.home.presenter

import com.flipside.briskcounter.data.BriskActivity
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.home.view.HomeView
import com.flipsidegroup.active10.utils.RetrieveDataReceiver
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialogType


interface HomePresenter : LifecycleAwarePresenter<HomeView> {

    fun getMyWalksData()

    fun checkReviewForShowing()

    fun checkHighAchieversForShowing()

    fun checkMoodDialog(moodType: BHMoodBottomDialogType?)

    fun recoverWalksData()

    fun getGlobalRules()

    fun checkForActivitiesBadges(briskActivity: BriskActivity? = null)

    fun retrieveLostDataFrom5thJanuary(resultReceiver: RetrieveDataReceiver?)

    fun continueFromIntroduceWidgetDialog()

    fun checkForShowingHeroPopup()

    fun checkForShowingDeepLinkDialog()

    fun continueFromHeroPopup(content: ScreenContent)

    fun highAchieversUpdateLastShowDate()

    fun highAchieversDontAskMeGain()

    fun nhsUserLogout()

    fun syncLastActivitiesAndRewardsToNhs()
}
