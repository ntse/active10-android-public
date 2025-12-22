package com.flipsidegroup.active10.utils.analytics

import android.os.Bundle
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.ACCOUNT_TYPE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.ACCOUNT_TYPE_GUEST
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.ACCOUNT_TYPE_NHS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.APP_VERSION
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.ARTICLE_SLUG
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.ARTICLE_TITLE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.DEVICE_ID
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_ABOUT_YOU
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_ARTICLE_READ
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_DISCOVER_SEARCH
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_NOTIFICATION_SENT
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_NOTIFICATION_TAPPED
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_POPUP_CANCELED
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_ACTIVITY_LEVEL
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_AGE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_GENDER
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.OS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.PARAM_NOTIFICATION_NAME
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.PARAM_RESULT_NUMBER
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.PARAM_SEARCH_TERM
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.SLUG
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.SOURCE
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.asLong
import com.flipsidegroup.active10.utils.buildWalkingMinutesArrayList
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class FirebaseAnalyticsHelper(
    private val settingsUtils: SettingsUtils,
    private val preferenceRepository: PreferenceRepository,
) {

    private var firebaseAnalytics: FirebaseAnalytics =
        FirebaseAnalytics.getInstance(UIUtils.getAppContext())

    private var mhInfoPage: InfoPage? = null
    private var isIDidTtClicked = false
    private var feeling: String? = null
    private val mhArticles = mutableListOf<InfoPage>()
    private val mhFeelingArticles = mutableListOf<InfoPage>()

    fun sendFirebaseEvent(eventName: String, bundle: Bundle) {
        bundle.putString(ACCOUNT_TYPE, if (preferenceRepository.isUserLoggedIn) ACCOUNT_TYPE_NHS else ACCOUNT_TYPE_GUEST)
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun saveEvent(event: String, bundle: Bundle? = null) {
        val b = bundle ?: Bundle()
        b.putString(
            DEVICE_ID,
            DeviceUtils.getDeviceId(settingsUtils)
        )
        b.putString(APP_VERSION, BuildConfig.VERSION_NAME)
        b.putString(OS, Constants.DEVICE_OS)
        b.putString(ACCOUNT_TYPE, if (preferenceRepository.isUserLoggedIn) ACCOUNT_TYPE_NHS else ACCOUNT_TYPE_GUEST)
        firebaseAnalytics.logEvent(event, b)
        Timber.d("FirebaseAnalyticsHelper: saveEvent: $event, bundle: $b")
    }

    fun saveRewardEvent(event: String, rewardSlug: String) {
        val bundle = Bundle()
        bundle.putString(Constants.FirebaseAnalytics.REWARD_SLUG, rewardSlug)
        saveEvent(event, bundle)
    }

    fun sendViewScreenEvent(screenClass: String, previousClass: String? = null, tab: String? = null) {
        saveEvent("ViewScreen", Bundle().apply {
            putString("screen_name", screenClass)
            previousClass?.let { putString("previous_screen_name", previousClass) }
            tab?.let { putString("selected_tab", tab) }
        })
    }

    fun sendArticleButtonClickedEvent(articleSlug: String, ctaName: String) {
        saveEvent("ButtonClick", Bundle().apply {
            putString("button_name", "cta")
            putString("source", "discover_$articleSlug")
            putString("cta_name", ctaName)
        })
    }

    fun sendButtonClickedEvent(buttonName: String, extraParams: Map<String, String> = emptyMap()) {
        saveEvent("ButtonClick", Bundle().apply {
            putString("button_name", buttonName)
            extraParams.forEach { (key, value) -> putString(key, value) }
        })
    }

    fun sendWalksNearButtonClickedEvent(ctaName: String, walkName: String) {
        sendButtonClickedEvent(
            ctaName,
            mapOf("walk_name" to walkName)
        )
    }

    fun saveAboutYouEvent(userGender: String, userAge: Int?, userActivityLevel: String?) {
        val bundle = Bundle()
        bundle.putString(KEY_GENDER, userGender)
        userAge?.let { bundle.putInt(KEY_AGE, it) }
        bundle.putString(KEY_ACTIVITY_LEVEL, userActivityLevel ?: "")

        saveEvent(EVENT_ABOUT_YOU, bundle)
    }

    fun saveArticleRead(slug: String?, title: String, source: String, searchTerm: String? = null) {
        val bundle = Bundle().apply {
            putString(ARTICLE_TITLE, title)
            slug?.let { putString(ARTICLE_SLUG, it) }
            putString(SOURCE, source)
            searchTerm?.ifEmpty { null }?.let { putString(PARAM_SEARCH_TERM, it) }
        }
        saveEvent(EVENT_ARTICLE_READ, bundle)
    }

    fun popupCanceled(slug: String) {
        val bundle = Bundle().apply {
            putString(SLUG, slug)
        }

        saveEvent(EVENT_POPUP_CANCELED, bundle)
    }

    fun paceCheckerPopupDismissed() {
        val bundle = Bundle().apply {
            putString("reply", "try_later")
        }
        saveEvent("pace_checker_popup", bundle)
    }

    fun paceCheckerOpened() {
        val bundle = Bundle().apply {
            putLong("new_user", preferenceRepository.isPaceCheckerNewUser.asLong())
        }
        saveEvent("pace_checker_opened", bundle)
    }

    fun paceCheckerCompleted(result: String) {
        val bundle = Bundle().apply {
            putString("result", result)
            putLong("new_user", preferenceRepository.isPaceCheckerNewUser.asLong())
            putLong(
                "targets_hit_last_2_weeks",
                preferenceRepository.daysHitTargetInLastTwoWeeks.toLong()
            )
        }
        saveEvent("pace_checker_completed", bundle)
    }

    fun paceCheckerCanceled(seconds: Int) {
        val bundle = Bundle().apply {
            putLong("new_user", preferenceRepository.isPaceCheckerNewUser.asLong())
            putLong("seconds_walked", seconds.toLong())
        }
        saveEvent("pace_checker_cancelled", bundle)
    }

    fun paceCheckerWasUseful(reply: String) {
        val bundle = Bundle().apply {
            putLong("new_user", preferenceRepository.isPaceCheckerNewUser.asLong())
            putString("reply", reply)
        }
        saveEvent("pace_checker_was_useful", bundle)
    }

    fun paceCheckerMoreToExplore(reply: String) {
        val bundle = Bundle().apply {
            putLong("new_user", preferenceRepository.isPaceCheckerNewUser.asLong())
            putString("reply", reply)
        }
        saveEvent("pace_checker_more_to_explore", bundle)
    }

    fun logCoach5kSmashingItYesEvent() {
        val bundle = Bundle().apply {
            putString("response", "yes")
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
        }
        saveEvent("smashing_it_popup", bundle)
    }

    fun logCoach5kSmashingItNoEvent() {
        val bundle = Bundle().apply {
            putString("response", "no")
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
        }
        saveEvent("smashing_it_popup", bundle)
    }

    fun logCoach5kDownloadYesEvent() {
        val bundle = Bundle().apply {
            putString("response", "yes")
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
        }
        saveEvent("download_c25k_popup", bundle)
    }

    fun logCoach5kDownloadNoEvent() {
        val bundle = Bundle().apply {
            putString("response", "no")
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
        }
        saveEvent("download_c25k_popup", bundle)
    }

    fun logCoach5kTargetChaserAgainHas5K(daysSince: Long) {
        val bundle = Bundle().apply {
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
            putLong("days_since", daysSince)
        }
        saveEvent("target_chaser_again_has_c25k", bundle)
    }

    fun logCoach5kTargetChaserAgain(daysSince: Long) {
        val bundle = Bundle().apply {
            putString("created_at", DateHelper.analyticsDateTimeFormat(System.currentTimeMillis()))
            putLong("days_since", daysSince)
        }
        saveEvent("target_chaser_again", bundle)
    }

    fun mentalHealthBannerClicked() {
        saveEvent("emm_popup_2023")
    }

    fun mentalHealthIDidItClicked() {
        isIDidTtClicked = true
    }

    fun mentalHealthMoodClicked(moodSlug: String) {
        feeling = moodSlug
    }

    fun mentalHealthFlowStart(infoPage: InfoPage) {
        mhInfoPage = infoPage
    }

    fun mentalHealthFeelingArticle(article: InfoPage) {
        mhFeelingArticles.add(article)
    }

    fun mentalHealthRelatedArticle(article: InfoPage) {
        mhArticles.add(article)
    }

    fun notificationSent(notificationName: String) {
        saveEvent(EVENT_NOTIFICATION_SENT, Bundle().apply {
            putString(PARAM_NOTIFICATION_NAME, notificationName)
        })
    }

    fun notificationTapped(notificationName: String) {
        saveEvent(EVENT_NOTIFICATION_TAPPED, Bundle().apply {
            putString(PARAM_NOTIFICATION_NAME, notificationName)
        })
    }

    fun discoverSearch(searchTerm: String, searchResults: Int) {
        if (searchTerm.isEmpty()) return
        saveEvent(EVENT_DISCOVER_SEARCH, Bundle().apply {
            putString(PARAM_SEARCH_TERM, searchTerm)
            putInt(PARAM_RESULT_NUMBER, searchResults)
        })
    }

    fun nhsAccountDisconnectAnswer(answerOne: String, answerTwo: String?) {
        saveEvent("nhs_account_disconnect", Bundle().apply {
            putString("answer_one", answerOne)
            putString("answer_two", answerTwo)
        })
    }

    fun nhsAccountLogoutAnswer(answerOne: String, answerTwo: String?) {
        saveEvent("nhs_account_logout", Bundle().apply {
            putString("answer_one", answerOne)
            putString("answer_two", answerTwo)
        })
    }

    fun walksNearMePopup(isCancelled: Boolean) {
        saveEvent("walks_near_me_popup", Bundle().apply {
            putString("response", if (isCancelled) "Cancel" else "View your walks")
        })
    }

    fun goToJauntlyPopup(isCancelled: Boolean) {
        saveEvent("go_to_jauntly_popup", Bundle().apply {
            putString("response", if (isCancelled) "cancel" else "continue")
        })
    }

    fun sendDailyWalkingPlanEvent(
        userPlans: WalkingPlanEntity,
        cmsPlan: WalkingPlan,
        hourlyStepsData: List<HourlyStepData>,
    ) {
        val doneDays = userPlans.currentWalkingPlan!!.days.size
        val dayIndex = (doneDays - 1) % 7
        val weekIndex = (doneDays - 1) / 7
        val dailyTargetBrisk = cmsPlan.planItineraryItems[weekIndex]!!.dailyBriskMinutes[dayIndex]!!
        val dailyTargetNonBrisk =
            cmsPlan.planItineraryItems[weekIndex]!!.dailyNonBriskMinutes[dayIndex]!!

        val briskMinutesArray = hourlyStepsData.buildWalkingMinutesArrayList(true)
        val nonBriskMinutesArray = hourlyStepsData.buildWalkingMinutesArrayList(false)

        val briskMinutesStringList = briskMinutesArray.joinToString(",")
        val nonBriskMinutesStringList = nonBriskMinutesArray.joinToString(",")

        val dailyCurrentBrisk = briskMinutesArray.sum()
        val dailyCurrentNonBrisk = nonBriskMinutesArray.sum()

        saveEvent("walking_plan", Bundle().apply {
            with(userPlans.currentWalkingPlan!!) {
                putString("plan_id", planId.toString())
                putString("plan_name", cmsPlan.planName)
                putInt("plan_number", userPlans.walkingPlanHistory.count {
                    it.status == WalkingPlanState.COMPLETED.name.lowercase()
                })
                putString(
                    "plan_state", if (doneDays == (cmsPlan.planItineraryItems.size * 7))
                        WalkingPlanState.COMPLETED.name.lowercase()
                    else
                        WalkingPlanState.ACTIVE.name.lowercase()
                )
                putString(
                    "brisk_minutes",
                    briskMinutesStringList)
                putString(
                    "non_brisk_minutes",
                    nonBriskMinutesStringList)
                putInt("sum_brisk_minutes", dailyCurrentBrisk)
                putInt("sum_non_brisk_minutes", dailyCurrentNonBrisk)
                putInt("active_tens", dailyCurrentBrisk / 10)
                putInt("target_brisk_minutes", dailyTargetBrisk)
                putInt("target_non_brisk_minutes", dailyTargetNonBrisk)
                putInt("hit_target_brisk", if (dailyCurrentBrisk >= dailyTargetBrisk) 1 else 0)
                putInt(
                    "hit_target_non_brisk",
                    if (dailyCurrentNonBrisk >= dailyTargetNonBrisk) 1 else 0
                )
                putInt("week", weekIndex + 1)
                putInt("day", dayIndex + 1)
            }
        })
    }

    fun mentalHealthFlowEnd() {
        val article = mhInfoPage ?: return

        val articles = mhArticles.map { it.analyticsTag.ifEmpty { it.slug } }
            .joinToString { it }
            .take(100)

        val feelings = mhFeelingArticles.map { it.analyticsTag.ifEmpty { it.slug } }
            .joinToString { it }
            .take(100)

        val bundle = Bundle().apply {
            putString("article_name", article.title)
            putString("article_tag", article.analyticsTag.ifBlank { article.slug })
            putString("i_did_it_clicked", isIDidTtClicked.toString())
            feeling?.let { putString("feeling", it) }
            putString("link_article_read", articles)
            putString("feeling_article_read", feelings)
        }

        saveEvent("emm_flow_2023", bundle)

        mhInfoPage = null
        isIDidTtClicked = false
        feeling = null
        mhFeelingArticles.clear()
        mhArticles.clear()
    }
}
