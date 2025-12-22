package com.flipsidegroup.active10.presentation.legals.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.databinding.ActivityAccessibilityStatementBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.discover_details.ArticleAdapter
import com.flipsidegroup.active10.presentation.discover_details.ArticlePartAT
import com.flipsidegroup.active10.presentation.discover_details.youtube.YoutubePlayerIntent
import com.flipsidegroup.active10.presentation.legals.presenter.LegalsPresenter
import com.flipsidegroup.active10.presentation.legals.view.LegalsView
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.openURL
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

const val RULE_TYPE = "rule_type"

fun Context.LegalsActivity(ruleType: String): Intent {
    return Intent(this, LegalsActivity::class.java).apply {
        putExtra(RULE_TYPE, ruleType)
    }
}

class LegalsActivity : BaseSecureActivity<LegalsView>(), LegalsView {

    @Inject
    lateinit var presenter: LegalsPresenter

    private var binding: ActivityAccessibilityStatementBinding by lifecycleAwareVariable()

    private val adapter by lazy { ArticleAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityAccessibilityStatementBinding.inflate(layoutInflater)
                .apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpToolbar()
        setUpRecyclerView()
        presenter.getLegalRules(intent.extras?.getString(RULE_TYPE) ?: "")
    }


    private fun setUpRecyclerView() {
        binding.accessibilityStatementRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LegalsActivity.adapter
        }
    }

    private fun setUpToolbar() {
        binding.accessibilityStatementToolbar.backTV.setOnClickListener { onBackPressed() }
    }

    override fun showData(data: List<ArticlePartAT>, title: String) {
        binding.progressBarStatement.isVisible = false
        adapter.submitList(data)
        binding.accessibilityStatementTitle.text = title
    }

    override fun navigateToUrl(url: String?) {
        url?.also { openURL(it) }
    }

    override fun navigateToYoutubeVideoPlayer(youtubeVideoId: String) {
        startActivity(YoutubePlayerIntent(youtubeVideoId))
    }

    override fun getPresenter(): LifecycleAwarePresenter<LegalsView> = presenter
}
