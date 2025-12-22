package com.flipsidegroup.active10.presentation.usecases

import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.CheckAuthManager
import timber.log.Timber
import javax.inject.Inject

class RemoveNhsUserDataUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val settingsUtils: SettingsUtils,
    private val localNotificationRepository: LocalNotificationRepository,
    private val checkAuthManager: CheckAuthManager,
) {

    operator fun invoke(showLogoutPopup: Boolean = true) {
        preferenceRepository.showLogoutPopupAttemptsCount = 3
        if (preferenceRepository.isUserLoggedIn && showLogoutPopup) {
            preferenceRepository.showLogoutPopup = true
        }
        preferenceRepository.isUserLoggedIn = false
        settingsUtils.getSettingsHolder().copy(nhsUser = null, nhsToken = null).also {
            settingsUtils.saveSettingsHolder(it)
        }
        localNotificationRepository.setNoAccountUser()
        //"postValue" instead "value" because it can be invoked in background thread
        checkAuthManager.invokeAuthScreen.postValue(false)
        preferenceRepository.isBiometricAllowed = false
        preferenceRepository.authPinCode = null
        Timber.d("Remove NHS User Data: Logged out - User data removed")
    }

}