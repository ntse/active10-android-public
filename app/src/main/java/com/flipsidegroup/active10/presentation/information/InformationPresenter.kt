package com.flipsidegroup.active10.presentation.information

import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class InformationPresenter @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val preferenceRepository: PreferenceRepository,
    private val analyticsHelper: FirebaseAnalyticsHelper
): BasePresenter<InformationContract.View>(), InformationContract.Presenter {

    private lateinit var screenId: String

    override fun loadContent(screenId: String) {
        this.screenId = screenId
        screenRepository.getScreenContentBySlug(screenId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { screen ->
                view?.configureView(
                    screen.title,
                    screen.description,
                    screen.firstImageUrl,
                    screen.getPropertyValue("first_button_title"),
                    screen.getPropertyValue("second_button_title"),
                    screen.getPropertyValue("background_colour") != null
                )
            }
            .addToDisposables()
    }

    override fun onPrimaryButtonClicked() {
        when (screenId) {
            Constants.CoachAppIntroduction.POPUP_SMASHING_IT -> {
                analyticsHelper.logCoach5kSmashingItYesEvent()
                preferenceRepository.coachAppSmashingItAnswer = "yes"
                view?.openNextInformationScreen(Constants.CoachAppIntroduction.POPUP_DOWNLOAD_C25K)
            }
            Constants.CoachAppIntroduction.POPUP_DOWNLOAD_C25K -> {
                analyticsHelper.logCoach5kDownloadYesEvent()
                preferenceRepository.coachAppDownloadAppAnswer = "yes"
                view?.openPlayStore(Constants.CoachAppIntroduction.COACH_PACKAGE_NAME)
                view?.close()
            }
            Constants.CoachAppIntroduction.POPUP_C25K_NO_THANKS,
            Constants.CoachAppIntroduction.POPUP_RUNNING_NOT_FOR_ME -> {
                view?.close()
            }
            else -> throw IllegalStateException("Unknown screen id")
        }
    }

    override fun onSecondaryButtonClicked() {
        when (screenId) {
            Constants.CoachAppIntroduction.POPUP_SMASHING_IT -> {
                analyticsHelper.logCoach5kSmashingItNoEvent()
                preferenceRepository.coachAppSmashingItAnswer = "no"
                view?.openNextInformationScreen(Constants.CoachAppIntroduction.POPUP_C25K_NO_THANKS)
            }
            Constants.CoachAppIntroduction.POPUP_DOWNLOAD_C25K -> {
                analyticsHelper.logCoach5kDownloadNoEvent()
                preferenceRepository.coachAppDownloadAppAnswer = "no"
                view?.openNextInformationScreen(Constants.CoachAppIntroduction.POPUP_RUNNING_NOT_FOR_ME)
            }
            Constants.CoachAppIntroduction.POPUP_C25K_NO_THANKS,
            Constants.CoachAppIntroduction.POPUP_RUNNING_NOT_FOR_ME -> {
                view?.close()
            }
            else -> throw IllegalStateException("Unknown screen id")
        }
    }

}