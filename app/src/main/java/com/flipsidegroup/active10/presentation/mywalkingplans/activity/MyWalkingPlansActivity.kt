package com.flipsidegroup.active10.presentation.mywalkingplans.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.databinding.ActivityMyWalkingPlansBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.adapters.MyWalkingPlansAdapter
import com.flipsidegroup.active10.presentation.mywalkingplans.adapters.MyWalkingPlansPart
import com.flipsidegroup.active10.presentation.mywalkingplans.presenter.MyWalkingPlansPresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.view.MyWalkingPlansView
import com.flipsidegroup.active10.presentation.usecases.FuncItemActionUseCase
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsIntent
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.MyWalkingPlansIntent(): Intent {
    return Intent(this, MyWalkingPlansActivity::class.java)
}

class MyWalkingPlansActivity : BaseSecureActivity<MyWalkingPlansView>(), MyWalkingPlansView {

    override fun getPresenter(): LifecycleAwarePresenter<MyWalkingPlansView> = presenter

    private var binding: ActivityMyWalkingPlansBinding by lifecycleAwareVariable()

    private val mainAdapter by lazy { MyWalkingPlansAdapter() }

    @Inject
    lateinit var presenter: MyWalkingPlansPresenter

    @Inject
    lateinit var localNotificationRepository: LocalNotificationRepository

    @Inject
    lateinit var funcItemActionUseCase: FuncItemActionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityMyWalkingPlansBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.my_walking_plans_title)

        checkNotificationConditions()
        setUpViews()
    }

    private fun setUpViews() {
        with(binding.recyclerView) {
            adapter = mainAdapter
        }

        presenter.loadContent()
    }

    override fun showData(content: List<MyWalkingPlansPart>) {
        mainAdapter.submitList(content)
    }

    private fun checkNotificationConditions() {
        localNotificationRepository.cancelNotClickedOnMWP()
        with(preferenceRepository) {
            if (!isUserNotSelectedPlanNotified) {
                localNotificationRepository.setNotSelectedMWP()
                isUserNotSelectedPlanNotified = true
            }
        }
    }

    override fun navigateToWalkingPlan(planId: Long) {
        startActivity(WalkingPlanDetailsIntent(planId))
    }

    override fun funcItemAction(actionSlug: String?) {
        funcItemActionUseCase(this, actionSlug)
    }

}