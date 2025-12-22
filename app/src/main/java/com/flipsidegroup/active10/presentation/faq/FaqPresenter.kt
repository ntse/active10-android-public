package com.flipsidegroup.active10.presentation.faq

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter


interface FaqPresenter : LifecycleAwarePresenter<FaqView> {

    fun getFaq()

    fun toggleListItem(faqListItem: FaqListItem)
}