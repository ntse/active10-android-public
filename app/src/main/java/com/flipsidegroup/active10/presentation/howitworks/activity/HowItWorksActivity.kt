package com.flipsidegroup.active10.presentation.howitworks.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.databinding.ActivityHowItWorksBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.howitworks.adapters.HowItWorksAdapter
import com.flipsidegroup.active10.presentation.howitworks.presenter.HowItWorksPresenter
import com.flipsidegroup.active10.presentation.howitworks.view.HowItWorksView
import com.flipsidegroup.active10.utils.OnPageScrolledListener
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val IN_IS_FROM_SETTINGS = "IN_IS_FROM_SETTINGS"

fun Context.HowItWorksIntent(isFromSettings: Boolean = false): Intent {
    return Intent(this, HowItWorksActivity::class.java).apply {
        putExtra(IN_IS_FROM_SETTINGS, isFromSettings)
    }
}

class HowItWorksActivity : BaseSecureActivity<HowItWorksView>(), HowItWorksView {

    @Inject
    internal lateinit var presenter: HowItWorksPresenter

    private var binding: ActivityHowItWorksBinding by lifecycleAwareVariable()

    private var adapter: HowItWorksAdapter? = null

    override fun getPresenter(): LifecycleAwarePresenter<HowItWorksView>? = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityHowItWorksBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        binding.howItWorksToolbar.backTV.setOnClickListener { onBackPressed() }
        setUpViews()

        presenter.getHowItWorks()
    }

    override fun onHowItWorksListReceived(list: List<HowItWorks>) {
        adapter?.updateHowItWorks(list)
    }

    private fun setUpViews() {
        adapter = HowItWorksAdapter(
            fragmentManager = supportFragmentManager,
            howItWorksList = emptyList()
        )
        binding.introVP.swipeEnabled = true
        binding.introVP.adapter = adapter
        binding.introVP.addOnPageChangeListener(OnPageScrolledListener { position, positionOffset ->
            if (positionOffset > 0 && position < getAdapterCount()
                || positionOffset < 0 && position > 0
            ) {
                showButtons()
            } else {
                return@OnPageScrolledListener
            }
        })

        binding.nextBTN.setOnClickListener {
            adapter?.let {
                if (binding.introVP.currentItem == it.count - 1) {
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
        adapter?.count ?: 0

    private fun goToPreviousScreen() {
        val currentItem = binding.introVP.currentItem
        binding.introVP.setCurrentItem(currentItem - 1, true)
        showButtons()
    }

    private fun goToNextScreen() {
        val currentItem = binding.introVP.currentItem
        binding.introVP.setCurrentItem(currentItem + 1, true)
        showButtons()
    }

    private fun showButtons() {
        when (binding.introVP.currentItem) {
            0 -> {
                binding.previousBTN.visibility = View.GONE
                binding.nextBTN.visibility = View.VISIBLE
                binding.nextBTN.text = UIUtils.getString(R.string.next)
            }

            adapter!!.count - 1 -> {
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
