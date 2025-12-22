package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.models.api.InfoPageContent
import com.flipsidegroup.active10.presentation.discover_details.ArticleHeaderAT
import com.flipsidegroup.active10.presentation.discover_details.ArticleLinkAT
import com.phe.betterhealth.components.articles.ArticlePageContent
import com.phe.betterhealth.components.articleswlp.ArticleHeaderWlp
import org.jsoup.Jsoup

fun ArticleLinkAT.Companion.fromInfoPageContent(
    item: ArticlePageContent,
    i: Int,
    callback: (ArticleLinkAT) -> Unit,
    isMentalHealthColors: Boolean
): ArticleLinkAT {
    val link = Jsoup.parse(item.body!!).select("a")
    val title = link.text()
    val href = link.attr("href")
    val contentDescription = link.attr("title")
    val analyticsTag = link.attr("rel")

    return ArticleLinkAT(
        content = InfoPageContent(title, (item as? InfoPageContent)?.order, item.type),
        contentId = i,
        url = href,
        contentDescription = contentDescription,
        analyticsTag = analyticsTag,
        onClickCallback = callback,
        isMentalHealthColors = isMentalHealthColors
    )
}

fun ArticleHeaderAT.toWlpHeader(backgroundColor: Int): ArticleHeaderWlp {
    return ArticleHeaderWlp(
        title = title,
        description = description,
        imageUrl = imageUrl,
        isMissionCompleted = isMissionCompleted,
        backgroundColor = backgroundColor
    )
}