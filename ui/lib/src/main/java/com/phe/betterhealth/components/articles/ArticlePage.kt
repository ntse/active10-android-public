package com.phe.betterhealth.components.articles

interface ArticlePage : java.io.Serializable {
    val id: Long
    val slug: String
    val published: Boolean
    val title: String
    val description: String
    val content: List<ArticlePageContent>
    val category: Int
    val analyticsTag: String
    val platform: String
    val imageUrl: String
    val buttonTitle: String
    val buttonAnalyticsTag: String
    val buttonUrl: String
    val buttonAccessibilityLabel: String
    val categoryId: Int
    val categoryLabel: String
    val categoryPosition: Int
    val categoryView: String
    val relatedSectionTitle: String?
    val relatedSectionDescription: String?
    val relatedInfoPages: List<Long>
}

interface ArticlePageContent : java.io.Serializable {
    val type: String
    val body: String?
}
