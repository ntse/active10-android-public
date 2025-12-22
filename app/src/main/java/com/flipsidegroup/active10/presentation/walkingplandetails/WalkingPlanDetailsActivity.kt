package com.flipsidegroup.active10.presentation.walkingplandetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.databinding.ActivityWalkingPlanDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanDebugDialog
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.usecases.FuncItemActionUseCase
import com.flipsidegroup.active10.presentation.walkingplanstatistics.WalkingPlanStatisticsIntent
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearIntent
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PARAM_WALKING_PLAN_ID = "param_walking_plan_id"

fun Context.WalkingPlanDetailsIntent(planId: Long): Intent {
    return Intent(this, WalkingPlanDetailsActivity::class.java).apply {
        putExtra(PARAM_WALKING_PLAN_ID, planId)
    }
}

class WalkingPlanDetailsActivity : BaseSecureActivity<WalkingPlanDetailsView>(), WalkingPlanDetailsView {

    override fun getPresenter(): LifecycleAwarePresenter<WalkingPlanDetailsView> = presenter

    @Inject
    lateinit var presenter: WalkingPlanDetailsPresenter

    @Inject
    lateinit var funcItemActionUseCase: FuncItemActionUseCase

    private var binding: ActivityWalkingPlanDetailsBinding by lifecycleAwareVariable()

    private val mainAdapter by lazy { WalkingPlanDetailsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentLoadingProgressMinDelay(0)
        setContentView(
            ActivityWalkingPlanDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        val planId = intent.getLongExtra(PARAM_WALKING_PLAN_ID, -1L)

        setUpViews(planId)
    }

    private fun setUpViews(planId: Long) {
        with(binding.recyclerView) {
            adapter = mainAdapter
        }

        presenter.loadContent(planId)
    }

    override fun showContent(content: List<WalkingPlanDetailsPart>) {
        mainAdapter.submitList(content)
    }

    override fun showDialog(dialog: WalkingPlanCommonDialog) {
        lifecycleScope.launch {
            lifecycle.withResumed {
                dialog.show(supportFragmentManager, WalkingPlanCommonDialog::class.java.simpleName)
            }
        }
    }

    override fun showDialog(dialog: WalkingPlanDebugDialog) {
        lifecycleScope.launch {
            lifecycle.withResumed {
                dialog.show(supportFragmentManager, WalkingPlanDebugDialog::class.java.simpleName)
            }
        }
    }

    override fun goToArticle(infoPage: InfoPage) {
        startActivity(DiscoverDetailsIntent(infoPage.id, infoPage.title, "my_walks"))
    }

    override fun goToWalksNear() {
        startActivity(WalksNearIntent())
    }

    override fun goToWalkingPlanStatistics(planId: Long) {
        startActivity(WalkingPlanStatisticsIntent(planId))
    }

    override fun funcItemAction(actionSlug: String?) {
        funcItemActionUseCase(this, actionSlug)
    }

    override fun pop() {
        finish()
    }
}