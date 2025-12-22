package com.flipsidegroup.active10.presentation.onboarding.presenter

import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.LegalRules
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.onboarding.fragments.TERMS_AND_CONDITIONS
import com.flipsidegroup.active10.presentation.onboarding.view.TermsAndConditionsView


class TermsAndConditionsPresenterImpl internal constructor(
    private val localRepository: LocalRepository
) :
    BasePresenter<TermsAndConditionsView>(), TermsAndConditionsPresenter {

    override fun getContent() {
        localRepository.getOnboarding(object : AppDatabase.OnDataLoadedListener<Onboarding?> {
            override fun onDataLoaded(data: Onboarding?) {
                view?.onContentReceived(data?.onboardingPermission)
            }
        })
    }

    override fun getTermsAndCondLastVersion() {
        localRepository.getGlobalRules(object : AppDatabase.OnDataLoadedListener<GlobalRules?> {
            override fun onDataLoaded(gr: GlobalRules?) {
                localRepository.getLegalRules(object :
                    AppDatabase.OnDataLoadedListener<List<LegalRules>> {
                    override fun onDataLoaded(data: List<LegalRules>) {
                        gr?.termsAndConditions?.latestVersion =
                            data.firstOrNull { it.pageType == TERMS_AND_CONDITIONS }?.version.toString()
                        view?.onTermsRulesRetrieved(gr?.termsAndConditions)
                    }
                })
            }
        })
    }
}