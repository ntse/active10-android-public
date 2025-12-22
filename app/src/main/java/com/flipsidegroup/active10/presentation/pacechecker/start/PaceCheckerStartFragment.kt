package com.flipsidegroup.active10.presentation.pacechecker.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.FragmentPaceCheckerStartBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.pacechecker.flat.PaceCheckerFlatDialog
import com.flipsidegroup.active10.utils.accessibilityFocusRequest
import com.phe.betterhealth.widgets.utils.isTouchExplorationEnabled
import javax.inject.Inject

class PaceCheckerStartFragment : BaseFragment<PaceCheckerStartView>(), PaceCheckerStartView {

    private var _binding: FragmentPaceCheckerStartBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: PaceCheckerStartPresenter

    override fun getPresenter(): LifecycleAwarePresenter<PaceCheckerStartView> = presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaceCheckerStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.paceCheckerStartTitle, true)

        binding.paceCheckerBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.paceCheckerButtonContinue.setOnClickListener { presenter.onContinueClicked() }

        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        if (requireContext().isTouchExplorationEnabled()) {
            binding.paceCheckerStartTitle.accessibilityFocusRequest()
        }
    }

    override fun showData(screen: ScreenContent) {
        binding.paceCheckerStartTitle.text = screen.title
        binding.paceCheckerStartDescription.text = screen.description
            .replace("\n", "<br>")
            .parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)
        binding.paceCheckerButtonContinue.text = screen.getPropertyValue("continue")
    }

    override fun navigateToPaceCheckerFlat(screen: ScreenContent?) {
        PaceCheckerFlatDialog.newInstance(screen).show(childFragmentManager, null)
    }
}
