package com.flipsidegroup.active10.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.DialogCommonBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable

class CommonBottomSheetDialog(
    @DrawableRes private val icon: Int? = null,
    @StringRes private val title: Int? = null,
    @StringRes private val subtitle: Int? = null,
    private val primaryButton: DialogButtonModel? = null,
    private val secondaryButton: DialogButtonModel? = null,
    private val onDismiss: () -> Unit = {}
): ExpandedBottomSheetDialogFragment() {

    private var binding: DialogCommonBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_common, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogCommonBinding.bind(view)

        icon?.let {
            binding.dialogCommonImage.isVisible = true
            binding.dialogCommonImage.setImageResource(it)
        }

        title?.let {
            binding.dialogCommonTitle.isVisible = true
            binding.dialogCommonTitle.setText(it)
            ViewCompat.setAccessibilityHeading(binding.dialogCommonTitle, true)
            binding.dialogCommonTitle.requestFocus()
        }

        subtitle?.let {
            binding.dialogCommonSubtitle.isVisible = true
            binding.dialogCommonSubtitle.setText(it)
        }

        primaryButton?.let { buttonModel ->
            binding.dialogCommonPrimaryButton.isVisible = true
            binding.dialogCommonPrimaryButton.setText(buttonModel.text)
            binding.dialogCommonPrimaryButton.setOnClickListener {
                buttonModel.onClick()
                dismiss()
            }
        }

        secondaryButton?.let { buttonModel ->
            binding.dialogCommonSecondaryButton.isVisible = true
            binding.dialogCommonSecondaryButton.setText(buttonModel.text)
            binding.dialogCommonSecondaryButton.setOnClickListener {
                buttonModel.onClick()
                dismiss()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }
}
