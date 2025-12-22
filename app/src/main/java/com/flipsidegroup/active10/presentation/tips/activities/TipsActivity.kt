package com.flipsidegroup.active10.presentation.tips.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.databinding.ActivityTipsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.activities.IN_NOTIFICATION_ANALYTICS
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.tips.adapters.TipsVPAdapter
import com.flipsidegroup.active10.presentation.tips.presenter.TipsPresenter
import com.flipsidegroup.active10.presentation.tips.view.TipsView
import com.flipsidegroup.active10.utils.OnPageScrolledListener
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val IN_IS_FROM_SETTINGS = "IN_IS_FROM_SETTINGS"

fun Context.TipsIntent(isFromSettings: Boolean = false, event: String? = null): Intent {
    return Intent(this, TipsActivity::class.java).apply {
        putExtra(IN_IS_FROM_SETTINGS, isFromSettings)
        putExtra(IN_NOTIFICATION_ANALYTICS, event)
    }
}

class TipsActivity : BaseSecureActivity<TipsView>(), TipsView {

    @Inject
    internal lateinit var presenter: TipsPresenter

    private var tipsVPAdapter: TipsVPAdapter? = null

    private var binding: ActivityTipsBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<TipsView>? = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityTipsBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        binding.tipsToolbar.backTV.setOnClickListener { onBackPressed() }
        setUpViews()

        presenter.getTips()
    }

    override fun onTipsListReceived(tipsList: List<Tip>) {
        tipsVPAdapter?.updateTips(tipsList)
    }

    private fun setUpViews() {
        tipsVPAdapter =
            TipsVPAdapter(supportFragmentManager, emptyList())
        binding.tipsVP.swipeEnabled = true
        binding.tipsVP.adapter = tipsVPAdapter
        binding.tipsVP.addOnPageChangeListener(OnPageScrolledListener { position, positionOffset ->
            if (positionOffset > 0 && position < getAdapterCount()
                || positionOffset < 0 && position > 0
            ) {
                showButtons()
            } else {
                return@OnPageScrolledListener
            }
        })

        binding.nextBTN.setOnClickListener {
            tipsVPAdapter?.let {
                if (binding.tipsVP.currentItem == it.count - 1) {
                    finish()
                    return@setOnClickListener
                }
            }
            goToNextScreen()
        }
        binding.previousBTN.setOnClickListener {
            goToPreviousScreen()
        }
    }

    private fun getAdapterCount(): Int =
        tipsVPAdapter?.count ?: 0

    private fun goToPreviousScreen() {
        val currentItem = binding.tipsVP.currentItem
        binding.tipsVP.setCurrentItem(currentItem - 1, true)
        showButtons()
    }

    private fun goToNextScreen() {
        val currentItem = binding.tipsVP.currentItem
        binding.tipsVP.setCurrentItem(currentItem + 1, true)
        showButtons()
    }

    private fun showButtons() {
        when (binding.tipsVP.currentItem) {
            0 -> {
                binding.previousBTN.visibility = View.GONE
                binding.nextBTN.visibility = View.VISIBLE
                binding.nextBTN.text = UIUtils.getString(R.string.next)
            }

            tipsVPAdapter!!.count - 1 -> {
                binding.previousBTN.visibility = View.VISIBLE
                binding.nextBTN.visibility = View.VISIBLE
                binding.nextBTN.text = UIUtils.getString(R.string.button_done)
            }

            else -> {
                binding.nextBTN.visibility = View.VISIBLE
                binding.previousBTN.visibility = View.VISIBLE
                binding.nextBTN.text = UIUtils.getString(R.string.next)
            }
        }
    }
}
