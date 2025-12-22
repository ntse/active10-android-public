package com.flipsidegroup.active10.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.databinding.DialogTermsAndConditionsBinding
import com.flipsidegroup.active10.presentation.legals.activity.LegalsActivity
import com.flipsidegroup.active10.presentation.onboarding.fragments.PRIVACY_POLICY
import com.flipsidegroup.active10.presentation.onboarding.fragments.TERMS_AND_CONDITIONS
import com.flipsidegroup.active10.utils.lifecycleAwareVariable

class TermsConditionsDialog(
    private val rules: GlobalRules,
    val onSubmit: () -> Unit
) : ExpandedBottomSheetDialogFragment() {

    private var binding: DialogTermsAndConditionsBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_terms_and_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogTermsAndConditionsBinding.bind(view)

        ViewCompat.setAccessibilityHeading(binding.dialogTermsTitle, true)
        binding.dialogTermsTitle.requestFocus()

        binding.dialogTermsSubtitle.text = rules.termsAndConditions?.title
        binding.dialogTermsSubmitButton.text = rules.termsAndConditions?.button
        binding.dialogTermsCheckbox.text = rules.termsAndConditions?.agree

        binding.dialogTermsPolicyTv.setOnClickListener {
            startActivity(requireContext().LegalsActivity(PRIVACY_POLICY))
        }

        binding.dialogTermsTermsTv.setOnClickListener {
            startActivity(requireContext().LegalsActivity(TERMS_AND_CONDITIONS))
        }

        binding.dialogTermsSubmitButton.setOnClickListener {
            if (binding.dialogTermsCheckbox.isChecked) {
                onSubmit()
                dismiss()
            } else {
                binding.dialogTermsNeedTooAgreeError.isVisible = true
                binding.dialogTermsNeedTooAgreeError.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }
    }
}
