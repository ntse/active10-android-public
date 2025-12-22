package com.flipsidegroup.active10.presentation.faq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityFaqBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val IN_IS_FROM_SETTINGS = "IN_IS_FROM_SETTINGS"

fun Context.FaqIntent(isFromSettings: Boolean = false): Intent {
    return Intent(this, FaqActivity::class.java).apply {
        putExtra(IN_IS_FROM_SETTINGS, isFromSettings)
    }
}

class FaqActivity : BaseSecureActivity<FaqView>(), FaqView {

    @Inject
    internal lateinit var presenter: FaqPresenter

    private var faqAdapter: FaqAdapter? = null

    override fun getPresenter(): LifecycleAwarePresenter<FaqView> = presenter

    private var binding: ActivityFaqBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityFaqBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpToolbar()
        setUpFaqAdapter()

        presenter.getFaq()
    }

    override fun onGetFaqCompleted(faqList: List<FaqListItem>) {
        faqAdapter?.submitList(faqList)
    }

    private fun setUpToolbar() {
        binding.faqToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.faqToolbar.titleTV.text = getString(R.string.faq_title)
    }

    private fun setUpFaqAdapter() {
        val layoutManager = LinearLayoutManager(this)
        binding.faqRV.layoutManager = layoutManager

        faqAdapter = FaqAdapter()
        binding.faqRV.adapter = faqAdapter

        faqAdapter?.onItemClickListener = { presenter.toggleListItem(it) }
    }
}
