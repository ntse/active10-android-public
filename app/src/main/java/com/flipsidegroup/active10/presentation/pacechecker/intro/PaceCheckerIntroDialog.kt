package com.flipsidegroup.active10.presentation.pacechecker.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.databinding.DialogPaceCheckerIntroBinding
import com.flipsidegroup.active10.presentation.pacechecker.getPaceCheckerIntent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PaceCheckerIntroDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerIntroBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    companion object {

        fun newInstance(screenContent: ScreenContent?) =
            PaceCheckerIntroDialog().apply {
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
        _binding = DialogPaceCheckerIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")

        if (screenContent != null) {
            binding.header.text = screenContent.title
            binding.description.text = screenContent.description
            binding.paceCheckerButtonContinue.text =
                screenContent.getPropertyValue("try_pace_checker")
            binding.paceCheckerButtonSkip.text = screenContent.getPropertyValue("try_later")
        }

        binding.paceCheckerButtonContinue.setOnClickListener {
            startActivity(requireContext().getPaceCheckerIntent())
            dismiss()
        }

        binding.paceCheckerButtonSkip.setOnClickListener { dismiss() }
    }
}