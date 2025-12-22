package com.flipsidegroup.active10.presentation.discover_details

import androidx.annotation.StringRes
import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.components.articles.ArticlePageContent
import com.phe.betterhealth.components.share.ShareButton

enum class ArticleViewTypeAT {
        HEADER,
        HEADING,
        CONTENT,
        LINK,
        SHARE,
        MISSION,
        CTA,
        BULLET_LIST,
        WHITE_BULLET_LIST,
        NUMBERED_LIST,
        IMAGE,
        VIDEO,
        RELATED_ARTICLE_HEADER,
        RELATED_ARTICLE
}


sealed class ArticlePartAT(
        val type: ArticleViewTypeAT,
        val id: Int,
)

data class ArticleHeaderAT(
        val title: String,
        val description: String,
        val imageUrl: String?,
        val isMissionCompleted: Boolean,
) : ArticlePartAT(ArticleViewTypeAT.HEADER, -1)

data class ArticleContentAT(
        val content: ArticlePageContent,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.CONTENT, contentId)

data class ArticleLinkAT(
    val content: ArticlePageContent,
    val contentDescription: String?,
    val contentId: Int,
    val url: String?,
    val analyticsTag: String?,
    val onClickCallback: (ArticleLinkAT) -> Unit,
    val isMentalHealthColors: Boolean
) : ArticlePartAT(ArticleViewTypeAT.LINK, contentId) {
        companion object // needed to making extensions
}

data class ArticleHeadingAT(
        val heading: ArticlePageContent,
        val headingId: Int,
        val backgroundColor: Int? = null,
) : ArticlePartAT(ArticleViewTypeAT.HEADING, headingId)

data class ArticleButtonShareAT(
        override val shareButtonStyle: String,
        override val shareButtonTitle: String,
        override val shareButtonDescription: String,
        override val shareButtonAccessibilityLabel: String,
        val onClickCallback: () -> Unit,
) : ArticlePartAT(ArticleViewTypeAT.SHARE, -2), ShareButton

data class ArticleButtonMissionAT(
        val isCompleted: Boolean,
        val onClickCallback: () -> Unit,
) : ArticlePartAT(ArticleViewTypeAT.MISSION, -3)

data class ArticleButtonCtaAT(
    val title: String,
    val onClickCallback: () -> Unit,
    val isMentalHealthColors: Boolean
) : ArticlePartAT(ArticleViewTypeAT.CTA, -4)

data class ArticleBulletListAT(
        val list: List<String>,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.BULLET_LIST, contentId)

data class ArticleWhiteBulletListAT(
        val list: List<String>,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.WHITE_BULLET_LIST, contentId)

data class ArticleImageAT(
        val imageUrl: String,
        val title: String,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.IMAGE, contentId){
        companion object // needed to making extensions
}

data class ArticleVideoAT(
        val imageUrl: String,
        val youtubeUrl: String,
        val contentId: Int,
        val onClickCallback: (String) -> Unit,
) : ArticlePartAT(ArticleViewTypeAT.VIDEO, contentId){
        companion object // needed to making extensions
}

data class ArticleNumberedListAT(
        val list: List<String>,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.NUMBERED_LIST, contentId)


data class ArticleRelatedHeaderAT(
        @StringRes val headerTextRes: Int,
        val headingId: Int
) : ArticlePartAT(ArticleViewTypeAT.RELATED_ARTICLE_HEADER, headingId)


data class ArticleRelatedAT(
        val onClickCallback: (ArticleRelatedAT) -> Unit,
        val articlePage: ArticlePage,
        val contentId: Int,
) : ArticlePartAT(ArticleViewTypeAT.RELATED_ARTICLE, contentId)
