package com.flipsidegroup.active10.presentation.stayUpdated

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentStayUpdatedBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.stayUpdated.presenter.StayUpdatedPresenter
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setTextHtml
import io.reactivex.Completable
import javax.inject.Inject

class StayUpdatedFragment : BaseFragment<StayUpdatedView>(),
    StayUpdatedView, StayUpdatedListener {

    @Inject
    internal lateinit var presenter: StayUpdatedPresenter

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    override fun getPresenter(): LifecycleAwarePresenter<StayUpdatedView> = presenter

    private var binding: FragmentStayUpdatedBinding by lifecycleAwareVariable()

    companion object {
        fun newInstance(isOnboarding: Boolean): StayUpdatedFragment {
            val instance = StayUpdatedFragment()
            instance.arguments = Bundle().apply {
                putBoolean(Constants.IS_ONBOARDING, isOnboarding)
            }
            return instance
        }
    }

    private var screenContent: ScreenContent? = null
    private var isOnboarding: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logScreenViewEvent()
        return inflater.inflate(R.layout.fragment_stay_updated, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is StayUpdatedInitListener) {
            context.init(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStayUpdatedBinding.bind(view)

        initializeArguments()
        setCheckboxesState()
        presenter.loadData()
    }

    private fun initializeArguments() {
        isOnboarding = arguments?.getBoolean(Constants.IS_ONBOARDING) ?: false
    }

    private fun setCheckboxesState() {
        binding.preferencesReceiveEmail.isChecked =
            settingsUtils.getSettingsHolder().nhsUser!!.isEmailUpdatesAllowed
    }

    override fun showData(content: ScreenContent) {
        screenContent = content
        content.let {
            binding.preferencesImage.loadFromUrl(it.firstImageUrl)
            binding.preferencesTitle.setTextHtml(it.title)
            binding.preferencesSubtitle.setTextHtml(it.description)
            binding.preferencesListTitle.text = it.getPropertyValue("options_title")
            binding.preferencesReceiveEmail.setTextHtml(it.getPropertyValue("option_1"))
        }
    }

    override fun saveData() {
        val isReceiveEmailAllowed = binding.preferencesReceiveEmail.isChecked

        presenter.saveData(
            isAllowReceiveEmail = isReceiveEmailAllowed,
        )
    }

    private fun logScreenViewEvent() {
        firebaseAnalyticsHelper.sendViewScreenEvent(if (preferenceRepository.isUserLoggedIn) "NHSLoginStayUpdated" else "StayUpdated")
    }
}