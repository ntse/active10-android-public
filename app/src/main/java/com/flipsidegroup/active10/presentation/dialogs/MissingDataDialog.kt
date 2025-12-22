package com.flipsidegroup.active10.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.databinding.DialogMissingDataBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading

class MissingDataDialog(
    val globalRules: GlobalRules
): ExpandedBottomSheetDialogFragment() {

    private var binding: DialogMissingDataBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_missing_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogMissingDataBinding.bind(view)

        binding.dialogMissingDataTitle.text = globalRules.missingData?.title
        binding.dialogMissingDataTitle.setHeading()
        binding.dialogMissingDataTitle.requestFocus()

        binding.dialogMissingDataSubmitButton.setOnClickListener {
            dismiss()
        }
        binding.dialogMissingDataDismissButton.setOnClickListener {
            dismiss()
        }
    }
}
