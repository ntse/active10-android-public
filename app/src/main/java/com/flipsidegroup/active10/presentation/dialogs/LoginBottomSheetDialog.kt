package com.flipsidegroup.active10.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.view.size
import com.airbnb.paris.extensions.style
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.DialogLoginBinding
import com.flipsidegroup.active10.utils.BulletItemLayout
import com.flipsidegroup.active10.utils.LoginBottomSheetDialogType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setTextHtml

class LoginBottomSheetDialog(
    private val dialogType: LoginBottomSheetDialogType = LoginBottomSheetDialogType.YELLOW_BUTTON,
    @DrawableRes private val icon: Int? = null,
    private val title: String? = null,
    private val subtitle: String? = null,
    private val primaryButton: DialogCMSButtonModel? = null,
    private val secondaryButton: DialogCMSButtonModel? = null,
    private val onDismiss: () -> Unit = {},
    private val textAlignment: Int = View.TEXT_ALIGNMENT_CENTER
) : ExpandedBottomSheetDialogFragment() {

    private var binding: DialogLoginBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogLoginBinding.bind(view)

        icon?.let {
            binding.dialogLoginImage.isVisible = true
            binding.dialogLoginImage.setImageResource(it)
        }

        title?.let {
            binding.dialogLoginTitle.isVisible = true
            binding.dialogLoginTitle.text = it
            ViewCompat.setAccessibilityHeading(binding.dialogLoginTitle, true)
            binding.dialogLoginTitle.requestFocus()
        }

        subtitle?.let {
            if (it.contains("•")) {
                binding.dialogBulletListContainer.removeAllViews()
                binding.dialogBulletListContainer.isVisible = true
                val contentList = it.split("\r\n")
                contentList.forEach { content ->
                    val item = BulletItemLayout(requireContext())
                    item.binding.whiteBulletListItemContent.setTextHtml(
                        content.replace("• |•".toRegex(), "")
                    )
                    setContentDescriptionForBulletList(
                        binding.dialogBulletListContainer,
                        item.binding.whiteBulletListItemContent,
                        contentList.size,
                        content
                    )
                    binding.dialogBulletListContainer.addView(item)
                }
            } else {
                binding.dialogLoginSubtitle.isVisible = true
                binding.dialogLoginSubtitle.setTextHtml(it)
                binding.dialogLoginSubtitle.textAlignment = textAlignment
            }
        }

        primaryButton?.let { buttonModel ->
            setFirstButtonStyle(binding.dialogLoginPrimaryButton)
            binding.dialogLoginPrimaryButton.isVisible = true
            binding.dialogLoginPrimaryButton.text = buttonModel.text ?: ""
            binding.dialogLoginPrimaryButton.setOnClickListener {
                dismiss()
                buttonModel.onClick()
            }
        }

        secondaryButton?.let { buttonModel ->
            setSecondButtonStyle(binding.dialogLoginSecondaryButton)
            binding.dialogLoginSecondaryButton.isVisible = true
            binding.dialogLoginSecondaryButton.text = buttonModel.text ?: ""
            binding.dialogLoginSecondaryButton.setOnClickListener {
                dismiss()
                buttonModel.onClick()
            }
        }

    }

    private fun setContentDescriptionForBulletList(
        layout: LinearLayout, textView: TextView, listSize: Int, content: String
    ) {
        textView.contentDescription =
            if (layout.isEmpty()) "List start. List item 1 of ${listSize}. $content"
            else "List item ${layout.size + 1} of ${listSize}. $content"
    }

    private fun setFirstButtonStyle(view: TextView) {
        when (dialogType) {
            LoginBottomSheetDialogType.DISCONNECT -> {
                view.style(R.style.DisconnectButtonLarge)
            }

            LoginBottomSheetDialogType.YELLOW_BUTTON -> {
                view.style(R.style.PrimaryButtonLarge)
            }

            LoginBottomSheetDialogType.GREEN_BUTTON -> {
                view.style(R.style.TealButtonLarge)
            }
        }
    }

    private fun setSecondButtonStyle(button: AppCompatButton) {
        when (dialogType) {
            LoginBottomSheetDialogType.DISCONNECT -> {
                button.style(R.style.TealButtonLarge)
            }

            LoginBottomSheetDialogType.YELLOW_BUTTON -> {
                button.style(R.style.SecondaryButtonLarge)
            }

            LoginBottomSheetDialogType.GREEN_BUTTON -> {
                button.style(R.style.SecondaryButtonLarge)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

}