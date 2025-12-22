package com.flipsidegroup.active10.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import timber.log.Timber


private const val SETTINGS_PREFERENCES = "settings_sh_preferences"
private const val SETTINGS_DATA_HOLDER_KEY = "SETTINGS_DATA_HOLDER_KEY"

class SettingsUtils(private val context: Context, private val appGson: Gson) {

    fun updateSettings(settingsDataHolder: SettingsDataHolder) {
        val savedSettingsHolder = getSettingsHolder()
        savedSettingsHolder.run {
            this.goalsList =
                settingsDataHolder.goalsList ?: goalsList
            this.targetList =
                settingsDataHolder.targetList ?: targetList
            this.isBriskReminderSet =
                settingsDataHolder.isBriskReminderSet ?: isBriskReminderSet
            this.isLocationEnabled =
                settingsDataHolder.isLocationEnabled ?: isLocationEnabled
            this.isFitnessMotionEnabled =
                settingsDataHolder.isFitnessMotionEnabled ?: isFitnessMotionEnabled
            this.briskReminderTimestamp =
                settingsDataHolder.briskReminderTimestamp ?: briskReminderTimestamp
            this.goalReason =
                settingsDataHolder.goalReason ?: goalReason
            this.resetTimestamp =
                settingsDataHolder.resetTimestamp ?: resetTimestamp
            this.firstTimeInApp =
                settingsDataHolder.firstTimeInApp ?: firstTimeInApp
            this.generateRandomWalkData =
                settingsDataHolder.generateRandomWalkData ?: this.generateRandomWalkData
            this.generate1Active10WalkData =
                settingsDataHolder.generate1Active10WalkData ?: this.generate1Active10WalkData
            this.generate5Active10WalkData =
                settingsDataHolder.generate5Active10WalkData ?: this.generate5Active10WalkData
            this.deviceId = settingsDataHolder.deviceId ?: deviceId
            this.lastTimeInApp =
                settingsDataHolder.lastTimeInApp ?: lastTimeInApp
            this.notificationMin =
                settingsDataHolder.notificationMin ?: notificationMin
            this.lapsedCount =
                settingsDataHolder.lapsedCount ?: lapsedCount
            this.nextOnboardingNotification =
                settingsDataHolder.nextOnboardingNotification ?: nextOnboardingNotification
            this.deviceLocationHolder =
                settingsDataHolder.deviceLocationHolder ?: deviceLocationHolder
            this.totalBriskMinutes =
                settingsDataHolder.totalBriskMinutes ?: totalBriskMinutes
            this.lastSaved =
                settingsDataHolder.lastSaved ?: lastSaved
            this.isDeviceRegistered = settingsDataHolder.isDeviceRegistered ?: isDeviceRegistered
            this.isMigrationDataSent = settingsDataHolder.isMigrationDataSent ?: isMigrationDataSent
            this.lastSavedTarget =
                settingsDataHolder.lastSavedTarget ?: lastSavedTarget
            this.showSetTarget =
                settingsDataHolder.showSetTarget ?: showSetTarget
            this.lastMyWalksSavedTimestamp =
                settingsDataHolder.lastMyWalksSavedTimestamp ?: lastMyWalksSavedTimestamp
            this.lastReviewShownTimestamp =
                settingsDataHolder.lastReviewShownTimestamp ?: lastReviewShownTimestamp
            this.hasLeftReview =
                settingsDataHolder.hasLeftReview ?: hasLeftReview
            this.isJsonContentPersisted =
                settingsDataHolder.isJsonContentPersisted ?: isJsonContentPersisted
            this.lastCMSRefresh =
                settingsDataHolder.lastCMSRefresh ?: lastCMSRefresh
            this.useProdCMS =
                settingsDataHolder.useProdCMS ?: useProdCMS
            this.recoverDataLastTimestamp =
                settingsDataHolder.recoverDataLastTimestamp ?: recoverDataLastTimestamp
            this.recoverDataLastTimeAccessed =
                settingsDataHolder.recoverDataLastTimeAccessed ?: recoverDataLastTimeAccessed
            this.versionCode = settingsDataHolder.versionCode ?: versionCode
            this.termsAndConditionsVersion =
                settingsDataHolder.termsAndConditionsVersion ?: termsAndConditionsVersion
            this.termsAndConditionsVersion =
                settingsDataHolder.termsAndConditionsVersion ?: termsAndConditionsVersion
            this.myWalksTimestamp =
                settingsDataHolder.myWalksTimestamp ?: myWalksTimestamp
            this.appUpdateLastTimestamp =
                settingsDataHolder.appUpdateLastTimestamp ?: appUpdateLastTimestamp
            this.wasAskedMotionPermission =
                settingsDataHolder.wasAskedMotionPermission ?: wasAskedMotionPermission
            this.rewardBadgesStartTimestamp =
                settingsDataHolder.rewardBadgesStartTimestamp ?: rewardBadgesStartTimestamp
            this.earnedBadges = settingsDataHolder.earnedBadges ?: earnedBadges
            this.readArticles = settingsDataHolder.readArticles ?: readArticles
            this.shouldShowRewardOnboarding =
                settingsDataHolder.shouldShowRewardOnboarding ?: shouldShowRewardOnboarding
            this.shouldCheckForLostData =
                settingsDataHolder.shouldCheckForLostData ?: shouldCheckForLostData
            this.showMissingAlertSooner =
                settingsDataHolder.showMissingAlertSooner ?: showMissingAlertSooner
            this.hadLostData = settingsDataHolder.hadLostData ?: hadLostData
            this.nrOfRetryLostDada = settingsDataHolder.nrOfRetryLostDada ?: nrOfRetryLostDada
            this.lastRetryLostData = settingsDataHolder.lastRetryLostData ?: lastRetryLostData
            this.shouldSeeRetrieveErrorAlerted =
                settingsDataHolder.shouldSeeRetrieveErrorAlerted ?: shouldSeeRetrieveErrorAlerted
            this.shouldFailRetrieveDataTest =
                settingsDataHolder.shouldFailRetrieveDataTest ?: shouldFailRetrieveDataTest
            this.areUserDetailsSet = settingsDataHolder.areUserDetailsSet ?: areUserDetailsSet
            this.isOnboardingFinished = settingsDataHolder.isOnboardingFinished ?: isOnboardingFinished
            this.isWidgetIntroduceShown = settingsDataHolder.isWidgetIntroduceShown ?: isWidgetIntroduceShown
            this.shouldShowUpdateAppDialog = settingsDataHolder.shouldShowUpdateAppDialog ?: shouldShowUpdateAppDialog
            this.shouldShowRetrieveDataDialog = settingsDataHolder.shouldShowRetrieveDataDialog ?: shouldShowRetrieveDataDialog
            this.shouldShowUpdateTermsDialog = settingsDataHolder.shouldShowUpdateTermsDialog ?: shouldShowUpdateTermsDialog
            this.highAchieversLastShowDate = settingsDataHolder.highAchieversLastShowDate ?: highAchieversLastShowDate
            this.highAchieversDoNotAskMeAgain = settingsDataHolder.highAchieversDoNotAskMeAgain ?: highAchieversDoNotAskMeAgain
            this.earnedHighAchieversBadgeTarget = settingsDataHolder.earnedHighAchieversBadgeTarget ?: earnedHighAchieversBadgeTarget
            this.heroPopupsShown = settingsDataHolder.heroPopupsShown ?: heroPopupsShown
            this.campaignBannersShown = settingsDataHolder.campaignBannersShown ?: campaignBannersShown
            this.classicUser = settingsDataHolder.classicUser ?: classicUser
            this.nhsUser = settingsDataHolder.nhsUser ?: nhsUser
            this.nhsToken = settingsDataHolder.nhsToken ?: nhsToken
            this.nhsLastSyncTime = settingsDataHolder.nhsLastSyncTime ?: nhsLastSyncTime
            this.nhsLastSyncUserId = settingsDataHolder.nhsLastSyncUserId ?: nhsLastSyncUserId
        }.also {
            saveSettingsHolder(savedSettingsHolder)
        }
    }

    fun getSettingsHolder(): SettingsDataHolder {
        val settingsHolderJson =
            getSettingsPreferences().getString(SETTINGS_DATA_HOLDER_KEY, null)

        settingsHolderJson ?: return SettingsDataHolder()

        return try {
            appGson.fromJson(settingsHolderJson, SettingsDataHolder::class.java)
        } catch (ex: JsonSyntaxException) {
            Timber.d("Failed to parse settings. Error: ${ex.message}")
            SettingsDataHolder()
        }
    }

    fun saveSettingsHolder(settingsDataHolder: SettingsDataHolder) {
        val settingsHolderJson =
            appGson.toJson(settingsDataHolder, SettingsDataHolder::class.java)

        getSettingsPreferences().edit()
            .putString(SETTINGS_DATA_HOLDER_KEY, settingsHolderJson)
            .commit()
    }

    private fun getSettingsPreferences(): SharedPreferences {
        return context.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
    }
}