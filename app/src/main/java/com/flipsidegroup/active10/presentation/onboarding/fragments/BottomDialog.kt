package com.flipsidegroup.active10.presentation.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.LayoutBottomsheetSelectGenderBinding
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialog : BottomSheetDialogFragment() {

    private var binding: LayoutBottomsheetSelectGenderBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottomsheet_select_gender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutBottomsheetSelectGenderBinding.bind(view)

        binding.continueBottomDialog.setOnClickListener {
            dismiss()
        }
    }
}