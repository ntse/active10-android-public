package com.flipsidegroup.active10.presentation.discover_details

import com.phe.betterhealth.components.articles.*
import com.phe.betterhealth.components.articleswlp.*


fun ArticleHeaderAT.toArticlePart() = ArticleHeader(title, null, imageUrl, isMissionCompleted)

fun ArticleButtonShareAT.toArticlePart() = ArticleButtonShare(shareButtonStyle, shareButtonTitle, shareButtonDescription, shareButtonAccessibilityLabel, onClickCallback)
