package com.flipsidegroup.active10.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogWalkingPlanBottomSheetBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset


class WalkingPlanBottomSheetDialog(
    private val content: ScreenContent,
    private val onFirstButton: () -> Unit,
    private val onSecondButton: () -> Unit = {}
) : DialogFragment() {

    private var binding: DialogWalkingPlanBottomSheetBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogWithDimmedBackground)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.attributes?.windowAnimations = R.style.Widget_DialogAnimation
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return inflater.inflate(R.layout.dialog_walking_plan_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWalkingPlanBottomSheetBinding.bind(view)
        binding.root.setBottomPaddingToBottomInset()

        isCancelable = false

        showContent(content)
    }

    private fun showContent(content: ScreenContent) {
        with(binding) {
            screen = content
            closeButton.isVisible = !content.secondButtonTitle.isNullOrEmpty()

            continueButton.setOnClickListener {
                onFirstButton()
                dismiss()
            }
            closeButton.setOnClickListener {
                onSecondButton()
                dismiss()
            }
        }
    }
}