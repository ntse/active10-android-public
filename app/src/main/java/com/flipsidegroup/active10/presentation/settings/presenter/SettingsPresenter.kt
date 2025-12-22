package com.flipsidegroup.active10.presentation.settings.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.settings.fragments.SettingsView

interface SettingsPresenter: LifecycleAwarePresenter<SettingsView> {

    fun generateSettingsItemList()

    fun onNotificationsPermissionResult(isGranted: Boolean)

    fun initializeReminder()

    fun getContent(slug: String)

    fun onUserDisconnect()

    fun onUserLoggedOut()

}