package com.flipsidegroup.active10.presentation.walksnear.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.databinding.ActivityWalksNearBinding
import com.flipsidegroup.active10.presentation.circularwalk.CircularWalkIntent
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walksnear.adapters.WalksNearAdapter
import com.flipsidegroup.active10.presentation.walksnear.adapters.WalksNearPart
import com.flipsidegroup.active10.presentation.walksnear.presenter.WalksNearPresenter
import com.flipsidegroup.active10.presentation.walksnear.view.WalksNearView
import com.flipsidegroup.active10.presentation.walksneardetails.activity.WalkNearDetailsIntent
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.WalksNearIntent(): Intent {
    return Intent(this, WalksNearActivity::class.java)
}

class WalksNearActivity : BaseSecureActivity<WalksNearView>(), WalksNearView {

    override fun getPresenter(): LifecycleAwarePresenter<WalksNearView> = presenter

    private var binding: ActivityWalksNearBinding by lifecycleAwareVariable()

    private val mainAdapter by lazy { WalksNearAdapter() }

    @Inject
    lateinit var presenter: WalksNearPresenter

    @Inject
    internal lateinit var localNotificationRepository: LocalNotificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityWalksNearBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.walks_near_title)

        logScreenEvent()

        checkNotificationConditions()
        setUpViews()
    }

    private fun setUpViews() {
        with(binding.recyclerView) {
            adapter = mainAdapter
        }

        presenter.loadContent()
    }

    override fun showData(content: List<WalksNearPart>) {
        mainAdapter.submitList(content)
    }

    override fun navigateToCuratedWalkDetails(walk: CuratedWalk) {
        startActivity(WalkNearDetailsIntent(walk))
    }

    override fun navigateToCircularWalkDetails(walk: CircularWalkResponse) {
        startActivity(CircularWalkIntent(walk))
    }

    override fun showLoading() {
        binding.loadingView.isVisible = true
    }

    override fun hideLoading() {
        binding.loadingView.isVisible = false
    }

    private fun checkNotificationConditions() {
        localNotificationRepository.cancelNotClickedOnWalksNear()
    }

    private fun logScreenEvent() {
        firebaseAnalyticsHelper.sendViewScreenEvent("WalksNearMe")
    }

}