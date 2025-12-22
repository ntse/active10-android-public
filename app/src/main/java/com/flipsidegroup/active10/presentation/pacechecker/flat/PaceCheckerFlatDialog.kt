package com.flipsidegroup.active10.presentation.pacechecker.flat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogPaceCheckerFlatBinding
import com.flipsidegroup.active10.presentation.pacechecker.PaceCheckerActivity
import com.flipsidegroup.active10.presentation.pacechecker.timer.PaceCheckerTimerFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phe.betterhealth.widgets.common.setHtml

class PaceCheckerFlatDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerFlatBinding? = null
    private val binding get() = _binding!!

    companion object {

        fun newInstance(screenContent: ScreenContent?) = PaceCheckerFlatDialog().apply {
            arguments = Bundle().apply {
                putParcelable("screenContent", screenContent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaceCheckerFlatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")

        if (screenContent != null) {
            binding.header.text = screenContent.title
            binding.description.setHtml(screenContent.description)
            binding.paceCheckerButtonContinue.text = screenContent.getPropertyValue("ok")
        }

        binding.paceCheckerButtonContinue.setOnClickListener {
            dismiss()
            (requireActivity() as PaceCheckerActivity)
                .navigateToFragments(PaceCheckerTimerFragment.newInstance(), "pace_checker_timer")
        }
    }
}
