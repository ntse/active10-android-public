package com.flipsidegroup.active10.presentation.discover_details

import androidx.core.net.toUri
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.InternalLinkUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.fromInfoPageContent
import com.flipsidegroup.active10.utils.getYoutubeVideoId
import com.flipsidegroup.active10.utils.removeHtmlTags
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


class DiscoverDetailsPresenterImpl(
    private val discoverRepository: DiscoverRepository,
    private val preferenceRepository: PreferenceRepository,
    private val settingsUtils: SettingsUtils,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper
) : BasePresenter<DiscoverDetailsView>(), DiscoverDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    private var currentArticle: InfoPage? = null
    private var source: String = ""
    private var searchTerm: String? = null
    private var currentArticleId = 0L

    private var isMentalHealthArticle = false

    override fun loadPageParts(
        articleId: Long?,
        source: String,
        searchText: String?,
        articleSlug: String?
    ) {
        this.source = source
        this.searchTerm = searchText
        view?.showLoading()
        discoverRepository.getArticles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { articles -> handleArticles(articles, articleId, articleSlug) },
                { error -> handleError(error) }
            ).addToDisposables()
    }

    private fun handleError(error: Throwable?) {
        view?.hideLoading()
        view?.showAlert(error)
    }

    private fun handleArticles(
        articles: List<InfoPage>,
        articleId: Long?,
        articleSlug: String? = null
    ) {
        val currentArticle = articles.first { it.id == articleId || it.slug == articleSlug }
        this.currentArticle = currentArticle
        currentArticleId = currentArticle.id

        val relatedArticles =
            articles.filter { it.id in currentArticle.relatedInfoPages }
                .sortedBy { currentArticle.relatedInfoPages.indexOf(it.id) }
        isMentalHealthArticle = "Mental Health" in currentArticle.categoryLabel
        val header = createHeader(currentArticle)
        val content = createContent(currentArticle, relatedArticles, isMentalHealthArticle)


        view?.showData(buildList {
            add(header)
            addAll(content)
        }, currentArticle.title)

        if (isMentalHealthArticle) {
            view?.changeColorsToMentalHealth()
            firebaseAnalyticsHelper.mentalHealthFlowStart(currentArticle)
        }

        view?.hideLoading()

        earnRewards(currentArticle.slug)
        sendEvent(currentArticle)
    }

    private fun sendEvent(currentArticle: InfoPage) {
        firebaseAnalyticsHelper.saveArticleRead(
            currentArticle.slug,
            currentArticle.title,
            source,
            searchTerm
        )
    }

    private fun earnRewards(articleSlug: String) {
        preferenceRepository.countOfReadArticles++

        if (preferenceRepository.countOfReadArticles >= 2) {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.STAR_STUDENT,
                preferenceRepository = preferenceRepository
            )
        }

        if (preferenceRepository.countOfReadArticles >= 5) {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.HEALTH_GURU,
                preferenceRepository = preferenceRepository
            )
        }

        if (articleSlug == "Discover_article_better_health_overview") {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.GET_SET_GO,
                preferenceRepository = preferenceRepository
            )
        }

        if (articleSlug == "health_unlocked") {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.COMMUNITY_CHAMPION,
                preferenceRepository = preferenceRepository
            )
        }
    }

    private fun createContent(
        infoPage: InfoPage,
        relatedArticles: List<InfoPage>,
        isMentalHealthColors: Boolean
    ) = infoPage
        .contentList
        .mapIndexedNotNull { index, it ->
            when (it.type) {
                "header" -> ArticleHeadingAT(it, index)
                "content" -> if (!it.body?.removeHtmlTags()?.trim()
                        .isNullOrBlank()
                ) ArticleContentAT(it, index) else null

                "link" -> ArticleLinkAT.fromInfoPageContent(
                    item = it,
                    i = index,
                    callback = { articleLink -> buttonClicked(articleLink) },
                    isMentalHealthColors = isMentalHealthColors
                )

                "bullet_list" -> {
                    ArticleBulletListAT(
                        list = Jsoup.parse(it.body!!).select("li").map { it.html() },
                        contentId = index
                    )
                }

                "numbered_list" -> {
                    ArticleNumberedListAT(
                        list = Jsoup.parse(it.body!!).select("li").map { it.html() },
                        contentId = index
                    )
                }

                else -> null
            }
        }
        .addCTAButton(infoPage, isMentalHealthColors)
        .addRelatedArticles(relatedArticles, infoPage)

    private fun List<ArticlePartAT>.addCTAButton(
        infoPage: InfoPage,
        isMentalHealthColors: Boolean
    ): List<ArticlePartAT> {
        return if (infoPage.buttonTitle.isNotBlank() && infoPage.buttonUrl.isNotBlank()) {
            this.plus(
                ArticleButtonCtaAT(
                    title = infoPage.buttonTitle,
                    onClickCallback = { navigateToLink(infoPage.buttonUrl) },
                    isMentalHealthColors = isMentalHealthColors,
                )
            )
        } else {
            this
        }
    }

    private fun List<ArticlePartAT>.addRelatedArticles(
        relatedArticles: List<InfoPage>,
        infoPage: InfoPage
    ) = if (relatedArticles.isNotEmpty()) {
        plus(
            ArticleRelatedHeaderAT(
                R.string.more_to_explore,
                infoPage.contentList.size
            )
        ).plus(relatedArticles.mapIndexed { i, relatedArticle ->
            ArticleRelatedAT(
                articlePage = relatedArticle,
                contentId = infoPage.contentList.size + i + 1,
                onClickCallback = ::onArticleSelected
            )
        })
    } else {
        this
    }

    private fun buttonClicked(articleLink: ArticleLinkAT) {
        val buttonTitle = articleLink.content.body ?: ""
        firebaseAnalyticsHelper.sendArticleButtonClickedEvent(
            currentArticle?.slug ?: "",
            buttonTitle
        )
        articleLink.url?.let { navigateToLink(it) }
    }

    private fun navigateToLink(url: String) {
        if ("i_did_it" in url) {
            firebaseAnalyticsHelper.mentalHealthIDidItClicked()
            presenterScope.launch {
                view?.showConfettiAnimation(true)
                delay(3000)
                view?.showConfettiAnimation(false)
                view?.navigateToMentalHealthMood()
            }
            return
        }

        // Link format - active10://nhs_login/sign_in
        if (InternalLinkUtils.resolveDestination(url.toUri()) == InternalLinkUtils.Destination.SIGN_IN) {
            view?.navigateToNhsSignIn()
            return
        }

        val youtubeVideoId = url.getYoutubeVideoId()
        if (youtubeVideoId != null) {
            view?.navigateToYoutubeVideoPlayer(youtubeVideoId)
        } else {
            view?.navigateToUrl(url)
        }
    }

    private fun createHeader(infoPage: InfoPage): ArticleHeaderAT = ArticleHeaderAT(
        title = infoPage.title,
        description = infoPage.description,
        imageUrl = infoPage.imageUrl,
        isMissionCompleted = false
    )

    private fun onArticleSelected(articleRelated: ArticleRelatedAT) {
        (articleRelated.articlePage as InfoPage).let { page ->
            if (isMentalHealthArticle) {
                firebaseAnalyticsHelper.mentalHealthRelatedArticle(page)
            }

            if (page.categoryView == "link") {
                view?.navigateToUrl(page.destination?.android)
            } else {
                view?.openNewArticle(page)
            }
        }
    }

    override fun unbind() {
        firebaseAnalyticsHelper.mentalHealthFlowEnd()
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}
