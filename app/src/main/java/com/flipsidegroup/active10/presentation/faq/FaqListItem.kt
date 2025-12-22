package com.flipsidegroup.active10.presentation.faq

import com.flipsidegroup.active10.data.FaqItem

data class FaqListItem(

    val faqItem: FaqItem,

    val isExpanded: Boolean
)