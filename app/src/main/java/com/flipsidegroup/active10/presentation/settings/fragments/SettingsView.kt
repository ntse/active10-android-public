package com.flipsidegroup.active10.presentation.settings.fragments

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.phe.betterhealth.components.settings.SettingsItem

interface SettingsView : BaseView {

    fun updateListItems(settings: List<SettingsItem>)

    fun goToSetTarget()

    fun goToRewards()

    fun goToActivityLevel()

    fun goToLegalScreen(legalScreenString: String)

    fun goToBriskWalkReminder()

    fun goToFaq()

    fun shareApp()

    fun goToTips()

    fun goToGoals()

    fun goToHowItWorks()

    fun goToNhsLogin()

    fun turnOnAlarm(timestamp: Long?)

    fun turnOffAlarm()

    fun showDisconnectFitDialog()

    fun goToDetails(isUserLoggedIn: Boolean)

    fun goToCommunicationPreference()

    fun showDisconnect()

    fun showLogout()

    fun onContentReceived(slug: String, content: ScreenContent)

    fun goToAcknowledgments()

    fun showUserLoggedOut()

    fun showUserDisconnected()

    fun goToQuickUnlock()

}
