package com.flipsidegroup.active10.presentation.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.DialogUpdateAppBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable

class UpdateAppDialog(
    private val onUpdateConfirmed: (Boolean) -> Unit
) : ExpandedBottomSheetDialogFragment() {

    companion object {
        private const val PROD_APPLICATION_ID = "uk.ac.shef.oak.pheactiveten"
    }

    private var binding: DialogUpdateAppBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_update_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogUpdateAppBinding.bind(view)

        ViewCompat.setAccessibilityHeading(binding.dialogUpdateTitle, true)
        binding.dialogUpdateTitle.requestFocus()

        binding.dialogUpdateUpdateButton.setOnClickListener {
            val appId = PROD_APPLICATION_ID

            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appId")
                )
            )
            onUpdateConfirmed(true)
            dismiss()
        }

        binding.dialogUpdateNotNowButton.setOnClickListener {
            onUpdateConfirmed(false)
            dismiss()
        }
    }

}