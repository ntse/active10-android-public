package com.flipsidegroup.active10.presentation.pacechecker.exit

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogPaceCheckerExitBinding
import com.flipsidegroup.active10.presentation.pacechecker.timer.PaceCheckerTimerFragment
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phe.betterhealth.widgets.common.setHtml
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PaceCheckerExitDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerExitBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    companion object {

        fun newInstance(screenContent: ScreenContent?, seconds: Int) =
            PaceCheckerExitDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("screenContent", screenContent)
                    putInt("seconds", seconds)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaceCheckerExitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")
        val seconds = requireArguments().getInt("seconds")

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
        binding.paceCheckerButtonSkip.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerCanceled(seconds)
            requireActivity().finish()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        (parentFragment as PaceCheckerTimerFragment).resumeTimer()
    }
}
