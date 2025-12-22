package com.flipsidegroup.active10.presentation.discover_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.databinding.ActivityDiscoverDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.discover_details.youtube.YoutubePlayerIntent
import com.flipsidegroup.active10.presentation.mentalhealth.mood.MentalHealthMoodActivity
import com.flipsidegroup.active10.presentation.signIn.SignInActivity
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.openURL
import com.flipsidegroup.active10.utils.playAnimationIfEnabled
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

private const val PARAM_ARTICLE_ID = "ARTICLE_ID"
private const val PARAM_ARTICLE_TITLE = "ARTICLE_TITLE"
private const val PARAM_SOURCE = "SOURCE"
private const val PARAM_SEARCH_TEXT = "SEARCH_TEXT"
private const val PARAM_ARTICLE_SLUG = "ARTICLE_SLUG"
private const val DEFAULT_ARTICLE_ID = -1L

fun Context.DiscoverDetailsIntent(
    articleId: Long? = null,
    source: String,
    searchText: String? = null,
    articleSlug: String? = null
): Intent {
    return Intent(this, DiscoverDetailsActivity::class.java).apply {
        putExtra(PARAM_ARTICLE_ID, articleId)
        putExtra(PARAM_SOURCE, source)
        putExtra(PARAM_SEARCH_TEXT, searchText)
        putExtra(PARAM_ARTICLE_SLUG, articleSlug)
    }
}

class DiscoverDetailsActivity : BaseSecureActivity<DiscoverDetailsView>(), DiscoverDetailsView {

    override fun getPresenter(): LifecycleAwarePresenter<DiscoverDetailsView> = presenter

    private val adapter by lazy { ArticleAdapter() }

    @Inject
    lateinit var presenter: DiscoverDetailsPresenter

    private var binding: ActivityDiscoverDetailsBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityDiscoverDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }

        binding.discoverDetailsToolbar.backTV.setOnClickListener { onBackPressed() }
        setUpRecyclerView()

        val source = intent.getStringExtra(PARAM_SOURCE)
        val articleId = intent.getLongExtra(PARAM_ARTICLE_ID, DEFAULT_ARTICLE_ID)
        val searchText = intent.getStringExtra(PARAM_SEARCH_TEXT)
        val articleSlug = intent.getStringExtra(PARAM_ARTICLE_SLUG)

        presenter.loadPageParts(
            articleId = articleId,
            source = source ?: "",
            searchText = searchText,
            articleSlug = articleSlug
        )
    }

    override fun changeColorsToMentalHealth() {
        binding.discoverDetailsToolbar.root.background =
            ContextCompat.getDrawable(this, R.drawable.mental_health_background)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorMentalHealth)
    }

    private fun setUpRecyclerView() {
        binding.discoverDetailsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DiscoverDetailsActivity.adapter
        }
    }

    override fun showData(data: List<ArticlePartAT>, title: String) {
        adapter.submitList(data)
    }

    override fun navigateToUrl(url: String?) {
        url?.also { openURL(it) }
    }

    override fun navigateToYoutubeVideoPlayer(youtubeVideoId: String) {
        startActivity(YoutubePlayerIntent(youtubeVideoId))
    }

    override fun openNewArticle(infoPage: InfoPage) {
        startActivity(DiscoverDetailsIntent(infoPage.id, infoPage.title, "discover"))
    }

    override fun showConfettiAnimation(show: Boolean) {
        binding.confettiAnimation.isVisible = show
        binding.confettiAnimation.playAnimationIfEnabled(preferenceRepository.isAnimationEnabled)
        binding.confettiAnimationIcon.isVisible = show
        binding.confettiAnimationIconContainer.isVisible = show

        if (preferenceRepository.isAnimationEnabled) {
            binding.confettiAnimationIcon.slideUp()
        }
    }

    private fun View.slideUp() {
        val animate = TranslateAnimation(
            0f,
            0f,
            height.toFloat() * 2,
            0f
        )
        animate.duration = 1000
        animate.fillAfter = true
        startAnimation(animate)
    }

    override fun navigateToMentalHealthMood() {
        startActivity(Intent(this, MentalHealthMoodActivity::class.java))
    }

    override fun navigateToNhsSignIn() {
        startActivity(signInIntent(FlowType.SETTINGS))
    }
}
