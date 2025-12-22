package com.flipsidegroup.active10.presentation.classicuserdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.AppCompatRadioButton
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivityClassicUserDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.CommonBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.DialogButtonModel
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.USER_GENDER_FEMALE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.USER_GENDER_MALE
import com.flipsidegroup.active10.utils.DetailItemLayout
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setTextHtml
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.getClassicUserDetailsIntent(): Intent {
    return Intent(this, ClassicUserDetailsActivity::class.java)
}

class ClassicUserDetailsActivity : BaseSecureActivity<ClassicUserDetailsView>(), ClassicUserDetailsView {

    @Inject
    internal lateinit var presenter: ClassicUserDetailsPresenter

    private var screenContent: ScreenContent? = null
    private var shouldShowAgeValidationDialog = true
    private var userActivityLevel = ""

    override fun getPresenter(): LifecycleAwarePresenter<ClassicUserDetailsView> = presenter

    private var binding: ActivityClassicUserDetailsBinding by lifecycleAwareVariable()

    private val valueEt by lazy { binding.ageLayout.findViewById<EditText>(R.id.valueET) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityClassicUserDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.detailsToolbar.backTV.setOnClickListener { onBackPressed() }

        setUpAgeEditText()
        handleAgeInsert()
        setUpActivityRadioButtons()
        setUpdateButton()
        presenter.loadContent()
        presenter.loadData()
    }

    private fun setUpActivityRadioButtons() {
        with(binding.activityLevelLayout) {
            val userDetailsNoActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsNoActiveRB)
            val userDetailsModerateActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsModerateActiveRB)
            val userDetailsVeryActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsVeryActiveRB)

            userDetailsNoActiveRB.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    userActivityLevel = ActivityLevelEnum.INACTIVE.value
                    userDetailsModerateActiveRB.isChecked = false
                    userDetailsVeryActiveRB.isChecked = false
                }
            }

            userDetailsModerateActiveRB.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    userActivityLevel = ActivityLevelEnum.MODERATELY_ACTIVE.value
                    userDetailsNoActiveRB.isChecked = false
                    userDetailsVeryActiveRB.isChecked = false
                }
            }

            userDetailsVeryActiveRB.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    userActivityLevel = ActivityLevelEnum.ACTIVE.value
                    userDetailsNoActiveRB.isChecked = false
                    userDetailsModerateActiveRB.isChecked = false
                }
            }
        }
    }

    private fun setUpdateButton() {
        binding.updateBTN.setOnClickListener {
            presenter.saveData(
                ClassicUser(
                    gender = checkSelectedGender(),
                    age = valueEt.text.toString().toIntOrNull(),
                    activityLevel = userActivityLevel
                )
            )
        }
    }

    override fun showContent(content: ScreenContent) {
        binding.userDetailsTitleTV.text = content.title
        binding.userDetailsDescriptionTV.setTextHtml(content.description)
        screenContent = content

        binding.genderTitleTV.visibility = View.VISIBLE
        binding.genderRadioGroup.visibility = View.VISIBLE
        binding.activityLevelLayout.visibility = View.VISIBLE
        binding.ageLayout.findViewById<EditText>(R.id.valueET).inputType = InputType.TYPE_CLASS_NUMBER

        contentItemBuilder(binding.ageLayout, "details_age")
    }

    override fun showData(classicUser: ClassicUser?) {
        classicUser?.gender?.let {
            when (it) {
                USER_GENDER_MALE -> binding.maleRB.isChecked = true
                USER_GENDER_FEMALE -> binding.femaleRB.isChecked = true
                else -> binding.otherRB.isChecked = true
            }
        }
        classicUser?.age?.let { binding.ageLayout.setValue(it.toString()) }
        classicUser?.activityLevel?.let {
            with(binding.activityLevelLayout) {
                val userDetailsNoActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsNoActiveRB)
                val userDetailsModerateActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsModerateActiveRB)
                val userDetailsVeryActiveRB = findViewById<AppCompatRadioButton>(R.id.userDetailsVeryActiveRB)
                when (it) {
                    ActivityLevelEnum.INACTIVE.value -> userDetailsNoActiveRB.isChecked = true
                    ActivityLevelEnum.MODERATELY_ACTIVE.value -> userDetailsModerateActiveRB.isChecked = true
                    ActivityLevelEnum.ACTIVE.value -> userDetailsVeryActiveRB.isChecked = true
                }
            }
        }
    }

    private fun checkSelectedGender(): String {
        return when {
            binding.maleRB.isChecked -> USER_GENDER_MALE
            binding.femaleRB.isChecked -> USER_GENDER_FEMALE
            binding.otherRB.isChecked -> ""
            else -> ""
        }
    }

    private fun setUpAgeEditText() {
        with(binding.ageLayout) {
            val valueET = findViewById<EditText>(R.id.valueET)
            setMaxCharacters(2)
            valueET.isFocusableInTouchMode = true
            valueET.isFocusable = true
            valueET.inputType = InputType.TYPE_CLASS_NUMBER
            valueET.maxLines = 1
            valueET.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isUserAgeValid()
                }
                false
            }
        }
    }

    private fun isUserAgeValid(): Boolean {
        val userSelectedAge = valueEt.text.toString()
        if (userSelectedAge.isNotEmpty() && (userSelectedAge.toIntOrNull()
                ?: 0) <= 16 && shouldShowAgeValidationDialog
        ) {
            showYourSafetyIsImportantToUs()
            shouldShowAgeValidationDialog = false
            return false
        }
        return true
    }

    private fun handleAgeInsert() {
        valueEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //no operation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shouldShowAgeValidationDialog = true
            }

            override fun afterTextChanged(s: Editable?) {
                //no operation needed
            }
        })
    }

    private fun contentItemBuilder(view: DetailItemLayout, key: String) {
        val list = screenContent?.getPropertyValue(key)?.split("\r\n")
        list?.let {
            if (list.size == 2) {
                view.setTitle(list[0])
                view.setLabel(list[1])
                view.visibility = View.VISIBLE
            }
        }
    }

    override fun onUserDetailsSaved() {
        finish()
    }

    private fun showYourSafetyIsImportantToUs() {
        CommonBottomSheetDialog(
            title = R.string.your_safety_is_important_to_us,
            subtitle = R.string.under_the_age,
            primaryButton = DialogButtonModel(R.string.button_continue),
        ).show(supportFragmentManager, null)
    }
}