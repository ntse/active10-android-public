package com.flipsidegroup.active10.presentation.userDetails.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.PermissionEnum
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentUserDetailsBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.CommonBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.DialogButtonModel
import com.flipsidegroup.active10.presentation.onboarding.activities.PermissionActivity
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_ABOUT_YOU_SKIPPED
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.USER_GENDER_FEMALE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.USER_GENDER_MALE
import com.flipsidegroup.active10.utils.InputEditText
import com.flipsidegroup.active10.utils.addGlobalLayoutListenerToHideViewWhenKeyboardShown
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.removeGlobalLayoutListener
import com.flipsidegroup.active10.utils.setHeading
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

private const val USER_DETAILS_POSITION_KEY = "in_permission_position_key"
private const val USER_DETAILS_ONBOARDING_KEY = "user_details_onboarding_key"

class UserDetailsFragment : BaseFragment<BaseView>() {

    private var binding: FragmentUserDetailsBinding by lifecycleAwareVariable()

    @Inject
    lateinit var settingsUtils: SettingsUtils

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    lateinit var screenRepository: ScreenRepository

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var loginRepository: LoginRepository

    private val disposable = CompositeDisposable()

    private var genderSelected = false
        set(value) {
            field = value
            handleContinueValidation()
        }

    private var userGender = ""
        set(value) {
            field = value
            genderSelected = true
        }

    private var userActivityLevel: ActivityLevelEnum? = null
        set(value) {
            field = value
            handleContinueValidation()
        }

    private var userAge: Int? = null
        set(value) {
            field = value
            handleContinueValidation()
        }

    private var shouldShowAgeValidationDialog = true

    private val continueButtonValidation = MutableLiveData<Boolean>()

