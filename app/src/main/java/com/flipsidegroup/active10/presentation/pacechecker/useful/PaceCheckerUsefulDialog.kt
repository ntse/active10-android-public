package com.flipsidegroup.active10.presentation.pacechecker.useful

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogPaceCheckerUsefulBinding
import com.flipsidegroup.active10.presentation.pacechecker.result.PaceCheckerResultFragment
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phe.betterhealth.widgets.common.setHtml
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PaceCheckerUsefulDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerUsefulBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    companion object {

        fun newInstance(screenContent: ScreenContent?) = PaceCheckerUsefulDialog().apply {
            arguments = Bundle().apply {
                putParcelable("screenContent", screenContent)
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
        _binding = DialogPaceCheckerUsefulBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")

        if (screenContent != null) {
            binding.header.text = screenContent.title
            binding.description.setHtml(screenContent.description)
            binding.paceCheckerButtonContinue.text = screenContent.getPropertyValue("yes")
            binding.paceCheckerButtonSkip.text = screenContent.getPropertyValue("no")
        }

        binding.paceCheckerButtonContinue.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerWasUseful("yes")
            requireActivity().finish()
        }

        binding.paceCheckerButtonSkip.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerWasUseful("no")
            (parentFragment as PaceCheckerResultFragment).onUsefulDialogClickedNo()
        }
    }
}
