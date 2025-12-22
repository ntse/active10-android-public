package com.flipsidegroup.active10.presentation.home.presenter

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.flipside.briskcounter.data.BriskActivity
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.LegalRules
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.NhsSyncRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.home.view.HomeView
import com.flipsidegroup.active10.presentation.onboarding.fragments.TERMS_AND_CONDITIONS
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.services.MyRecoverLostWalkService
import com.flipsidegroup.active10.services.MyWalksService
import com.flipsidegroup.active10.services.RecoverLostWalkService
import com.flipsidegroup.active10.services.RecoverWalksService
import com.flipsidegroup.active10.services.WalkingPlanService
import com.flipsidegroup.active10.services.widget.MediumWidget
import com.flipsidegroup.active10.services.widget.SmallWidget
import com.flipsidegroup.active10.utils.Constants.RetrieveLostData.NUMBER_OF_RETRIEVE_DATA_TRIES
import com.flipsidegroup.active10.utils.Constants.RetrieveLostData.RETRIEVE_LOST_DATA_DAY
import com.flipsidegroup.active10.utils.Constants.RetrieveLostData.RETRIEVE_LOST_DATA_MONTH
import com.flipsidegroup.active10.utils.Constants.RetrieveLostData.RETRIEVE_LOST_DATA_YEAR
import com.flipsidegroup.active10.utils.Constants.SEPTEMBER_2019_TIMESTAMP
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.END_DATE
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.START_DATE
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.RetrieveDataReceiver
import com.flipsidegroup.active10.utils.WalkDataGenerator
import com.flipsidegroup.active10.utils.WalksUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.onDataLoaded
import com.flipsidegroup.active10.utils.valueOrNull
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialog
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialogType
import com.phe.betterhealth.components.moodbottomdialog.BHRelatedArticle
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.absoluteValue


private const val REVIEW_REPEAT_DAYS = 7
private const val REVIEW_REPEAT_A10 = 5
private const val ACTIVE_10 = 10
private const val DEBUG_ENTER_IN_RECOVER_DATA_LIMIT = 5
private const val THREE_ACTIVE_10 = 3 * ACTIVE_10

private const val THREE_DAY_STREAK_DAYS = 3
private const val PERFECT_WEEK_DAYS = 7
private const val TWO_WEEK_STREAK_DAYS = 14
private const val PERFECT_MONTH_DAYS = 28

private const val HUNDRED_CLUB = 100
private const val TWO_HUNDRED_CLUB = 250
private const val FIVE_HUNDRED_CLUB = 500
private const val THOUSAND_CLUB = 1000
private const val TWO_THOUSAND_CLUB = 2000
private const val THREE_THOUSAND_CLUB = 3000

private const val HIGH_FIVE_DAYS = 5
private const val WONDER_WEEK_DAYS = 7
private const val FIGHTING_FIT_BRISK = 150
private const val HEALTHY_HIKER_BRISK = 150
private const val HEALTHY_HIKER_BRISK_WEEKS = 2
private const val QUICK_MARCH_BRISK = 300

