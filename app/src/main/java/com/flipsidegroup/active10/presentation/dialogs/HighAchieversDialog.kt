package com.flipsidegroup.active10.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.DialogHighAchieversBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable

class HighAchieversDialog : ExpandedBottomSheetDialogFragment() {

    var onIncreaseNowClicked: () -> Unit = {}
    var onMaybeLaterClicked: () -> Unit = {}
    var onDoNotAskMeAgainCliched: (() -> Unit)? = null
    var onDismissCallback: () -> Unit = {}

    private var binding: DialogHighAchieversBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_high_achievers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogHighAchieversBinding.bind(view)

        ViewCompat.setAccessibilityHeading(binding.dialogHighAchieversTitle, true)
        binding.dialogHighAchieversTitle.requestFocus()

        binding.dialogHighAchieversIncreaseNowButton.setOnClickListener {
            onIncreaseNowClicked()
            onDismissCallback()
            dismiss()
        }

        binding.dialogHighAchieversMaybeLaterButton.setOnClickListener {
            onMaybeLaterClicked()
            onDismissCallback()
            dismiss()
        }

        onDoNotAskMeAgainCliched?.let { doNotAskMeAgainCallback ->
            binding.dialogHighAchieversDontAskButton.isVisible = true
            binding.dialogHighAchieversDontAskButton.setOnClickListener {
                doNotAskMeAgainCallback()
                onDismissCallback()
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        onMaybeLaterClicked()
        onDismissCallback()
    }
}
