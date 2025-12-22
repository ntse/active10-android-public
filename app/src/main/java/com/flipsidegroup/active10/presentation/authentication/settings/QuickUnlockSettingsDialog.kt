package com.flipsidegroup.active10.presentation.authentication.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.flipsidegroup.active10.databinding.DialogQuickUnlockSettingsBinding
import com.flipsidegroup.active10.presentation.common.dialogfragments.BaseDialogFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable

class QuickUnlockSettingsDialog(
    private val onPrimaryBtnClick: () -> Unit,
    private val onSecondaryBtnClick: () -> Unit,
): BaseDialogFragment<BaseView>() {

    private var binding: DialogQuickUnlockSettingsBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogQuickUnlockSettingsBinding.inflate(inflater, container, false)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.primaryBtn.setOnClickListener {
            onPrimaryBtnClick()
            dismiss()
        }

        binding.secondaryBtn.setOnClickListener {
            onSecondaryBtnClick()
            dismiss()
        }

        return binding.root
    }

}