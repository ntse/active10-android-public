package com.flipsidegroup.active10.presentation.todaywalk.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.dataholders.DeviceLocationHolder
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository.Companion.CAMPAIGN_BANNER
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.presentation.todaywalk.view.TodayWalkView
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.TodayWalkHeaderHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.hasPermissions
import com.flipsidegroup.active10.utils.isAppInstalled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import java.time.LocalDate
import javax.inject.Inject



private const val MIN_TIME_UPDATE_INTERVAL_IN_MS = 100L
private const val MIN_DISTANCE_UPDATE_INTERVAL = 0f
private const val NETWORK_PROVIDER = "network"

class TodayWalkPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val context: Context,
    private val localRepository: LocalRepository,
    private val todayWalkHeaderHelper: TodayWalkHeaderHelper,
    private val preferenceRepository: PreferenceRepository,
    private val screenRepository: ScreenRepository,
    private val analyticsHelper: FirebaseAnalyticsHelper
) : BasePresenter<TodayWalkView>(), TodayWalkPresenter, LocationListener {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    private var locationManager: LocationManager? = null
    private var walkingMessages: WalkingMessageResponse? = null

    override fun checkPaceCheckerIntro() {
        presenterScope.launch {
            if (preferenceRepository.isPaceCheckerNewUser && !preferenceRepository.isPaceCheckerIntroDismissed) {
                runCatching {
                    screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_WHAT_IS_BRISK)
                        .await()
                }
                    .onFailure { view?.showAlert(it) }
                    .onSuccess {
                        preferenceRepository.isPaceCheckerIntroDismissed = true
                        view?.showPaceCheckerIntro(it)
                    }

            }
        }
    }

    override fun checkMyWalksNewFeaturesBanner() {
        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.MY_WALKS_BANNER)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess {
                    view?.showMyWalksNewFeaturesBanner(it)
                }
        }
    }

    override fun checkMentalHealthBanner() {
        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.BH_MENTAL_BANNER).await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showMentalHealthBanner(it) }
        }
    }

    override fun checkCampaignBanner() {
        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(CAMPAIGN_BANNER).await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showCampaignBanner(it) }
        }
    }

    override fun shouldDisplayCampaignBanner(bannerId: String): Boolean {
        val shownPopups = settingsUtils.getSettingsHolder().campaignBannersShown ?: emptyList()
        return !shownPopups.contains(bannerId)
    }

    override fun markCampaignBannerAsSeen(bannerId: String) {
        val shownPopups = settingsUtils.getSettingsHolder().campaignBannersShown ?: emptyList()
        settingsUtils.updateSettings(SettingsDataHolder(campaignBannersShown = shownPopups.plus(bannerId)))
    }

    private var hitTheTarget = false

    override fun checkForIntroducingCoachApp(briskMinutes: Int) {
        val target = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET
        val hitTargetNow = (briskMinutes >= target.times(10))
        val targetChaserBadge = getTargetChaserBadge()
        if (preferenceRepository.coachAppIntroducedDate == null && targetChaserBadge != null && hitTargetNow && !hitTheTarget) {
            val daysSince = DateHelper.getDateDiffInDays(
                targetChaserBadge.timestamp,
                System.currentTimeMillis()
            )
            if (isCoachAppInstalled()) {
                analyticsHelper.logCoach5kTargetChaserAgainHas5K(daysSince)
            } else {
                view?.introduceCoachApp()
                analyticsHelper.logCoach5kTargetChaserAgain(daysSince)
            }
            preferenceRepository.coachAppIntroducedDate = LocalDate.now()
        }
        hitTheTarget = hitTargetNow
    }

    private fun getTargetChaserBadge() =
        settingsUtils.getSettingsHolder().earnedBadges
            ?.find { it.id == RewardBadgeEnum.TARGET_CHASER.id }

    private fun isCoachAppInstalled(): Boolean =
        context.isAppInstalled(Constants.CoachAppIntroduction.COACH_PACKAGE_NAME)

    //The location will be removed in exchange for postCode
    @SuppressLint("MissingPermission")
    override fun persistDeviceLocation() {
        val hasLocationPermission =
            context.hasPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!hasLocationPermission) {
            return
        }

        if (context.getSystemService(Context.LOCATION_SERVICE) is LocationManager) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        val gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation =
            locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        when {
            gpsLocation != null -> {
                saveDeviceLocation(gpsLocation)
                return
            }

            networkLocation != null -> {
                saveDeviceLocation(networkLocation)
                return
            }

            else -> registerLocationListener()
        }
    }

    override fun getWalkingMessages(totalBriskMin: Int) {
        if (walkingMessages == null) {
            localRepository.getWalkingMessages(object :
                AppDatabase.OnDataLoadedListener<WalkingMessageResponse?> {
                override fun onDataLoaded(data: WalkingMessageResponse?) {
                    walkingMessages = data
                    view?.onMessagesReceived(
                        todayWalkHeaderHelper.getHeaderText(
                            totalBriskMin,
                            walkingMessages
                        )
                    )
                }
            })
        } else {
            view?.onMessagesReceived(
                todayWalkHeaderHelper.getHeaderText(
                    totalBriskMin,
                    walkingMessages
                )
            )
        }
    }

    override fun getRewardBadges() {
        localRepository.getRewardBadges(object :
            AppDatabase.OnDataLoadedListener<List<RewardBadge>> {
            override fun onDataLoaded(data: List<RewardBadge>) {
                view?.onRewardsReceived(data)
            }
        })
    }

    override fun onLocationChanged(location: Location) {
        saveDeviceLocation(location)
        locationManager?.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    private fun saveDeviceLocation(location: Location?) {
        location ?: return

        val latitude = location.latitude
        val longitude = location.longitude
        settingsUtils.updateSettings(
            SettingsDataHolder(
                deviceLocationHolder = DeviceLocationHolder(
                    latitude,
                    longitude
                )
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener() {
        locationManager?.let {
            var networkProviderExists = false
            if (it.allProviders.contains(NETWORK_PROVIDER)) {
                networkProviderExists = true
            }

            if (networkProviderExists) {
                it.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_UPDATE_INTERVAL_IN_MS,
                    MIN_DISTANCE_UPDATE_INTERVAL,
                    this
                )
            }
        }
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}
