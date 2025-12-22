package com.flipsidegroup.active10.presentation.authentication.setPin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivitySetPinBinding
import com.flipsidegroup.active10.presentation.authentication.unlockSetup.unlockSetupSuccessIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.termsAndConditionsIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.closeKeyboard
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.setPinIntent(flowType: FlowType): Intent {
    return Intent(this, SetPinActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class SetPinActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivitySetPinBinding by lifecycleAwareVariable()

    private var attemptsCount = 0
    private val firstPin = MutableLiveData<String>()
    private val secondPin = MutableLiveData<String>()
    private val stepType = MutableLiveData(ContentType.SET_PIN)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivitySetPinBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        savedInstanceState?.let { stepType.value = it.serializable<ContentType>(STEP_TYPE) }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        val flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        setupPinTypeContent(flowType)
        setupPinInputs()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STEP_TYPE, stepType.value)
    }

    private fun setupPinTypeContent(flowType: FlowType) {
        with(binding) {
            val pinDigits = listOf(pinDigit4, pinDigit3, pinDigit2, pinDigit1)

            stepType.observe(this@SetPinActivity) { type ->
                pinDigits.forEach {
                    it.isEnabled = type in arrayOf(ContentType.SET_PIN, ContentType.CONFIRM_PIN)
                }
                title.text = getString(type.title)
                description.text = getString(type.description)
                continueBtn.text = getString(type.buttonText)
                continueBtn.setOnClickListener {
                    setupPinSubTypeContent(
                        pinDigits = pinDigits,
                        subType = type,
                        flowType = flowType
                    )
                }
                when (type) {
                    ContentType.SET_PIN -> {
                        errorMessage.isInvisible = true
                        continueBtn.isEnabled = false
                    }

                    ContentType.CONFIRM_PIN -> continueBtn.isEnabled = false
                    ContentType.TOO_MANY_ATTEMPTS -> {
                        binding.toolbar.backTV.isVisible = false
                        continueBtn.isEnabled = true
                        onBackPressedDispatcher.addCallback(this@SetPinActivity) {}
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupPinSubTypeContent(
        pinDigits: List<EditText>,
        subType: ContentType,
        flowType: FlowType
    ) {
        when (subType) {
            ContentType.SET_PIN -> {
                if (isPinFieldsValidated()) {
                    firstPin.value = pinDigits.joinToString { it.text.toString() }
                    pinDigits.forEach { it.text.clear() }
                    stepType.value = ContentType.CONFIRM_PIN
                }
            }

            ContentType.CONFIRM_PIN -> {
                if (isPinFieldsValidated()) {
                    secondPin.value = pinDigits.joinToString { it.text.toString() }
                    if (secondPin.value == firstPin.value) {
                        preferenceRepository.authPinCode = secondPin.value
                        preferenceRepository.isBiometricAllowed = false
                        startActivity(unlockSetupSuccessIntent(flowType))
                    } else if (attemptsCount < 1) {
                        binding.errorMessage.isInvisible = false
                        attemptsCount += 1
                        stepType.value = ContentType.MISMATCH_PIN
                    } else {
                        pinDigits.forEach { it.text.clear() }
                        stepType.value = ContentType.TOO_MANY_ATTEMPTS
                    }
                }
            }

            ContentType.MISMATCH_PIN -> {
                firstPin.value = ""
                secondPin.value = ""
                pinDigits.forEach { it.text.clear() }
                stepType.value = ContentType.SET_PIN
            }

            ContentType.TOO_MANY_ATTEMPTS -> {
                if (preferenceRepository.isOnboardingCompleted) {
                    startActivity(
                        Intent(this, HomeActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    )
                } else {
                    startActivity(termsAndConditionsIntent(flowType = flowType))
                }
            }
        }
    }

    private fun setupPinInputs() {
        with(binding) {

            pinDigit1.addTextChangedListener(createTextWatcher(pinDigit2))
            pinDigit2.addTextChangedListener(createTextWatcher(pinDigit3))
            pinDigit3.addTextChangedListener(createTextWatcher(pinDigit4))
            pinDigit4.addTextChangedListener(createTextWatcher(pinDigit4, continueBtn))

            pinDigit2.setOnKeyListener(createBackspaceListener(pinDigit1, pinDigit2))
            pinDigit3.setOnKeyListener(createBackspaceListener(pinDigit2, pinDigit3))
            pinDigit4.setOnKeyListener(createBackspaceListener(pinDigit3, pinDigit4))

            val digitFilter = InputFilter { source, _, _, _, _, _ ->
                val regex = Regex("[0-9]+")
                if (source.toString().matches(regex)) {
                    source
                } else {
                    ""
                }
            }

            pinDigit1.filters = arrayOf(digitFilter, InputFilter.LengthFilter(1))
            pinDigit2.filters = arrayOf(digitFilter, InputFilter.LengthFilter(1))
            pinDigit3.filters = arrayOf(digitFilter, InputFilter.LengthFilter(1))
            pinDigit4.filters = arrayOf(digitFilter, InputFilter.LengthFilter(1))

            pinDigit1.requestFocus()
        }
    }

    private fun createTextWatcher(
        field: EditText? = null,
        continueBtn: AppCompatButton? = null,
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.continueBtn.isEnabled = isPinFieldsValidated()

                if (s?.length == 1 && continueBtn == null) {
                    field?.requestFocus()
                } else if (s?.length == 1 && continueBtn != null) {
                    closeKeyboard()
                    field?.clearFocus()
                }
            }
        }
    }

    private fun createBackspaceListener(
        previousField: EditText,
        currentField: EditText
    ): View.OnKeyListener {
        return View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                previousField.text.isNotEmpty()
            ) {
                if (currentField.text.isNotEmpty()) {
                    currentField.text.clear()
                } else {
                    previousField.text.clear()
                    previousField.requestFocus()
                }
                return@OnKeyListener true
            }
            false
        }
    }

    private fun isPinFieldsValidated(): Boolean = with(binding) {
        return validatePinField(pinDigit4.text.toString()) &&
                validatePinField(pinDigit3.text.toString()) &&
                validatePinField(pinDigit2.text.toString()) &&
                validatePinField(pinDigit1.text.toString())
    }

    private fun validatePinField(pinField: String): Boolean =
        pinField.toIntOrNull() != null && pinField.length == 1

    private enum class ContentType(
        @StringRes val title: Int,
        @StringRes val description: Int,
        @StringRes val buttonText: Int
    ) {
        SET_PIN(
            title = R.string.set_pin_title,
            description = R.string.set_pin_description,
            buttonText = R.string.button_continue
        ),
        CONFIRM_PIN(
            title = R.string.set_pin_confirm_title,
            description = R.string.set_pin_confirm_description,
            buttonText = R.string.button_continue
        ),
        MISMATCH_PIN(
            title = R.string.set_pin_mismatch_title,
            description = R.string.set_pin_mismatch_description,
            buttonText = R.string.try_again_button
        ),
        TOO_MANY_ATTEMPTS(
            title = R.string.set_pin_too_many_attempts_title,
            description = R.string.set_pin_too_many_attempts_description,
            buttonText = R.string.button_continue
        )
    }

    companion object {
        private const val STEP_TYPE = "step_type"
    }
}