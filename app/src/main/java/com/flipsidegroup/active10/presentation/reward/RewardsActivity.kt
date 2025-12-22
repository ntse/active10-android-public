package com.flipsidegroup.active10.presentation.reward

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.GridLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.databinding.ActivityRewardsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.reward.adapter.RewardCardAdapter
import com.flipsidegroup.active10.presentation.reward.presenter.RewardsPresenter
import com.flipsidegroup.active10.presentation.reward.view.RewardsView
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val SHARING_INTENT_TYPE = "text/plain"
const val SHARING_URL = "https://www.nhs.uk/better-health/get-active/"

fun Context.getRewardIntent(): Intent {
    return Intent(this, RewardsActivity::class.java)
}

class RewardsActivity : BaseSecureActivity<RewardsView>(), RewardsView {

    @Inject
    lateinit var presenter: RewardsPresenter

    private var binding: ActivityRewardsBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<RewardsView>? = presenter
    private var adapter: RewardCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivityRewardsBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpToolbar()
        setUpView()
        presenter.getRewardBadges()
    }

    private fun setUpToolbar() {
        binding.rewardsToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.rewardsToolbar.titleTV.text = getString(R.string.rewards)
    }

    private fun setUpView() {
        adapter = RewardCardAdapter(presenter::selectBadge)

        val gridLayoutManager = GridLayoutManager(this, 1)

        binding.rewardsRv.layoutManager = gridLayoutManager
        binding.rewardsRv.adapter = adapter
    }

    override fun onRewardBadgesReceived(data: List<Pair<String, List<RewardBadge>>>) {
        adapter?.submitList(data)
    }

    override fun showDialog(rewardBadge: RewardBadge, earnedBadges: List<EarnRewardBadge>) {
        RewardsDialog.newInstance(
            earnedBadges,
            rewardBadge,
            preferenceRepository.isAnimationEnabled
        ).show(
            supportFragmentManager,
            null
        )
    }
}
