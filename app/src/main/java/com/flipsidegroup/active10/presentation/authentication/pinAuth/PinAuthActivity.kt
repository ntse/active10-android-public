package com.flipsidegroup.active10.presentation.authentication.pinAuth

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
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityPinUnlockBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.usecases.RedirectOnAuthFailureUseCase
import com.flipsidegroup.active10.utils.closeKeyboard
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.pinAuthIntent(): Intent {
    return Intent(this, PinAuthActivity::class.java).apply {
        setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    }
}

class PinAuthActivity : BasePublicActivity<PinAuthView>(), PinAuthView {

    @Inject
    internal lateinit var presenter: PinAuthPresenter

    @Inject
    internal lateinit var redirectOnAuthFailureUseCase: RedirectOnAuthFailureUseCase

    override fun getPresenter(): LifecycleAwarePresenter<PinAuthView> = presenter

    private var binding: ActivityPinUnlockBinding by lifecycleAwareVariable()

    private var attemptsCount = 0
    private val firstPin = MutableLiveData<String>()
    private val stepType = MutableLiveData(ContentType.SET_PIN)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityPinUnlockBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        savedInstanceState?.let { stepType.value = it.serializable<ContentType>(STEP_TYPE) }

        onBackPressedDispatcher.addCallback(this) {}

        setupPinTypeContent()
        setupPinInputs()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STEP_TYPE, stepType.value)
    }

    private fun setupPinTypeContent() {
        with(binding) {
            val pinDigits = listOf(pinDigit4, pinDigit3, pinDigit2, pinDigit1)

            stepType.observe(this@PinAuthActivity) { type ->
                pinDigits.forEach {
                    it.isEnabled = type in arrayOf(ContentType.SET_PIN)
                }
                title.text = getString(type.title)
                description.text = getString(type.description)
                continueBtn.text = getString(type.buttonText)
                continueBtn.setOnClickListener {
                    setupPinSubTypeContent(
                        pinDigits = pinDigits,
                        type = type
                    )
                }
                when (type) {
                    ContentType.SET_PIN -> continueBtn.isEnabled = false
                    ContentType.TOO_MANY_ATTEMPTS -> continueBtn.isEnabled = true
                    else -> Unit
                }
            }
        }
    }

    private fun setupPinSubTypeContent(
        pinDigits: List<EditText>,
        type: ContentType,
    ) {
        when (type) {
            ContentType.SET_PIN -> {
                if (isPinFieldsValidated()) {
                    firstPin.value = pinDigits.joinToString { it.text.toString() }
                    pinDigits.forEach { it.text.clear() }
                    if (firstPin.value == preferenceRepository.authPinCode) {
                        checkAuthManager.invokeAuthScreen.value = false
                        finish()
                    } else if (attemptsCount < 1) {
                        binding.errorMessage.isInvisible = false
                        attemptsCount += 1
                    } else {
                        presenter.logout()
                        stepType.value = ContentType.TOO_MANY_ATTEMPTS
                    }
                }
            }

            ContentType.TOO_MANY_ATTEMPTS -> {
                redirectOnAuthFailureUseCase(context = this)
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
                binding.errorMessage.isInvisible = true

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
            title = R.string.returning_user_title,
            description = R.string.returning_user_description,
            buttonText = R.string.button_continue
        ),
        TOO_MANY_ATTEMPTS(
            title = R.string.returning_user_too_many_attempts_title,
            description = R.string.returning_user_too_many_attempts_description,
            buttonText = R.string.button_continue
        )
    }

    companion object {
        private const val STEP_TYPE = "step_type"
    }
}