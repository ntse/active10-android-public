package com.flipsidegroup.active10.presentation.legals.view

import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.discover_details.ArticlePartAT

interface LegalsView : BaseView {

    fun showData(data: List<ArticlePartAT>, title: String)

    fun navigateToUrl(url: String?)

    fun navigateToYoutubeVideoPlayer(youtubeVideoId: String)
}