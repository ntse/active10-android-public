package com.flipsidegroup.active10.presentation.pacechecker.pause

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogPaceCheckerPauseBinding
import com.flipsidegroup.active10.presentation.pacechecker.timer.PaceCheckerTimerFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phe.betterhealth.widgets.common.setHtml
import dagger.android.support.AndroidSupportInjection

class PaceCheckerPauseDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerPauseBinding? = null
    private val binding get() = _binding!!

    companion object {

        fun newInstance(screenContent: ScreenContent?) =
            PaceCheckerPauseDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("screenContent", screenContent)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaceCheckerPauseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")

        if (screenContent != null) {
            binding.header.text = screenContent.title
            binding.description.setHtml(screenContent.description)
            binding.paceCheckerButtonContinue.text = screenContent.getPropertyValue("continue")
            binding.paceCheckerButtonSkip.text = screenContent.getPropertyValue("exit")
        }

        binding.paceCheckerButtonContinue.setOnClickListener {
            (parentFragment as PaceCheckerTimerFragment).resumeTimer()
            dismiss()
        }
        binding.paceCheckerButtonSkip.setOnClickListener { requireActivity().finish() }
    }

    override fun onCancel(dialog: DialogInterface) {
        (parentFragment as PaceCheckerTimerFragment).resumeTimer()
    }
}