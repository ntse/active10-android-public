package com.flipsidegroup.active10.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.databinding.DialogMissingDataBinding
import com.flipsidegroup.active10.databinding.DialogNhsLogBackInBinding
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phe.betterhealth.widgets.common.setHtml
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NhsLogBackInDialog: ExpandedBottomSheetDialogFragment() {

    private var binding: DialogNhsLogBackInBinding by lifecycleAwareVariable()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var screenRepository: ScreenRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.isDraggable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_nhs_log_back_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidSupportInjection.inject(this)
        binding = DialogNhsLogBackInBinding.bind(view)

        screenRepository.getScreenContentBySlug("nhs_log_back_in")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { screen ->
                binding.dialogTitle.text = screen.title
                binding.dialogSubtitle.text = screen.description.replace("\r\n", "\n\n")
                binding.dialogSubmitButton.text = screen.firstButtonTitle
                binding.dialogCancelButton.text = screen.secondButtonTitle
            }.addTo(disposables)

        binding.dialogCancelButton.setOnClickListener {
            dismiss()
        }
        binding.dialogSubmitButton.setOnClickListener {
            startActivity(requireContext().signInIntent(flowType = FlowType.SETTINGS))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }
}
