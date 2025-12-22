package com.flipsidegroup.active10.data.persistance.newapi

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.AppRepository
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenRepository @Inject constructor(
    private val appRepository: AppRepository,
    private val localRepository: LocalRepository
) {

    fun getScreenContentBySlug(slug: String) =
        getScreenContents()
            .map { screens ->
                screens.firstOrNull { it.slug == slug } ?: ScreenContent()
            }

    fun getScreensByIds(ids: List<Long?>) =
        getScreenContents()
            .map { screens ->
                screens.filter { it.id in ids }
                    .sortedBy { ids.indexOf(it.id) }
            }

    private fun getScreenContents() = localRepository.getScreenContents()
        .flatMap { screenContents ->
            if (screenContents.isNotEmpty()) {
                Single.just(screenContents)
                    .subscribeOn(Schedulers.io())
            } else {
                appRepository.getScreenContents()
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {
                        localRepository.persistScreenContents(it)
                    }
            }
        }

    companion object {
        const val SLUG_DISCOVER = "discover_screen_v3"
        const val SLUG_HERO_POPUP = "hero_popup"
        const val SLUG_DEEP_LINKING_MODAL = "deep_linking_popup"

        const val WELCOME_TO_PACE_CHECKER = "welcome_to_pace_checker"
        const val PACE_CHECKER_FLAT_TERRAIN = "pace_checker_flat_terrain"
        const val PACE_CHECKER_ARE_YOU_SURE = "pace_checker_are_you_sure"
        const val PACE_CHECKER_WAS_THIS_USEFUL = "pace_checker_was_this_useful"
        const val PACE_CHECKER_MORE_TO_EXPLORE = "pace_checker_more_to_explore"
        const val PACE_CHECKER_PAUSED = "pace_checker_paused"
        const val PACE_CHECKER_WHAT_IS_BRISK = "pace_checker_what_is_brisk"

        const val MY_WALKS_BANNER = "my_walks_banner"

        const val BH_MENTAL_BANNER = "bh_mental_banner"
        const val BH_MENTAL_MOOD = "bh_mental_mood"

        const val MENTAL_MOOD_BOTTOM_DIALOG_GREAT = "bh_mental_mood_great"
        const val MENTAL_MOOD_BOTTOM_DIALOG_GOOD = "bh_mental_mood_good"
        const val MENTAL_MOOD_BOTTOM_DIALOG_OK = "bh_mental_mood_ok"
        const val MENTAL_MOOD_BOTTOM_DIALOG_BAD = "bh_mental_mood_bad"

        const val CAMPAIGN_BANNER = "campaign_banner"

        const val LOGIN_DISCONNECT_SURE = "nhs_login_disconnect_sure"
        const val LOGIN_CREATE_ACCOUNT = "nhs_login_create_account"
        const val LOGIN_CREATING_ACCOUNT = "nhs_login_creating_account"
        const val LOGIN_CURRENT_LOCATION = "nhs_login_current_location"
        const val LOGIN_DISCONNECT = "nhs_login_disconnect"
        const val LOGIN_LOGOUT = "nhs_login_logout"
        const val LOGIN_LOGOUT_SURE = "nhs_login_logout_sure"
        const val LOGIN_MY_DETAILS = "nhs_login_my_details"
        const val LOGIN_HOW_WE_USE = "nhs_login_how_we_use"
        const val LOGIN_STAY_UPDATED = "nhs_login_stay_updated"
        const val LOGIN_TO_USE_TERMS = "nhs_login_to_use_terms"
        const val LOGIN_CONTINUE_TO_NHS = "nhs_login_continue_to_nhs"
        const val LOGIN_UPDATE_DETAILS = "nhs_login_update_details"
        const val LOGIN_NEW_FEATURE_WALKING_PLANS = "new_feature_walking_plans"
        const val LOGIN_NEW_FEATURE_WALKS_NEAR = "new_feature_walks_near_you_pending"
        const val LOGIN_NEW_FEATURE_MONTHLY_REPORT = "new_feature_monthly_report"
        const val LOGIN_CIRCULAR_WALK_DETAILS = "circular_walk_details_view"

        const val WALKING_PLAN_VIEW = "walking_plan_view"
        const val DOWNLOAD_COUCH25K_VIEW = "download_c25k"
    }
}
