package com.flipsidegroup.active10.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.DialogIntroducingWidgetBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import timber.log.Timber

class IntroducingWidgetDialog : ExpandedBottomSheetDialogFragment() {

    lateinit var onContinue: () -> Unit

    private var binding: DialogIntroducingWidgetBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_introducing_widget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogIntroducingWidgetBinding.bind(view)

        ViewCompat.setAccessibilityHeading(binding.dialogIntroducingTitle, true)

        binding.dialogIntroducingContinueButton.setOnClickListener {
            dismiss()
            try {
                onContinue()
            } catch (err: Throwable) {
                Timber.e(err)
            }
        }
    }
}
