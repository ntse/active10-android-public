package com.flipsidegroup.active10.presentation.root

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import com.flipsidegroup.active10.databinding.DialogRootDetectionAlertBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.system.exitProcess

class RootDetectionAlertDialog : BottomSheetDialogFragment() {

    var binding: DialogRootDetectionAlertBinding by lifecycleAwareVariable()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = DialogRootDetectionAlertBinding.inflate(inflater)

        // Set up the bottom sheet dialog
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isHideable = false
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        setOnButtonClickListener()

        return dialog
    }
    private fun setOnButtonClickListener() {
        binding.closeButton.setOnClickListener {
            requireActivity().finish()
            exitProcess(0)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().finish()
        exitProcess(0)
    }

}