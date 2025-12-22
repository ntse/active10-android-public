package com.phe.betterhealth.components.articleswlp

import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.components.articles.ArticlePageContent
import com.phe.betterhealth.components.share.ShareButton

enum class ArticleViewTypeWlp {
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
    RELATED_ARTICLES_HEADING,
    ARTICLE,
    SPACER,
}

sealed class ArticlePartWlp(
    val type: ArticleViewTypeWlp,
    val id: Int,
)

data class ArticleHeaderWlp(
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val isMissionCompleted: Boolean,
    val backgroundColor: Int? = null
) : ArticlePartWlp(ArticleViewTypeWlp.HEADER, -1)

data class ArticleContentWlp(
    val content: ArticlePageContent,
    val contentId: Int,
) : ArticlePartWlp(ArticleViewTypeWlp.CONTENT, contentId)

data class ArticleLinkWlp(
    val content: ArticlePageContent,
    val contentDescription: String?,
    val contentId: Int,
    val url: String?,
    val analyticsTag: String?,
    val backgroundColor: Int? = null,
    val onClickCallback: (ArticleLinkWlp) -> Unit,
) : ArticlePartWlp(ArticleViewTypeWlp.LINK, contentId) {
    companion object // needed to making extensions
}

data class ArticleHeadingWlp(
    val heading: ArticlePageContent,
    val headingId: Int
) : ArticlePartWlp(ArticleViewTypeWlp.HEADING, headingId)

data class ArticleButtonShareWlp(
    override val shareButtonStyle: String,
    override val shareButtonTitle: String,
    override val shareButtonDescription: String,
    override val shareButtonAccessibilityLabel: String,
    val onClickCallback: () -> Unit,
) : ArticlePartWlp(ArticleViewTypeWlp.SHARE, -2), ShareButton

data class ArticleButtonMissionWlp(
    val isCompleted: Boolean,
    val onClickCallback: () -> Unit,
) : ArticlePartWlp(ArticleViewTypeWlp.MISSION, -3)

data class ArticleButtonCtaWlp(
    val title: String,
    val onClickCallback: () -> Unit,
) : ArticlePartWlp(ArticleViewTypeWlp.CTA, -4)

data class ArticleRelatedHeaderWlp(
    val title: String,
    val description: String,
) : ArticlePartWlp(ArticleViewTypeWlp.RELATED_ARTICLES_HEADING, -5)

data class ArticleSpacerWlp(
    val heightInDp: Float? = null,
) : ArticlePartWlp(ArticleViewTypeWlp.SPACER, -6)

data class ArticleBulletListWlp(
    val list: List<String>,
    val contentId: Int,
) : ArticlePartWlp(ArticleViewTypeWlp.BULLET_LIST, contentId)

data class ArticleWhiteBulletListWlp(
    val list: List<String>,
    val contentId: Int,
) : ArticlePartWlp(ArticleViewTypeWlp.WHITE_BULLET_LIST, contentId)

data class ArticleNumberedListWlp(
    val list: List<String>,
    val contentId: Int,
) : ArticlePartWlp(ArticleViewTypeWlp.NUMBERED_LIST, contentId)

data class ArticleRelatedWlp(
    val onClickCallback: (ArticleRelatedWlp) -> Unit,
    val articlePage: ArticlePage,
    val contentId: Int,
    val indicatorColor: Int,
) : ArticlePartWlp(ArticleViewTypeWlp.ARTICLE, contentId)