class HomePresenterImpl @Inject constructor(
    private val context: Context,
    private val settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val discoverRepository: DiscoverRepository,
    private val screenRepository: ScreenRepository,
    private val preferenceRepository: PreferenceRepository,
    private val loginRepository: LoginRepository,
    private val nhsSyncRepository: NhsSyncRepository,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) : BasePresenter<HomeView>(), HomePresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    private var currentDayBrisk: Int = 0

    override fun retrieveLostDataFrom5thJanuary(resultReceiver: RetrieveDataReceiver?) {
        val retryNr = settingsUtils.getSettingsHolder().nrOfRetryLostDada ?: 0
        val lastRetry = settingsUtils.getSettingsHolder().lastRetryLostData ?: 0

        if (settingsUtils.getSettingsHolder().shouldCheckForLostData == false ||
            settingsUtils.getSettingsHolder().hadLostData != null ||
            retryNr >= NUMBER_OF_RETRIEVE_DATA_TRIES || DateHelper.isSameDay(lastRetry)
        ) {
            settingsUtils.updateSettings(SettingsDataHolder(shouldCheckForLostData = false))
            if (settingsUtils.getSettingsHolder().shouldShowRetrieveDataDialog == true) {
                view?.showRetrieveDataDialog()
            }
            return
        }

        val installationDate = DateHelper.getInstalledDate(context).timeInMillis
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, RETRIEVE_LOST_DATA_YEAR)
        calendar.set(Calendar.MONTH, RETRIEVE_LOST_DATA_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, RETRIEVE_LOST_DATA_DAY)
        var start = DateHelper.getStartOfDay(calendar).timeInMillis

        if (installationDate > start) {
            start = installationDate
        }

        var end = settingsUtils.getSettingsHolder().lastMyWalksSavedTimestamp
        if (end == null || DateHelper.isSameDay(end)) {
            val endCalendar = Calendar.getInstance()
            endCalendar.add(Calendar.DAY_OF_YEAR, -1)
            end = DateHelper.getEndOfDay(endCalendar).timeInMillis
        }

        val activitiesDays = localRepository.getActivitiesDaysNumber(start, end).toLong()
        val diffDays = DateHelper.getDateDiffInDays(start, end) + 1
        if (activitiesDays == diffDays) {
            settingsUtils.updateSettings(SettingsDataHolder(shouldCheckForLostData = false))
            return
        }

        settingsUtils.updateSettings(SettingsDataHolder(shouldCheckForLostData = true))
        view?.showRetrieveDataDialog()

        localRepository.getSortedActivitiesOnDays(
            start,
            end,
            object : AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                override fun onDataLoaded(data: List<StepOverview>) {
                    val intent = context.MyRecoverLostWalkService(
                        startTimestamp = findFirstMissingDay(start, data) + 1,
                        endTimestamp = findLastMissingDay(end, data) - 1,
                        resultReceiver = resultReceiver
                    )
                    RecoverLostWalkService.enqueueWork(context, intent)
                }
            })
    }

    override fun checkForShowingHeroPopup() {
        screenRepository.getScreenContentBySlug(ScreenRepository.SLUG_HERO_POPUP)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { content ->
                    checkForShowingHeroPopup(content)
                },
                { error ->
                    Timber.d(error, "cannot get hero popup.")
                }
            ).addToDisposables()
    }

    override fun checkForShowingDeepLinkDialog() {
        screenRepository.getScreenContentBySlug(ScreenRepository.SLUG_DEEP_LINKING_MODAL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { content ->
                    checkIfShouldShowDeepLinkDialog(content)
                },
                { error ->
                    Timber.d(error, "cannot get deep link popup.")
                }
            ).addToDisposables()
    }

    private fun checkForShowingHeroPopup(content: ScreenContent) {
        val versionName = content.getPropertyValue("popup_version") ?: return
        val startDate = content.getPropertyValue("start_date")?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it)
        }
        val endDate = Calendar.getInstance().apply {
            content.getPropertyValue("end_date")?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it)
            }?.let { time = it }
        }.let { DateHelper.getEndOfDay(it) }.time

        val shownPopups = settingsUtils.getSettingsHolder().heroPopupsShown ?: emptyList()

        if ((!shownPopups.contains(content.slug.plus(versionName))
                    && startDate?.before(Date()) == true) && endDate.after(Date())
        ) {
            view?.showHeroPopupDialog(content)
            settingsUtils.updateSettings(
                SettingsDataHolder(
                    heroPopupsShown = shownPopups.plus(
                        content.slug.plus(versionName)
                    )
                )
            )
        } else {
            checkForShowingWidgetIntroduceDialog()
        }
    }

    private fun checkIfShouldShowDeepLinkDialog(content: ScreenContent) {
        val simpleDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = content.getPropertyValue(START_DATE)?.let { runCatching { simpleDateFormatter.parse(it) }.getOrNull() }
        val endDate = content.getPropertyValue(END_DATE)?.let { runCatching { simpleDateFormatter.parse(it) }.getOrNull() }
        val today = Date()

        val dialogId = "${content.slug}${content.title.hashCode()}${content.description.hashCode()}"
        val shownPopups = settingsUtils.getSettingsHolder().heroPopupsShown ?: emptyList()

        if (!shownPopups.contains(dialogId) && startDate?.before(today) == true && endDate?.after(today) == true) {
            view?.showDeepLinkDialog()
            settingsUtils.updateSettings(SettingsDataHolder(heroPopupsShown = shownPopups.plus(dialogId)))
        }
    }

    private fun checkForShowingWidgetIntroduceDialog() {
        if (settingsUtils.getSettingsHolder().isWidgetIntroduceShown != true && !checkIfWidgetIsInstalled()) {
            view?.showIntroduceWidgetDialog()
            settingsUtils.updateSettings(SettingsDataHolder(isWidgetIntroduceShown = true))
        }
    }

    override fun continueFromIntroduceWidgetDialog() {
        discoverRepository
            .getAndroidWidgetArticle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { article ->
                    if (article.isAvailable) article.get()
                        ?.let { view?.goToArticle(it, "hero_popup") }
                },
                { error -> Timber.e(error, "get widget introduce article error") }
            ).addToDisposables()
    }

    override fun continueFromHeroPopup(content: ScreenContent) {
        content.getPropertyValue("article_slug")?.let { slug ->
            discoverRepository
                .getArticleBySlug(slug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { article ->
                        article.valueOrNull()?.let { page ->
                            if (page.categoryView == "link") {
                                view?.navigateToUrl(page.destination?.android)
                            } else {
                                view?.goToArticle(page, "hero_popup")
                            }
                        }
                    },
                    { error -> Timber.e(error, "get hero popup article error") }
                ).addToDisposables()
        }
    }

    override fun highAchieversUpdateLastShowDate() {
        settingsUtils.updateSettings(SettingsDataHolder(highAchieversLastShowDate = DateHelper.getCurrentTimestamp()))
    }

    override fun highAchieversDontAskMeGain() {
        settingsUtils.updateSettings(SettingsDataHolder(highAchieversDoNotAskMeAgain = true))
    }

    override fun nhsUserLogout() {
        view?.showLoading()

        loginRepository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("User logged out")
                removeNhsUserDataUseCase()
                view?.hideLoading()
                view?.checkForShowingNhsLogBackInDialog()
            }, { error ->
                Timber.e("Error logging out, but we still log out user from the app. $error")
                removeNhsUserDataUseCase()
                view?.hideLoading()
                view?.checkForShowingNhsLogBackInDialog()

            }).addToDisposables()
    }

    override fun syncLastActivitiesAndRewardsToNhs() {
        nhsSyncRepository.trySyncLastActivitiesAndRewards()
        Completable.timer(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view?.checkForShowingNhsLogBackInDialog()
            }.addToDisposables()
    }

    private fun checkIfWidgetIsInstalled() =
        AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, MediumWidget::class.java)).isNotEmpty() ||
                AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, SmallWidget::class.java)).isNotEmpty()

    private fun findFirstMissingDay(start: Long, data: List<StepOverview>): Long {
        if (data.isNullOrEmpty()) {
            return start
        }

        var prev: Long = data[0].timestamp ?: return start
        if (!DateHelper.isSameDay(prev, start)) {
            return start
        }

        if (data.size == 1) {
            return DateHelper.getEndTimestampOfDay(start)
        }

        var diff: Long
        var current: Long?
        for (i in 1 until data.size) {
            current = data[i].timestamp ?: DateHelper.getEndTimestampOfDay(prev)
            diff = DateHelper.getDateDiffInDays(prev, current)
            if (diff > 1) {
                return DateHelper.getEndTimestampOfDay(prev) + 1
            }
            prev = current
        }

        return DateHelper.getEndTimestampOfDay(prev)
    }

    private fun findLastMissingDay(end: Long, data: List<StepOverview>): Long {
        if (data.isNullOrEmpty()) {
            return end
        }

        var next: Long = data[data.size - 1].timestamp ?: return end
        if (!DateHelper.isSameDay(next, end)) {
            return end
        }

        if (data.size == 1) {
            return DateHelper.getStartTimestampOfDay(end)
        }

        var diff: Long
        var current: Long?
        for (i in data.size - 2 downTo 0) {
            current = data[i].timestamp ?: DateHelper.getEndTimestampOfDay(next)
            diff = DateHelper.getDateDiffInDays(current, next)
            if (diff > 1) {
                return DateHelper.getStartTimestampOfDay(next) - 1
            }
            next = current
        }

        return DateHelper.getStartTimestampOfDay(next)
    }

    override fun getMyWalksData() {
        val isFitnessMotionEnabled = settingsUtils.getSettingsHolder().isFitnessMotionEnabled
        if (isFitnessMotionEnabled != true) {
            return
        }
        val lastMyWalksSavedTimestamp =
            settingsUtils.getSettingsHolder().lastMyWalksSavedTimestamp ?: return

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)

        val isSameDay = DateHelper.isSameDay(lastMyWalksSavedTimestamp)
        if (!isSameDay) {
            val serviceIntent = context.MyWalksService(
                startTimestamp = lastMyWalksSavedTimestamp,
                endTimestamp = calendar.timeInMillis
            )
            MyWalksService.enqueueWork(context, serviceIntent)
        }

        getWalkingPlanData()
    }

    private fun getWalkingPlanData() {
        val planServiceIntent = context.WalkingPlanService()
        WalkingPlanService.enqueueWork(context, planServiceIntent)
    }

    override fun checkMoodDialog(moodType: BHMoodBottomDialogType?) {
        moodType ?: return

        presenterScope.launch {
            val slug = when (moodType) {
                BHMoodBottomDialogType.GREAT -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_GREAT
                BHMoodBottomDialogType.GOOD -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_GOOD
                BHMoodBottomDialogType.OK -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_OK
                BHMoodBottomDialogType.TIRED -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_BAD
            }

            runCatching {
                val screenContent = screenRepository.getScreenContentBySlug(slug).await()
                val firstArticle = discoverRepository.getArticles().await()
                    .singleOrNull { it.id == screenContent.infoPageIds?.firstOrNull() }

                screenContent to firstArticle
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { (screenContent, firstArticle) ->
                    val moodDialog = BHMoodBottomDialog().apply {
                        titleText = screenContent?.title
                        subtitleText = screenContent?.description
                        descriptionText = screenContent?.getPropertyValue("first_label_text")
                        buttonText = screenContent?.getPropertyValue("first_button_title")
                        dialogType = moodType
                        article = firstArticle?.let {
                            BHRelatedArticle(
                                categoryLabel = it.categoryLabel,
                                title = it.title,
                                imageUrl = it.imageUrl,
                                isLink = it.categoryView == "link",
                            )
                        }
                        onArticleClickListener = {
                            firstArticle?.let {
                                this@HomePresenterImpl.view?.goToArticle(it, "home")
                            }
                        }
                    }

                    view?.showMoodBottomDialog(moodDialog)
                }
        }
    }

    override fun checkHighAchieversForShowing() {
        checkHighAchieversBadgesForShowing()
        checkReachingTargetForTwoWeeks()
    }

    private fun checkHighAchieversBadgesForShowing() {
        listOf(
            RewardBadgeEnum.HIGH_ACHIEVER,
            RewardBadgeEnum.AIMING_HIGH,
            RewardBadgeEnum.TARGET_CHASER
        ).forEach {
            getEarnedRewardBadge(it)?.let { earnedBadge ->
                if (!earnedBadge.wasShown) {
                    showBadge(it)
                }
            }
        }
    }

    override fun recoverWalksData() {
        val isFitnessMotionEnabled = settingsUtils.getSettingsHolder().isFitnessMotionEnabled
        if (isFitnessMotionEnabled != true) {
            return
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)

        var firstInstallTime =
            context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime

        if (firstInstallTime < SEPTEMBER_2019_TIMESTAMP || BuildConfig.DEBUG) {
            firstInstallTime = SEPTEMBER_2019_TIMESTAMP
        }

        val recoverDataLastTimestamp = settingsUtils.getSettingsHolder().recoverDataLastTimestamp
        val lastTimeAccessed = settingsUtils.getSettingsHolder().recoverDataLastTimeAccessed

        if (!DateHelper.isSameDay(firstInstallTime) && (recoverDataLastTimestamp == null || recoverDataLastTimestamp > firstInstallTime)) {
            val shouldRecoverData = if (BuildConfig.DEBUG) {
                lastTimeAccessed == null || DateHelper.getDateDiffInMinutes(lastTimeAccessed).absoluteValue >= DEBUG_ENTER_IN_RECOVER_DATA_LIMIT
            } else {
                lastTimeAccessed == null || !DateHelper.isSameDay(lastTimeAccessed)
            }

            if (shouldRecoverData) {
                val intent = context.RecoverWalksService(
                    startTimestamp = firstInstallTime,
                    endTimestamp = recoverDataLastTimestamp ?: calendar.timeInMillis
                )
                RecoverWalksService.enqueueWork(context, intent)
            }
        }
    }

    override fun getGlobalRules() {
        localRepository.getGlobalRules(object : AppDatabase.OnDataLoadedListener<GlobalRules?> {
            override fun onDataLoaded(gr: GlobalRules?) {
                localRepository.getLegalRules(object :
                    AppDatabase.OnDataLoadedListener<List<LegalRules>> {
                    override fun onDataLoaded(data: List<LegalRules>) {
                        gr?.termsAndConditions?.latestVersion =
                            data.firstOrNull { it.pageType == TERMS_AND_CONDITIONS }?.version.toString()
                        view?.onGlobalRulesRetrieved(gr)
                    }
                })
            }
        })
    }

    override fun checkReviewForShowing() {
        val hasReview = settingsUtils.getSettingsHolder().hasLeftReview
        if (hasReview == true) {
            return
        }

        val lastReviewTimestamp = settingsUtils.getSettingsHolder().lastReviewShownTimestamp
            ?: DateHelper.getInstalledDate(context).timeInMillis
        val reviewStartTimestamp = DateHelper.getStartTimestampOfDay(lastReviewTimestamp)
        val daysDiff = DateHelper.getDateDiffInDays(reviewStartTimestamp)

        if (daysDiff >= REVIEW_REPEAT_DAYS) {
            localRepository.getActivitiesOnDays(
                reviewStartTimestamp,
                DateHelper.getCurrentTimestamp(),
                object : AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                    override fun onDataLoaded(data: List<StepOverview>) {
                        var targetDays = 0

                        for (stepOverview in data) {
                            if ((stepOverview.totalBriskMin ?: 0) >= ACTIVE_10) {
                                targetDays++
                            }
                        }

                        if (targetDays >= REVIEW_REPEAT_A10) {
                            settingsUtils.updateSettings(SettingsDataHolder(lastReviewShownTimestamp = DateHelper.getCurrentTimestamp()))
                            view?.showReviewDialog()
                        }
                    }
                })
        }
    }

    private fun checkReachingTargetForTwoWeeks() {
        // Check if user reach the target everyday in last 2 weeks. For High Achievers functionality.
        val target = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET

        getAllOfWalkData { data ->
            val updatedData = data
                .let { list ->
                    val lastTimeStamp = list.lastOrNull()?.timestamp ?: 0L
                    if (DateHelper.isSameDay(lastTimeStamp, System.currentTimeMillis())) {
                        list
                    } else {
                        list.plus(StepOverview(DateHelper.getCurrentTimestamp(), currentDayBrisk))
                    }
                }
                .let { WalksUtils.fillMissingDaysWithEmptyData(it) }

            val dayHitTarget = updatedData.takeLast(14)
                .filter { (it.totalBriskMin ?: 0) >= target.times(10) }
                .size

            preferenceRepository.daysHitTargetInLastTwoWeeks = dayHitTarget

            if (target <= 2 && isTargetSucceed(updatedData, target)) {
                showHighAchieversDialog(target)
            } else if (target > 2 && isTargetSucceed(updatedData, target)) {
                checkHighAchieversBadgeForShowing(target) {
                    settingsUtils.updateSettings(SettingsDataHolder(earnedHighAchieversBadgeTarget = target))
                    showHighAchieversDialog(target)
                }
            }
        }
    }

    private fun isTargetSucceed(allData: List<StepOverview>, target: Int): Boolean {
        return when (target) {
            in 0..2 -> WalksUtils.isTargetSuccess14TimesInAny2WeeksInRow(allData, target)
            else -> WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(allData, target)
        }
    }

    private fun getAllOfWalkData(callback: (List<StepOverview>) -> Unit) {
        if (settingsUtils.getSettingsHolder()
                .let { it.generate1Active10WalkData == true || it.generate5Active10WalkData == true }
        ) {
            getAllOfDataFromGenerator(callback)
        } else {
            getAllOfDataFromDatabase(callback)
        }
    }

    private fun getAllOfDataFromDatabase(callback: (List<StepOverview>) -> Unit) {
        localRepository.getAllActivities(
            onDataLoaded = onDataLoaded { data ->
                callback(data)
            }
        )
    }

    private fun getAllOfDataFromGenerator(callback: (List<StepOverview>) -> Unit) {
        callback(WalkDataGenerator.getStepData())
    }

    private fun showHighAchieversDialog(currentTarget: Int) {
        val settings = settingsUtils.getSettingsHolder()
        val isDoNotAskAgain = settings.highAchieversDoNotAskMeAgain == true
        val isLessThanTwoWeeksAfterLastShow =
            (settings.highAchieversLastShowDate ?: 0) > DateHelper.getStartOfDay2WeeksAgo().time

        if (isDoNotAskAgain || isLessThanTwoWeeksAfterLastShow || currentTarget >= 5) {
            return
        }

        view?.showHighAchieversDialog(settings.highAchieversLastShowDate != null)
    }

    private fun checkHighAchieversBadgeForShowing(
        currentTarget: Int,
        afterShowingDialog: () -> Unit
    ) {
        if (getEarnedRewardBadge(RewardBadgeEnum.HIGH_ACHIEVER) == null) {
            showBadge(RewardBadgeEnum.HIGH_ACHIEVER, true, afterShowingDialog)
        } else if (getEarnedRewardBadge(RewardBadgeEnum.TARGET_CHASER) == null
            && getEarnedRewardBadge(RewardBadgeEnum.AIMING_HIGH) != null
            && (getEarnedRewardBadge(RewardBadgeEnum.AIMING_HIGH)?.timestamp
                ?: Long.MAX_VALUE) < DateHelper.getStartOfDay2WeeksAgo().time
            && currentTarget >= 4
        ) {
            showBadge(RewardBadgeEnum.TARGET_CHASER, true, afterShowingDialog)
        } else {
            afterShowingDialog()
        }
    }

    private fun getEarnedRewardBadge(badge: RewardBadgeEnum) =
        settingsUtils.getSettingsHolder().earnedBadges
            ?.find { it.id == badge.id }

    private fun showBadge(
        badge: RewardBadgeEnum,
        saveBadgeInEarnedBadges: Boolean = false,
        afterShowingDialog: () -> Unit = {}
    ) {
        if (saveBadgeInEarnedBadges) EarnBadgeHelper.saveEarnedBadge(
            settingsUtils = settingsUtils,
            badge = badge,
            preferenceRepository = preferenceRepository
        )
        localRepository.getRewardBadges(onDataLoaded { data ->
            val rewardBadge = data.find { it.id == badge.id }
            if (rewardBadge != null) {
                view?.showRewardDialog(rewardBadge, afterShowingDialog)
                EarnBadgeHelper.markBadgeAsShown(rewardBadge.id, settingsUtils)
            } else {
                afterShowingDialog()
            }
        })
    }

    override fun checkForActivitiesBadges(briskActivity: BriskActivity?) {
        currentDayBrisk = briskActivity?.minutesOfBrisk ?: 0
        checkHighAchieversForShowing()

        if (briskActivity != null && briskActivity.minutesOfBrisk == 0) {
            return
        }

        val goal = settingsUtils.getSettingsHolder().goalsList?.firstOrNull()
        val earningsBadges = settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()
        if (goal != null && earningsBadges.find { it.id == RewardBadgeEnum.GOAL_SETTER.id } == null) {
            EarnBadgeHelper.saveEarnedBadgeWithoutCheck(
                settingsUtils,
                preferenceRepository,
                RewardBadgeEnum.GOAL_SETTER,
                DateHelper.getInstalledDate(context).timeInMillis,
            )
        }

        if (settingsUtils.getSettingsHolder().generateRandomWalkData == true) {
            val stepData = WalkDataGenerator.getStepData()
            checkForActivityBadges(stepData, briskActivity)
            return
        }

        val startDay = settingsUtils.getSettingsHolder().rewardBadgesStartTimestamp
            ?: DateHelper.getStartTimestampOfDay(System.currentTimeMillis())
        val endDay = DateHelper.getEndTimestampOfDay(System.currentTimeMillis())

        localRepository.getSortedActivitiesOnDays(
            startDay,
            endDay,
            object : AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                override fun onDataLoaded(data: List<StepOverview>) {
                    checkForActivityBadges(data, briskActivity)
                }
            })
    }

    private fun checkForActivityBadges(data: List<StepOverview>, briskActivity: BriskActivity?) {
        val earnedBadges =
            settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()

        checkForTargetChaserBadges(data, briskActivity, earnedBadges)
        checkForBriskMinutesBadges(data, briskActivity, earnedBadges)
        checkForSteppingUpBadges(data, briskActivity, earnedBadges)
    }

    private fun checkForTargetChaserBadges(
        data: List<StepOverview>,
        briskActivity: BriskActivity?,
        earnedBadges: ArrayList<EarnRewardBadge>
    ) {
        val todayTarget =
            settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: 1

        val moveAndShakerBadge = earnedBadges.find { it.id == RewardBadgeEnum.MOVER_AND_SHAKER.id }
        if (moveAndShakerBadge == null || DateHelper.isSameDay(moveAndShakerBadge.timestamp)) {
            if (briskActivity == null) {
                checkForFirstA10Badge(data)
            } else if (briskActivity.minutesOfBrisk >= ACTIVE_10) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.MOVER_AND_SHAKER,
                    preferenceRepository = preferenceRepository
                )
            }
        }

        val onTargetBadge = earnedBadges.find { it.id == RewardBadgeEnum.ON_TARGET.id }
        if (onTargetBadge == null || DateHelper.isSameDay(onTargetBadge.timestamp)) {
            if (briskActivity == null) {
                checkForOnTargetBadge(data)
            } else if (briskActivity.minutesOfBrisk >= todayTarget * ACTIVE_10) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.ON_TARGET,
                    preferenceRepository = preferenceRepository
                )
            }
        }

        if (earnedBadges.find { it.id == RewardBadgeEnum.THREE_DAYS_STREAK.id } == null) {
            checkForTargetDaysStreakBadge(
                THREE_DAY_STREAK_DAYS,
                RewardBadgeEnum.THREE_DAYS_STREAK,
                data,
                briskActivity,
                todayTarget
            )
        }

        if (earnedBadges.find { it.id == RewardBadgeEnum.PERFECT_WEEK.id } == null) {
            checkForTargetDaysStreakBadge(
                PERFECT_WEEK_DAYS,
                RewardBadgeEnum.PERFECT_WEEK,
                data,
                briskActivity,
                todayTarget
            )
        }

        if (earnedBadges.find { it.id == RewardBadgeEnum.TWO_WEEK_STREAK.id } == null) {
            checkForTargetDaysStreakBadge(
                TWO_WEEK_STREAK_DAYS,
                RewardBadgeEnum.TWO_WEEK_STREAK,
                data,
                briskActivity,
                todayTarget
            )
        }

        if (earnedBadges.find { it.id == RewardBadgeEnum.PERFECT_MONTH.id } == null) {
            checkForTargetDaysStreakBadge(
                PERFECT_MONTH_DAYS,
                RewardBadgeEnum.PERFECT_MONTH,
                data,
                briskActivity,
                todayTarget
            )
        }
    }

    private fun checkForBriskMinutesBadges(
        data: List<StepOverview>,
        briskActivity: BriskActivity?,
        earnedBadges: ArrayList<EarnRewardBadge>
    ) {
        val threeThousandBadge =
            earnedBadges.find { it.id == RewardBadgeEnum.THREE_THOUSAND_CLUB.id }
        if (threeThousandBadge == null || DateHelper.isSameDay(threeThousandBadge.timestamp)) {
            if (briskActivity == null) {
                checkForClubBadges(data)
            } else {
                checkForClubBadges(data.reversed(), briskActivity)
            }
        }
    }

    private fun checkForSteppingUpBadges(
        data: List<StepOverview>,
        briskActivity: BriskActivity?,
        earnedBadges: ArrayList<EarnRewardBadge>
    ) {
        val hatTrickBadge = earnedBadges.find { it.id == RewardBadgeEnum.HAT_TRICK.id }
        if (hatTrickBadge == null || DateHelper.isSameDay(hatTrickBadge.timestamp)) {
            if (briskActivity == null) {
                checkForHatTrickBadge(data)
            } else if (briskActivity.minutesOfBrisk >= THREE_ACTIVE_10) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.HAT_TRICK,
                    preferenceRepository = preferenceRepository
                )
            }
        }

        val quickMarchBadge = earnedBadges.find { it.id == RewardBadgeEnum.QUICK_MARCH.id }
        val healthHikerBadge = earnedBadges.find { it.id == RewardBadgeEnum.HEALTHY_HIKER.id }
        if (earnedBadges.find { it.id == RewardBadgeEnum.WONDER_WEEK.id } == null ||
            quickMarchBadge == null || healthHikerBadge == null ||
            DateHelper.isSameDay(quickMarchBadge.timestamp) || DateHelper.isSameDay(healthHikerBadge.timestamp)) {
            if (briskActivity == null) {
                checkForBriskWeekBadges(data)
            } else {
                checkForBriskWeekBadges(data.reversed(), briskActivity)
            }
        }
    }

    private fun checkForFirstA10Badge(data: List<StepOverview>) {
        val firstA10 =
            data.find { it.totalBriskMin != null && it.totalBriskMin!! >= ACTIVE_10 }
        if (firstA10 != null) {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.MOVER_AND_SHAKER,
                timestamp = firstA10.timestamp,
                preferenceRepository = preferenceRepository
            )
        }
    }

    private fun checkForOnTargetBadge(data: List<StepOverview>) {
        val targets = settingsUtils.getSettingsHolder().targetList ?: arrayListOf()
        var dayTarget = if (targets.isEmpty()) 1 else targets[0].target ?: 1

        for (activity in data) {
            if (activity.timestamp != null && dayTarget < activity.timestamp!!) {
                dayTarget = targets.find { target ->
                    target.timestamp > activity.timestamp!! && target.timestamp < activity.timestamp!!
                }?.target ?: 1
            }

            if (activity.totalBriskMin != null && activity.totalBriskMin!! >= dayTarget * ACTIVE_10) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.ON_TARGET,
                    timestamp = activity.timestamp,
                    preferenceRepository = preferenceRepository
                )
                break
            }
        }
    }

    private fun checkForTargetDaysStreakBadge(
        days: Int,
        badge: RewardBadgeEnum,
        data: List<StepOverview>,
        briskActivity: BriskActivity? = null,
        todayTarget: Int = 1
    ) {
        val daysInARaw: Int
        val subList: List<StepOverview>
        val listSize = data.size
        when {
            briskActivity == null -> {
                daysInARaw = days
                subList = data
            }

            listSize >= days - 1 -> {
                daysInARaw = days - 1
                subList = data.subList(listSize - days + 1, listSize)
            }

            else -> {
                return
            }
        }

        val activity = getActivityIfTargetDaysInRow(subList, daysInARaw)
        activity?.let {
            val timestamp = if (briskActivity != null) {
                if (briskActivity.minutesOfBrisk >= todayTarget * ACTIVE_10) {
                    System.currentTimeMillis()
                } else return
            } else {
                it.timestamp
            }
            EarnBadgeHelper.saveEarnedBadgeWithoutCheck(
                settingsUtils = settingsUtils,
                preferenceRepository = preferenceRepository,
                badge = badge,
                timestamp = timestamp
            )
        }
    }

    private fun checkForClubBadges(data: List<StepOverview>, briskActivity: BriskActivity? = null) {
        var briskMinutes = 0
        val dataList: ArrayList<StepOverview> = arrayListOf()
        dataList.addAll(data)
        if (briskActivity != null) {
            dataList.add(
                StepOverview(
                    System.currentTimeMillis(),
                    briskActivity.minutesOfBrisk,
                    briskActivity.minutesOfWalk,
                    briskActivity.totalSteps
                )
            )
        }

        for (activity in dataList) {
            briskMinutes += activity.totalBriskMin ?: 0
            if (briskMinutes >= HUNDRED_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.HUNDRED_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskMinutes >= TWO_HUNDRED_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.TWO_HUNDRED_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskMinutes >= FIVE_HUNDRED_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.FIVE_HUNDRED_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskMinutes >= THOUSAND_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.THOUSAND_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskMinutes >= TWO_THOUSAND_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.TWO_THOUSAND_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskMinutes >= THREE_THOUSAND_CLUB) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.THREE_THOUSAND_CLUB,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
                return
            }
        }
    }

    private fun checkForHatTrickBadge(data: List<StepOverview>) {
        val activity = data.find { it.totalBriskMin != null && it.totalBriskMin == THREE_ACTIVE_10 }
        activity?.let {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.HAT_TRICK,
                timestamp = activity.timestamp,
                preferenceRepository = preferenceRepository
            )
        }
    }

    private fun checkForBriskWeekBadges(
        data: List<StepOverview>,
        briskActivity: BriskActivity? = null
    ) {
        val dataList: ArrayList<StepOverview> = arrayListOf()
        dataList.addAll(data)
        if (briskActivity != null) {
            dataList.add(
                StepOverview(
                    System.currentTimeMillis(),
                    briskActivity.minutesOfBrisk,
                    briskActivity.minutesOfWalk,
                    briskActivity.totalSteps
                )
            )
        }

        var briskDays = 0
        var briskWeek = 0
        var hasWonderWeek = false
        var hasQuickMarch = false
        var healthyHikerWeek = -1
        var hasHealthyHiker = false
        var weekNr = 0

        for (activity in dataList) {
            if (weekNr != DateHelper.weekNumber(activity.timestamp)) {
                briskDays = 0
                briskWeek = 0
                weekNr = DateHelper.weekNumber(activity.timestamp)
                if (abs(weekNr - healthyHikerWeek) >= HEALTHY_HIKER_BRISK_WEEKS) {
                    healthyHikerWeek = -1
                }
            }

            briskWeek += activity.totalBriskMin ?: 0
            if (activity.totalBriskMin != null && activity.totalBriskMin!! > 0) {
                briskDays += 1
                if (briskDays == HIGH_FIVE_DAYS) {
                    EarnBadgeHelper.saveEarnedBadge(
                        settingsUtils = settingsUtils,
                        badge = RewardBadgeEnum.HIGH_FIVE,
                        timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                        preferenceRepository = preferenceRepository
                    )
                } else if (briskDays == WONDER_WEEK_DAYS) {
                    EarnBadgeHelper.saveEarnedBadge(
                        settingsUtils = settingsUtils,
                        badge = RewardBadgeEnum.WONDER_WEEK,
                        timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                        preferenceRepository = preferenceRepository
                    )
                    if (hasQuickMarch && hasHealthyHiker) {
                        return
                    } else {
                        hasWonderWeek = true
                    }
                }
            }

            if (briskWeek >= FIGHTING_FIT_BRISK) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.FIGHTING_FIT,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
            }

            if (briskWeek >= HEALTHY_HIKER_BRISK && healthyHikerWeek != weekNr) {
                if (healthyHikerWeek == -1) {
                    healthyHikerWeek = weekNr
                } else {
                    EarnBadgeHelper.saveEarnedBadge(
                        settingsUtils = settingsUtils,
                        badge = RewardBadgeEnum.HEALTHY_HIKER,
                        timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                        preferenceRepository = preferenceRepository
                    )
                    hasHealthyHiker = true
                    if (hasQuickMarch && hasWonderWeek) {
                        return
                    }
                }
            }

            if (briskWeek >= QUICK_MARCH_BRISK) {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.QUICK_MARCH,
                    timestamp = if (briskActivity == null) activity.timestamp else System.currentTimeMillis(),
                    preferenceRepository = preferenceRepository
                )
                if (hasWonderWeek && hasHealthyHiker) {
                    return
                } else {
                    hasQuickMarch = true
                }
            }
        }
    }

    private fun getActivityIfTargetDaysInRow(data: List<StepOverview>, days: Int): StepOverview? {
        val targets = settingsUtils.getSettingsHolder().targetList ?: arrayListOf()
        var dayTarget = if (targets.isEmpty()) 1 else targets[0].target ?: 1
        var dayInRow = 0
        for (activity in data) {
            if (activity.timestamp != null && dayTarget < activity.timestamp!!) {
                dayTarget = targets.find { target ->
                    target.timestamp > activity.timestamp!! && target.timestamp < activity.timestamp!!
                }?.target ?: 1
            }

            if (activity.totalBriskMin != null && activity.totalBriskMin!! >= dayTarget * ACTIVE_10) {
                dayInRow += 1
                if (dayInRow == days) {
                    return activity
                }
            } else {
                dayInRow = 0
            }
        }

        return null
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}
