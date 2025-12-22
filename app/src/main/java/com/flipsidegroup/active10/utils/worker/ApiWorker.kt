package com.flipsidegroup.active10.utils.worker

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.hasInternetConnection
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class ApiWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        fun getRequest(): WorkRequest {
            return OneTimeWorkRequest.from(ApiWorker::class.java)
        }
    }

    @Inject
    internal lateinit var appRepository: AppRepository

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    private val disposables = CompositeDisposable()

    init {
        Active10App.appComponent.inject(this)
    }

    override fun doWork(): Result {
        if (!applicationContext.hasInternetConnection()) {
            Timber.d("No internet connection")
            FirebaseCrashlytics.getInstance().log(Constants.CMSAnalytics.CMS_FAILURE)
            return Result.failure()
        }

        // If CMS refreshed in last 24h stop and return
        if (!BuildConfig.DEBUG && !shouldRefreshCMS()) {
            return Result.success()
        }

        downloadData()
        return Result.success()
    }

    override fun onStopped() {
        disposables.dispose()
        super.onStopped()
    }

    private fun downloadData() {
        Timber.d("Start updating CMS")
        val goalsObs = appRepository.getGoalsList()
        val aboutObs = appRepository.getAboutCommunity()
        val faqObs = appRepository.getFaqList()
        val howItWorksObs = appRepository.getHowItWorksList()
        val tipsObs = appRepository.getTips()
        val walkingMessagesObs = appRepository.getWalkingMessages()
        val onboardingObs = appRepository.getOnboarding()
        val notificationsObs = appRepository.getNotifications()
        val globalRulesObs = appRepository.getGlobalRules()
        val legalRules = appRepository.getLegalList()
        val rewards = appRepository.getRewardList()
        val discoverCategories = appRepository.getDiscoverCategories()
        val discoverArticles = appRepository.getDiscoverArticles()
        val screenContents = appRepository.getScreenContents()
        val walkingPlans = appRepository.getWalkingPlans()

        Single.concat(
            arrayListOf(
                goalsObs,
                aboutObs,
                faqObs,
                howItWorksObs,
                tipsObs,
                walkingMessagesObs,
                onboardingObs,
                notificationsObs,
                globalRulesObs,
                rewards,
                legalRules,
                discoverCategories,
                discoverArticles,
                screenContents,
                walkingPlans
            )
        ).subscribe({
            Timber.d("CMD Data downloaded. Saving in the settings")
            settingsUtils.updateSettings(
                SettingsDataHolder(
                    lastCMSRefresh = DateHelper.getCurrentTimestamp(),
                    versionCode = BuildConfig.VERSION_CODE
                )
            )
        }, {
            Timber.w("Update CMS failure", it)
        }, {
            Timber.d("Completed")
        }).addTo(disposables)
    }

    private fun shouldRefreshCMS(): Boolean {
        if (settingsUtils.getSettingsHolder().versionCode == null
            || settingsUtils.getSettingsHolder().versionCode!! < BuildConfig.VERSION_CODE
        ) {
            return true
        }

        val appLastUpdateTime = settingsUtils.getSettingsHolder().appUpdateLastTimestamp
        val lastCMSRefresh = settingsUtils.getSettingsHolder().lastCMSRefresh
        lastCMSRefresh?.let {
            if (DateHelper.isSameDay(it)) {
                if (appLastUpdateTime != null && appLastUpdateTime > lastCMSRefresh) return true
                return false
            }
        }

        return true
    }


}