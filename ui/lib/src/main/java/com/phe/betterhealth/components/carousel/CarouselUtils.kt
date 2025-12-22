package com.phe.betterhealth.components.carousel

import com.phe.betterhealth.components.articles.ArticlePage

fun getMissionContentDescription(isCompleted: Boolean) =
    buildString {
        append("Mission ")
        if (!isCompleted) append("not ")
        append("completed")
    }

fun getCarouselSlideContentDescription(infoPage: ArticlePage, includeDescription: Boolean) =
    buildString {
        append(infoPage.categoryLabel)
        append(infoPage.title)
        if (includeDescription) append(infoPage.description)
    }
