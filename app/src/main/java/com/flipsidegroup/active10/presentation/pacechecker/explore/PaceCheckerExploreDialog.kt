package com.flipsidegroup.active10.presentation.pacechecker.explore

import android.os.Bundle
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.DialogPaceCheckerExploreBinding
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.DISCOVER_SCREEN_POSITION
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PaceCheckerExploreDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPaceCheckerExploreBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    companion object {

        fun newInstance(screenContent: ScreenContent?) = PaceCheckerExploreDialog().apply {
            arguments = Bundle().apply {
                putParcelable("screenContent", screenContent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            com.flipsidegroup.active10.R.style.TransparentBottomSheetDialogTheme
        );
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = DialogPaceCheckerExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.header, true)

        val screenContent = requireArguments().getParcelable<ScreenContent>("screenContent")

        if (screenContent != null) {
            binding.header.text = screenContent.title
            binding.description.text = screenContent.description
            binding.paceCheckerButtonContinue.text = screenContent.getPropertyValue("take_me")
            binding.paceCheckerButtonSkip.text = screenContent.getPropertyValue("maybe_later")
        }

        binding.paceCheckerButtonContinue.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerMoreToExplore("yes")
            startActivity(
                HomeActivity.getHomeIntent(
                    context = requireContext(),
                    screenPosition = DISCOVER_SCREEN_POSITION
                )
            )
            requireActivity().finish()
        }
        binding.paceCheckerButtonSkip.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerMoreToExplore("no")
            requireActivity().finish()
        }
    }
}