    private lateinit var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    companion object {
        fun newInstance(position: Int, description: String): UserDetailsFragment {
            val instance = UserDetailsFragment()
            instance.arguments = Bundle().apply {
                putInt(USER_DETAILS_POSITION_KEY, position)
                putString(USER_DETAILS_ONBOARDING_KEY, description)
            }
            return instance
        }

        const val DEFAULT_INDEX = -1
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    private var permissionEnum: PermissionEnum? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_details, container, false)
        setContentForTypeOfUser(view = view)
        return view
    }

    override fun onStart() {
        super.onStart()

        val parentLayout = when (val currentActivity = activity) {
            is PermissionActivity -> currentActivity.permissionsParentLayout
            else -> binding.userDetailsParentLayout
        }
        keyboardLayoutListener = binding.userDetailsParentLayout
            .addGlobalLayoutListenerToHideViewWhenKeyboardShown(
                buttonView = binding.actionButtonsLayout,
                containerView = parentLayout,
            )
    }

    override fun onStop() {
        super.onStop()
        binding.userDetailsParentLayout.removeGlobalLayoutListener(keyboardLayoutListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDetailsBinding.bind(view)

        val permissionPos = arguments?.getInt(USER_DETAILS_POSITION_KEY)
        val description = arguments?.getString(USER_DETAILS_ONBOARDING_KEY)
        permissionPos ?: return

        if (permissionPos != DEFAULT_INDEX) {
            permissionEnum = PermissionEnum.values()[permissionPos]
        }

        binding.userDetailsTitleTV.setHeading()
        setUpViews(description)
        continueButtonValidation.observe(viewLifecycleOwner) {
            binding.addMyDetailsButton.isEnabled = it
        }
    }

    private fun setContentForTypeOfUser(view: View) {
        if (preferenceRepository.isUserLoggedIn) {
            view.findViewById<TextView>(R.id.userDetailsTitleTV).text =
                requireContext().getText(R.string.activity_level_title)
            view.findViewById<ConstraintLayout>(R.id.loggedOutUserDetailsLayout).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.userDetailsTitleTV).text =
                requireContext().getText(R.string.about_you_title)
            view.findViewById<ConstraintLayout>(R.id.loggedOutUserDetailsLayout).visibility = View.VISIBLE
        }
    }

    private fun setUpViews(description: String?) {
        handleDescriptionText(description)
        setupAgeEditText()
        setupActivityCheckboxes()
        setupActionButtons()
        handleRadioSelection()
        handleAgeInsert()
        setupData()
    }

    private fun setupData() {
        with(settingsUtils.getSettingsHolder()) {
            when(classicUser?.gender) {
                USER_GENDER_MALE -> binding.maleRB.isChecked = true
                USER_GENDER_FEMALE -> binding.femaleRB.isChecked = true
                "" -> binding.otherRB.isChecked = true
            }
            classicUser?.let { user ->
                userAge = user.age
                user.age?.let { binding.ageEditText.setText(it.toString()) }
            }
            when(classicUser?.activityLevel) {
                ActivityLevelEnum.INACTIVE.value -> binding.userDetailsNoActiveRB.isChecked = true
                ActivityLevelEnum.MODERATELY_ACTIVE.value -> binding.userDetailsModerateActiveRB.isChecked = true
                ActivityLevelEnum.ACTIVE.value -> binding.userDetailsVeryActiveRB.isChecked = true
            }
        }
    }

    private fun handleAgeInsert() {
        binding.ageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //no operation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //no operation needed
                shouldShowAgeValidationDialog = true
            }

            override fun afterTextChanged(s: Editable?) {
                userAge = if (!s.isNullOrBlank()) s.toString().toInt() else null
            }

        })
    }

    private fun handleContinueValidation() {
        continueButtonValidation.value =
            genderSelected || userActivityLevel != null || userAge != null
    }

    private fun handleDescriptionText(description: String?) {
        if (!description.isNullOrEmpty()) {
            binding.descriptionTV.text = description
        } else {
            binding.descriptionTV.isVisible = false
        }
    }

    private fun setupActionButtons() {
        binding.addMyDetailsButton.setOnClickListener {
            settingsUtils.updateSettings(SettingsDataHolder(areUserDetailsSet = true))
            collectUserDataAndNavigateNext()
        }

        binding.skipButton.setOnClickListener {
            settingsUtils.updateSettings(SettingsDataHolder(areUserDetailsSet = true))
            firebaseAnalyticsHelper.saveEvent(EVENT_ABOUT_YOU_SKIPPED)
            startActivity(requireContext().TargetIntent(SetTargetMode.ONBOARDING))
        }
    }

    private fun setupAgeEditText() {
        binding.ageEditText.setMaxLines(1)
        binding.ageEditText.setMaxCharacters(2)
        binding.ageEditText.setInputType(InputType.TYPE_CLASS_NUMBER)

        binding.ageEditText.findViewById<InputEditText>(R.id.input_edit)
            .setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isUserAgeValid()
                }
                false
            }
    }

    private fun setupActivityCheckboxes() {
        screenRepository.getScreenContentBySlug("about_you")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { content ->
                    setupActivityCheckBoxes(content)
                },
                { error ->
                    Timber.d("Cannot load about_you view : ${error.message}")
                    setupActivityCheckBoxes(null)
                }
            ).addTo(disposable)
    }

    private fun setupActivityCheckBoxes(screenContent: ScreenContent?) {
        val inactiveText = screenContent?.getPropertyValue("inactive_text")
        val moderateText = screenContent?.getPropertyValue("moderately_active_text")
        val activeText = screenContent?.getPropertyValue("active_text")

        binding.inactiveCheckboxTitle.text = getString(R.string.inactive_title)
        binding.inactiveCheckboxText.text = inactiveText ?: getString(R.string.inactive_description)
        setAccessibilityForRadioButton(
            binding.inactiveCheckboxLayout,
            binding.inactiveCheckboxTitle,
            binding.inactiveCheckboxText,
            binding.userDetailsNoActiveRB
        )

        binding.moderatelyActiveCheckboxTitle.text = getString(R.string.moderatelyActive_title)
        binding.moderatelyActiveCheckboxText.text =
            moderateText ?: getString(R.string.moderatelyActive_description)
        setAccessibilityForRadioButton(
            binding.moderatelyActiveCheckboxLayout,
            binding.moderatelyActiveCheckboxTitle,
            binding.moderatelyActiveCheckboxText,
            binding.userDetailsModerateActiveRB
        )

        binding.veryActiveCheckboxTitle.text = getString(R.string.active_title)
        binding.veryActiveCheckboxText.text =
            activeText ?: getString(R.string.active_description)
        setAccessibilityForRadioButton(
            binding.veryActiveCheckboxLayout,
            binding.veryActiveCheckboxTitle,
            binding.veryActiveCheckboxText,
            binding.userDetailsVeryActiveRB
        )

        binding.inactiveCheckboxLayout.setOnClickListener { _ ->
            binding.userDetailsNoActiveRB.isChecked = true
        }

        binding.userDetailsNoActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.INACTIVE
                binding.userDetailsNoActiveRB.isChecked = true
                binding.userDetailsModerateActiveRB.isChecked = false
                binding.userDetailsVeryActiveRB.isChecked = false
                binding.inactiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }

        binding.moderatelyActiveCheckboxLayout.setOnClickListener { _ ->
            binding.userDetailsModerateActiveRB.isChecked = true
        }

        binding.userDetailsModerateActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.MODERATELY_ACTIVE
                binding.userDetailsModerateActiveRB.isChecked = true
                binding.userDetailsNoActiveRB.isChecked = false
                binding.userDetailsVeryActiveRB.isChecked = false
                binding.moderatelyActiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }

        binding.veryActiveCheckboxLayout.setOnClickListener { _ ->
            binding.userDetailsVeryActiveRB.isChecked = true
        }

        binding.userDetailsVeryActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.ACTIVE
                binding.userDetailsVeryActiveRB.isChecked = true
                binding.userDetailsNoActiveRB.isChecked = false
                binding.userDetailsModerateActiveRB.isChecked = false
                binding.veryActiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }
    }

    private fun setAccessibilityForRadioButton(
        btnView: View,
        btnTitle: TextView,
        btnText: TextView,
        btnRadio: AppCompatRadioButton
    ) {
        ViewCompat.setAccessibilityDelegate(btnView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                v: View, info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(v, info)
                info.actionList.clear()
                info.contentDescription =
                    "${if (btnRadio.isChecked) "Selected" else "Not selected"}. " + "${btnTitle.text}. ${btnText.text}. "
                info.roleDescription = "Radio Button"
                info.addAction(
                    AccessibilityActionCompat(
                        AccessibilityNodeInfoCompat.ACTION_CLICK, "Toggle"
                    )
                )
            }
        })
    }

    private fun handleRadioSelection() {
        binding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.maleRB -> userGender = USER_GENDER_MALE
                R.id.femaleRB -> userGender = USER_GENDER_FEMALE
                R.id.otherRB -> userGender = ""
            }
        }
    }

    private fun collectUserDataAndNavigateNext() {
        if (!isUserAgeValid()) {
            return
        }

        when {
            binding.maleRB.isChecked -> {
                userGender = USER_GENDER_MALE
            }

            binding.femaleRB.isChecked -> {
                userGender = USER_GENDER_FEMALE
            }

            binding.otherRB.isChecked -> {
                userGender = ""
            }
        }

        userAge = binding.ageEditText.getText().toIntOrNull()
        userActivityLevel =
            when {
                binding.userDetailsNoActiveRB.isChecked -> {
                    ActivityLevelEnum.INACTIVE
                }

                binding.userDetailsModerateActiveRB.isChecked -> {
                    ActivityLevelEnum.MODERATELY_ACTIVE
                }

                binding.userDetailsVeryActiveRB.isChecked -> {
                    ActivityLevelEnum.ACTIVE
                }

                else -> null
            }

        settingsUtils.updateSettings(
            SettingsDataHolder(
                classicUser = ClassicUser(
                    gender = userGender,
                    age = userAge,
                    activityLevel = userActivityLevel?.value,
                )
            )
        )
        firebaseAnalyticsHelper.saveAboutYouEvent(userGender, userAge, userActivityLevel?.value)

        if (preferenceRepository.isUserLoggedIn) {
            userActivityLevel?.let { level ->
                showLoading()
                loginRepository.postActivityLevel(level)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        hideLoading()
                        startActivity(requireContext().TargetIntent(SetTargetMode.ONBOARDING))
                    }
                    .subscribe(
                        {
                            Timber.d("Post latest goals/motivations, success")
                        },
                        {
                            Timber.d("Post latest goals/motivations, error", it)
                        }
                    ).addTo(disposable)
            }
        } else {
            startActivity(requireContext().TargetIntent(SetTargetMode.ONBOARDING))
        }
    }

    private fun isUserAgeValid(): Boolean {
        val userSelectedAge = binding.ageEditText.getText()
        val isAgeLessThan16 = (userSelectedAge.toIntOrNull() ?: 0) < 16
        if (userSelectedAge.isNotEmpty() && isAgeLessThan16 && shouldShowAgeValidationDialog) {

            showYourSafetyIsImportantToUs()

            shouldShowAgeValidationDialog = false
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        binding.userDetailsTitleTV.announceHeader()
    }

    private fun showYourSafetyIsImportantToUs() {
        CommonBottomSheetDialog(
            title = R.string.your_safety_is_important_to_us,
            subtitle = R.string.under_the_age,
            primaryButton = DialogButtonModel(R.string.button_continue),
        ).show(requireActivity().supportFragmentManager, null)
    }
}
