package com.flipsidegroup.active10.presentation.faq

import com.flipsidegroup.active10.data.FaqItem
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import javax.inject.Inject


class FaqPresenterImpl @Inject constructor(
    private val localRepository: LocalRepository
) : BasePresenter<FaqView>(), FaqPresenter {

    var faqListItems = listOf<FaqListItem>()

    override fun getFaq() {
        localRepository.getFaqContent(object : AppDatabase.OnDataLoadedListener<List<FaqItem>> {
            override fun onDataLoaded(data: List<FaqItem>) {
                faqListItems = data.map { FaqListItem(it, false) }

                view?.onGetFaqCompleted(faqListItems)
            }
        })
    }

    override fun toggleListItem(faqListItem: FaqListItem) {
        faqListItems = faqListItems.map {
            if (it.faqItem.id == faqListItem.faqItem.id) {
                it.copy(isExpanded = !it.isExpanded)
            } else it
        }

        view?.onGetFaqCompleted(faqListItems)
    }
}