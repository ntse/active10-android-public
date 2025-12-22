package com.flipsidegroup.active10.presentation.discover.adapter

import com.flipsidegroup.active10.data.models.api.InfoPageWrapper
import com.flipsidegroup.active10.data.models.api.ScreenContent


enum class GeneralViewType {
    ROOT_PAGE,
    CAROUSEL_SMALL_ITEMS,
    CAROUSEL_DOUBLE_ITEMS,
    CAROUSEL_LARGE_ITEMS,
    ARTICLE_LIST,
    ARTICLE_LIST_ITEM,
    ARTICLE_LIST_SMALL_ITEMS,
    ARTICLE_LIST_SMALL_ITEM_ELEMENT,
    HERO_ITEM,
    UNKNOWN;

    companion object {
        fun fromPageType(pageType: String?) = entries.singleOrNull {
            it.name.lowercase() == pageType
        } ?: UNKNOWN
    }
}

sealed interface GeneralScreen {
    val viewType: GeneralViewType?
}

data class GeneralScreenItem(
    val item: ScreenContent,
    val infoPages: List<InfoPageWrapper> = emptyList(),
    override val viewType: GeneralViewType? = GeneralViewType.fromPageType(item.type),
) : GeneralScreen

data class GeneralInfoItem(
    val item: InfoPageWrapper,
    val screenContent: ScreenContent,
    override val viewType: GeneralViewType?,
) : GeneralScreen
