package com.flipsidegroup.active10.presentation.walkingplanstatistics

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.data.BriskPauseResume
import com.flipside.briskcounter.internal.ActivityListener
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.databinding.ActivityWalkingPlanStatisticsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.couch.CouchAdvertIntent
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.mywalkingplans.activity.MyWalkingPlansIntent
import com.flipsidegroup.active10.presentation.usecases.FuncItemActionUseCase
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsIntent
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearIntent
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val GOOGLE_FIT_DELAY_MILLIS = 30000L
private const val GOOGLE_FIT_INITIAL_DELAY = 0L
const val PARAM_WALKING_PLAN_ID = "param_walking_plan_id"


fun Context.WalkingPlanStatisticsIntent(planId: Long): Intent {
    return Intent(this, WalkingPlanStatisticsActivity::class.java).apply {
        putExtra(PARAM_WALKING_PLAN_ID, planId)
    }
}

class WalkingPlanStatisticsActivity : BaseSecureActivity<WalkingPlanStatisticsView>(),
    WalkingPlanStatisticsView, ActivityListener {

    override fun getPresenter(): LifecycleAwarePresenter<WalkingPlanStatisticsView> = presenter

    @Inject
    lateinit var presenter: WalkingPlanStatisticsPresenter

    @Inject
    lateinit var funcItemActionUseCase: FuncItemActionUseCase

    private var binding: ActivityWalkingPlanStatisticsBinding by lifecycleAwareVariable()
    private var walkingPlanActivityObserver: CompositeDisposable = CompositeDisposable()

    private val mainAdapter by lazy { WalkingPlanStatisticsAdapter() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentLoadingProgressMinDelay(0)
        setContentView(
            ActivityWalkingPlanStatisticsBinding.inflate(layoutInflater)
                .apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        val planId = intent.getLongExtra(PARAM_WALKING_PLAN_ID, -1L)

        setUpViews(planId)
    }

    override fun onResume() {
        super.onResume()
        observeBriskCounter()
    }

    private fun setUpViews(planId: Long) {
        with(binding.recyclerView) {
            adapter = mainAdapter
        }

        presenter.loadContent(planId)
    }

    private fun observeBriskCounter() {
        if (walkingPlanActivityObserver.size() == 0) {
            walkingPlanActivityObserver.add(
                Observable.interval(
                    GOOGLE_FIT_INITIAL_DELAY, GOOGLE_FIT_DELAY_MILLIS, TimeUnit.MILLISECONDS
                ).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    presenter.loadBriskData()
                }, {
                    Timber.d("Error occurred while retrieving google fit today walk: ${it.message}")
                })
            )
        }
    }

    override fun doBriskCounter(pauseResumeList: List<BriskPauseResume>) {
        BriskCounter.retrieveTodayActivity(
            this, this, pauseResume = pauseResumeList
        )
    }

    override fun showContent(content: List<WalkingPlanStatisticsPart>) {
        mainAdapter.submitList(content)
    }

    override fun onSuccess(briskActivity: BriskActivity) {
        presenter.onTodayActivity(briskActivity)
    }

    override fun onFailure(activityError: String) {
        Timber.d("Walking Plan doesn't work")
    }

    override fun onPlanResumed() {
        observeBriskCounter()
    }

    override fun showDialog(dialog: WalkingPlanCommonDialog) {
        lifecycleScope.launch {
            lifecycle.withResumed {
                dialog.show(supportFragmentManager, WalkingPlanCommonDialog::class.java.simpleName)
            }
        }
    }

    override fun showDialog(dialog: WalkingPlanBottomSheetDialog, showConfetti: Boolean) {
        lifecycleScope.launch {
            lifecycle.withResumed {
                handler.removeCallbacksAndMessages(null)
                binding.confetti.isVisible = true
                handler.postDelayed({
                    binding.confetti.isVisible = false
                    dialog.show(
                        supportFragmentManager,
                        WalkingPlanBottomSheetDialog::class.java.simpleName
                    )
                }, if (showConfetti) 3000 else 0)
            }
        }
    }

    override fun showLoading() {
        binding.loadingView.isVisible = true
    }

    override fun hideLoading() {
        binding.loadingView.isVisible = false
    }

    override fun goToArticle(infoPage: InfoPage) {
        startActivity(DiscoverDetailsIntent(infoPage.id, infoPage.title, "my_walks"))
    }

    override fun goToWalksNear() {
        startActivity(WalksNearIntent())
    }

    override fun goToCouchAdvert() {
        finish()
        startActivity(CouchAdvertIntent())
    }

    override fun goToAnotherPlan(planId: Long) {
        val homeIntent = HomeActivity.getHomeIntent(this)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val stackOfActivities = arrayOf(
            homeIntent,
            MyWalkingPlansIntent(),
            WalkingPlanDetailsIntent(planId)
        )

        PendingIntent.getActivities(
            this,
            0,
            stackOfActivities,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ).send()
    }

    override fun funcItemAction(actionSlug: String?) {
        funcItemActionUseCase(this, actionSlug)
    }

    override fun goBack() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        walkingPlanActivityObserver.clear()
    }

    override fun onStop() {
        super.onStop()
        walkingPlanActivityObserver.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}