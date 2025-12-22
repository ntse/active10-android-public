package com.flipsidegroup.active10.presentation.faq

import com.flipsidegroup.active10.presentation.common.view.BaseView


interface FaqView : BaseView {

    fun onGetFaqCompleted(faqList: List<FaqListItem>)
}