package com.flipsidegroup.active10.presentation.discover_details

import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter


interface DiscoverDetailsPresenter : LifecycleAwarePresenter<DiscoverDetailsView> {

    fun loadPageParts(
        articleId: Long? = null,
        source: String,
        searchText: String? = null,
        articleSlug: String? = null
    )

}

interface DiscoverDetailsView : BaseView {

    fun showData(data: List<ArticlePartAT>, title: String)

    fun navigateToUrl(url: String?)

    fun navigateToYoutubeVideoPlayer(youtubeVideoId: String)

    fun openNewArticle(infoPage: InfoPage)

    fun showConfettiAnimation(show: Boolean)

    fun navigateToMentalHealthMood()

    fun navigateToNhsSignIn()

    fun changeColorsToMentalHealth()

}
