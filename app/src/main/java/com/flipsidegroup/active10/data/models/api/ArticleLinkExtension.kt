package com.flipsidegroup.active10.data.models.api

import com.phe.betterhealth.components.articles.ArticleLink
import org.jsoup.Jsoup


fun ArticleLink.Companion.fromInfoPageContent(item: InfoPageContent, i: Int, callback: (ArticleLink) -> Unit): ArticleLink {
    val link = Jsoup.parse(item.body).select("a")
    return ArticleLink(
        content = InfoPageContent(link.text(), item.order, item.type),
        contentId = i,
        url = link.attr("href"),
        contentDescription = link.attr("title"),
        analyticsTag = link.attr("rel"),
        onClickCallback = callback
    )
}

