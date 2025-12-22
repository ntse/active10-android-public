package com.flipsidegroup.active10.presentation.home.view

import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialog

interface HomeView : BaseView {

    fun showReviewDialog()

    fun onGlobalRulesRetrieved(globalRules: GlobalRules?)

    fun showRetrieveDataDialog()

    fun showIntroduceWidgetDialog()

    fun showDeepLinkDialog()

    fun showHeroPopupDialog(content: ScreenContent)

    fun goToArticle(infoPage: InfoPage, source: String)

    fun navigateToUrl(url: String?)

    fun showHighAchieversDialog(showDontAskMeAgain: Boolean)

    fun showRewardDialog(rewardBadge: RewardBadge, onDismiss: () -> Unit = {})

    fun showMoodBottomDialog(moodBottomDialog: BHMoodBottomDialog)

    fun checkForShowingNhsLogBackInDialog()
}
