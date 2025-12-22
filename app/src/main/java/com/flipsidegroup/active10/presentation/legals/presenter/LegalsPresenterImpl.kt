package com.flipsidegroup.active10.presentation.legals.presenter

import com.flipsidegroup.active10.data.LegalRules
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.discover_details.ArticleBulletListAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleContentAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleHeadingAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleLinkAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleNumberedListAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleWhiteBulletListAT
import com.flipsidegroup.active10.presentation.legals.view.LegalsView
import com.flipsidegroup.active10.utils.fromInfoPageContent
import com.flipsidegroup.active10.utils.getYoutubeVideoId
import com.flipsidegroup.active10.utils.removeHtmlTags
import org.jsoup.Jsoup

class LegalsPresenterImpl(val localRepository: LocalRepository) : BasePresenter<LegalsView>(),
    LegalsPresenter {

    override fun getLegalRules(legalScreenType: String) {
        localRepository.getLegalRules(object : AppDatabase.OnDataLoadedListener<List<LegalRules>> {
            override fun onDataLoaded(data: List<LegalRules>) {
                handleData(data.first { it.pageType == legalScreenType })
            }
        })
    }

    private fun handleData(rules: LegalRules) {
        val data = rules.content?.mapIndexedNotNull { i, it ->
            when (it.type) {
                "header" -> ArticleHeadingAT(it, i)
                "content" -> if (it.body.removeHtmlTags().trim().isNotBlank()) ArticleContentAT(
                    it,
                    i
                ) else null

                "link" -> ArticleLinkAT.fromInfoPageContent(
                    it,
                    i,
                    { articleLink ->
                        articleLink.url?.let { navigateToLink(it) }
                    }, false
                )

                "bullet_list" -> {
                    ArticleBulletListAT(
                        list = Jsoup.parse(it.body).select("li").map { it.html() },
                        contentId = i
                    )
                }

                "white_bullet_list" -> {
                    ArticleWhiteBulletListAT(
                        list = Jsoup.parse(it.body).select("li").map { it.html() },
                        contentId = i
                    )
                }

                "numbered_list" -> {
                    ArticleNumberedListAT(
                        list = Jsoup.parse(it.body).select("li").map { it.html() },
                        contentId = i
                    )
                }

                else -> null
            }
        } ?: emptyList()
        view?.showData(data, rules.title)
    }

    private fun navigateToLink(url: String) {
        val youtubeVideoId = url.getYoutubeVideoId()
        if (youtubeVideoId != null) {
            view?.navigateToYoutubeVideoPlayer(youtubeVideoId)
        } else {
            view?.navigateToUrl(url)
        }
    }

}