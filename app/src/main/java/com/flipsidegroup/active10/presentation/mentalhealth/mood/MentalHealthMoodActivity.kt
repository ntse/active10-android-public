package com.flipsidegroup.active10.presentation.mentalhealth.mood

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.databinding.ActivityMentalHealthMoodBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.utils.openURL
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialog
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialogType
import com.phe.betterhealth.components.moodbottomdialog.BHRelatedArticle
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class MentalHealthMoodActivity : BaseSecureActivity<BaseView>() {

    @Inject
    lateinit var screenRepository: ScreenRepository

    @Inject
    lateinit var discoverRepository: DiscoverRepository

    private var _binding: ActivityMentalHealthMoodBinding? = null
    private val binding get() = _binding!!

    override fun getPresenter() = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMentalHealthMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorMentalHealth)

        lifecycleScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.BH_MENTAL_MOOD).await()
            }
                .onFailure { showAlert(it) }
                .onSuccess {
                    with(binding.bhMoodSelector) {
                        titleText = it.title
                        subtitleText = it.description
                        answerFirstText = it.getPropertyValue("first_button_title")
                        answerSecondText = it.getPropertyValue("second_button_title")
                        answerThirdText = it.getPropertyValue("third_button_title")
                        answerFourthText = it.getPropertyValue("fourth_button_title")
                        secondButtonText = it.getPropertyValue("fifth_button_title")

                        setOnBackButtonClickListener { onBackPressed() }
                        setOnSecondButtonClickListener {
                            firebaseAnalyticsHelper.mentalHealthMoodClicked("skip")
                            startActivity(HomeActivity.getHomeIntent(this@MentalHealthMoodActivity))
                            finishAffinity()
                        }
                        setOnAnswerClickListener {
                            checkMoodDialog(clickedAnswerType)
                        }
                    }
                }
        }
    }

    private fun checkMoodDialog(moodType: BHMoodBottomDialogType?) {
        moodType ?: return

        lifecycleScope.launch {
            val slug = when (moodType) {
                BHMoodBottomDialogType.GREAT -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_GREAT
                BHMoodBottomDialogType.GOOD -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_GOOD
                BHMoodBottomDialogType.OK -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_OK
                BHMoodBottomDialogType.TIRED -> ScreenRepository.MENTAL_MOOD_BOTTOM_DIALOG_BAD
            }

            firebaseAnalyticsHelper.mentalHealthMoodClicked(slug)

            runCatching {
                val screenContent = screenRepository.getScreenContentBySlug(slug).await()
                val firstArticle = discoverRepository.getArticles().await()
                    .singleOrNull { it.id == screenContent.infoPageIds?.firstOrNull() }

                screenContent to firstArticle
            }
                .onFailure { showAlert(it) }
                .onSuccess { (screenContent, firstArticle) ->
                    val moodDialog = BHMoodBottomDialog().apply {
                        titleText = screenContent?.title
                        subtitleText = screenContent?.description
                        descriptionText = screenContent?.getPropertyValue("first_label_text")
                        buttonText = screenContent?.getPropertyValue("first_button_title")
                        dialogType = moodType
                        isCancelable = false
                        article = firstArticle?.let {
                            BHRelatedArticle(
                                categoryLabel = it.categoryLabel,
                                title = it.title,
                                imageUrl = it.imageUrl,
                                isLink = it.categoryView == "link",
                            )
                        }
                        onArticleClickListener = { firstArticle?.let { onArticleClicked(it) } }
                        onButtonClickListener = {
                            startActivity(HomeActivity.getHomeIntent(this@MentalHealthMoodActivity))
                            finishAffinity()
                        }
                    }

                    moodDialog.showDialog(supportFragmentManager)
                }
        }
    }

    private fun onArticleClicked(article: InfoPage) {
        firebaseAnalyticsHelper.mentalHealthFeelingArticle(article)
        startActivity(HomeActivity.getHomeIntent(this@MentalHealthMoodActivity))

        if (article.categoryView == "link") {
            article.destination?.android?.let { link -> openURL(link) }
        } else {
            startActivity(
                DiscoverDetailsIntent(
                    articleId = article.id,
                    source = "mental_health_mood"
                )
            )
        }
        finishAffinity()
    }
}
