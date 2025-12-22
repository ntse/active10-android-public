package com.phe.betterhealth.components.articles

import androidx.annotation.ColorRes
import com.phe.betterhealth.components.share.ShareButton

enum class ArticleViewType {
    HEADER,
    HEADER_WITH_BACKGROUND,
    HEADING,
    CONTENT,
    LINK,
    SHARE,
    MISSION,
    CTA,
    BULLET_LIST,
    NUMBERED_LIST,
    IMAGE,
    VIDEO,
    MENTAL_HEALTH_CTA,
    MENTAL_LINK,
    RELATED_ARTICLES_HEADING,
    RELATED_ARTICLE
}

sealed class ArticlePart(
    val type: ArticleViewType,
    val id: Int,
)

data class ArticleHeader(
    val title: String,
    val category: String?,
    val imageUrl: String?,
    val isMissionCompleted: Boolean,
) : ArticlePart(ArticleViewType.HEADER, -1)

data class ArticleHeaderWithBackground(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val isMissionCompleted: Boolean,
    @ColorRes val backgroundColor: Int? = null,
) : ArticlePart(ArticleViewType.HEADER_WITH_BACKGROUND, -1)

data class ArticleContent(
    val content: ArticlePageContent,
    val contentId: Int,
) : ArticlePart(ArticleViewType.CONTENT, contentId)

data class ArticleLink(
    val content: ArticlePageContent,
    val contentDescription: String?,
    val contentId: Int,
    val url: String?,
    val analyticsTag: String?,
    val onClickCallback: (ArticleLink) -> Unit,
) : ArticlePart(ArticleViewType.LINK, contentId) {
    companion object // needed to making extensions
}

data class ArticleMentalHealthLink(
    val content: ArticlePageContent,
    val contentDescription: String?,
    val contentId: Int,
    val url: String?,
    val analyticsTag: String?,
    val onClickCallback: (ArticleMentalHealthLink) -> Unit,
) : ArticlePart(ArticleViewType.MENTAL_LINK, contentId) {
    companion object // needed to making extensions
}

data class ArticleHeading(
    val heading: ArticlePageContent,
    val headingId: Int
) : ArticlePart(ArticleViewType.HEADING, headingId)

data class ArticleButtonShare(
    override val shareButtonStyle: String,
    override val shareButtonTitle: String,
    override val shareButtonDescription: String,
    override val shareButtonAccessibilityLabel: String,
    val onClickCallback: () -> Unit,
) : ArticlePart(ArticleViewType.SHARE, -2), ShareButton

data class ArticleButtonMission(
    val isCompleted: Boolean,
    val onClickCallback: () -> Unit,
) : ArticlePart(ArticleViewType.MISSION, -3)

data class ArticleButtonCta(
    val title: String,
    val onClickCallback: () -> Unit,
) : ArticlePart(ArticleViewType.CTA, -4)

data class ArticleButtonMentalHealthCta(
    val title: String,
    val onClickCallback: () -> Unit,
) : ArticlePart(ArticleViewType.MENTAL_HEALTH_CTA, -5)

data class ArticleRelatedHeader(
    val title: String,
    val description: String,
) : ArticlePart(ArticleViewType.RELATED_ARTICLES_HEADING, -6)

data class ArticleBulletList(
    val list: List<String>,
    val title: String,
    val contentId: Int,
) : ArticlePart(ArticleViewType.BULLET_LIST, contentId){
    companion object // needed to making extensions
}

data class ArticleNumberedList(
    val list: List<String>,
    val title: String,
    val contentId: Int,
) : ArticlePart(ArticleViewType.NUMBERED_LIST, contentId){
    companion object // needed to making extensions
}

data class ArticleImage(
    val imageUrl: String,
    val title: String,
    val contentId: Int,
) : ArticlePart(ArticleViewType.IMAGE, contentId){
    companion object // needed to making extensions
}

data class ArticleVideo(
    val imageUrl: String,
    val youtubeUrl: String,
    val contentId: Int,
    val onClickCallback: (String) -> Unit,
) : ArticlePart(ArticleViewType.VIDEO, contentId){
    companion object // needed to making extensions
}

data class ArticleRelatedItem(
    val article: ArticlePage,
    val onClickCallback: (ArticlePage) -> Unit,
) : ArticlePart(ArticleViewType.RELATED_ARTICLE, article.id.toInt()){
    companion object // needed to making extensions
}
