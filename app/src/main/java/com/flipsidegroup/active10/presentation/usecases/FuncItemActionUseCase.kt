package com.flipsidegroup.active10.presentation.usecases

import android.content.Context
import android.content.Intent
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.BETTER_HEALTH_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.mywalkingplans.activity.MyWalkingPlansIntent
import com.flipsidegroup.active10.presentation.stayUpdated.getStayUpdatedIntent
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearIntent
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import javax.inject.Inject

class FuncItemActionUseCase @Inject constructor(
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val discoverRepository: DiscoverRepository,
) {

    private var disposable: Disposable = Disposables.empty()

    operator fun invoke(context: Context, actionSlug: String?) {
        actionSlug ?: return

        with(context) {
            when (actionSlug) {
                ACTION_MY_WALKING_PLANS -> {
                    firebaseAnalyticsHelper.sendButtonClickedEvent("ViewMyWalkingPlans")
                    startActivity(MyWalkingPlansIntent())
                }
                ACTION_WALKS_IN_MY_AREA -> {
                    firebaseAnalyticsHelper.sendButtonClickedEvent("WalksNearMe")
                    startActivity(WalksNearIntent())
                }
                ACTION_MONTHLY_REPORT_OPT_OUT_VIEW,
                ACTION_MONTHLY_REPORT_VIEW -> {
                    firebaseAnalyticsHelper.sendButtonClickedEvent("MonthlyReportSettings")
                    startActivity(getStayUpdatedIntent(FlowType.STAY_UPDATED_ONLY))
                }
                ACTION_STAYING_ACTIVE -> {
                    firebaseAnalyticsHelper.sendButtonClickedEvent("AdviceAndSupport")
                    goToAdviceAndSupport(this)
                }
            }
        }
    }

    private fun goToAdviceAndSupport(context: Context) {
//        disposable = discoverRepository
//            .getArticleBySlug("staying_active_healthy")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { article ->
//                    if (article.isAvailable) article.get()?.let {
//                        context.startActivity(
//                            context.DiscoverDetailsIntent(it.id, it.title, "staying_active_and_healthy")
//                        )
//                    }
//                    disposable.dispose()
//                },
//                { error ->
//                    Timber.e(error, "get widget introduce article error")
//                    disposable.dispose()
//                }
//            )
        context.startActivity(
            HomeActivity.getHomeIntent(
                context = context,
                screenPosition = BETTER_HEALTH_SCREEN_POSITION
            ).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )
    }

    companion object {
        const val ACTION_MY_WALKING_PLANS = "my_walking_plans_view"
        const val ACTION_WALKS_IN_MY_AREA = "walks_in_my_area_view"
        const val ACTION_MONTHLY_REPORT_VIEW = "monthly_report_view"
        const val ACTION_MONTHLY_REPORT_OPT_OUT_VIEW = "monthly_report_opt_out_view"
        const val ACTION_STAYING_ACTIVE = "staying_active_and_healthy_view"
    }
}