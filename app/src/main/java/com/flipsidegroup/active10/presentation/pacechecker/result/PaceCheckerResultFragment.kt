package com.flipsidegroup.active10.presentation.pacechecker.result

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.FragmentPaceCheckerResultBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.pacechecker.explore.PaceCheckerExploreDialog
import com.flipsidegroup.active10.presentation.pacechecker.useful.PaceCheckerUsefulDialog
import com.phe.betterhealth.widgets.utils.textColor
import javax.inject.Inject

class PaceCheckerResultFragment : BaseFragment<PaceCheckerResultView>(), PaceCheckerResultView {

    @Inject
    lateinit var presenter: PaceCheckerResultPresenter

    private var _binding: FragmentPaceCheckerResultBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(steps: Float) = PaceCheckerResultFragment()
            .apply {
                arguments = Bundle().apply {
                    putFloat("steps", steps)
                }
            }
    }

    override fun getPresenter(): LifecycleAwarePresenter<PaceCheckerResultView> = presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaceCheckerResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.paceCheckerTitle, true)

        presenter.loadData(arguments?.getFloat("steps"))

        binding.paceCheckerButtonClose.setOnClickListener {
            presenter.onCloseClicked()
        }

        binding.paceCheckerButtonAgain.setOnClickListener {
            presenter.onAgainClicked()
        }
    }

    override fun showData(content: PaceCheckerResultContent) {
        with(binding) {
            paceCheckerTitle.text = content.title
            paceCheckerDescription.text = content.description
            paceCheckerAverageValue.text = content.cadence
            paceCheckerButtonAgain.text = content.continueButtonTitle
            paceCheckerAverageImage.setImageResource(content.averageImgRes)
            paceCheckerCaption.text = content.captionText
            paceCheckerCaptionIcon.setImageResource(content.captionIconRes)
            paceCheckerAverageTitle.isVisible = !content.isError
            paceCheckerAverageValueTitle.isVisible = !content.isError
            paceCheckerAverageValue.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                if (content.isError) 56f else 104f
            )
            paceCheckerAverageValue.textColor =
                if (content.isError) R.color.greyish_brown else R.color.bhTextPrimary
            setAccessibilityFocus(content)
        }
    }

    private fun setAccessibilityFocus(content: PaceCheckerResultContent) {
        with(binding) {
            ViewCompat.setAccessibilityDelegate(paceCheckerAverageContainer, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    if (content.isError) {
                        info.contentDescription = info.text
                    } else {
                        info.contentDescription =
                            "${paceCheckerAverageValue.text} steps per minute"
                    }
                }
            })
            if (content.isError)
                paceCheckerTitle.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            else
                paceCheckerAverageContainer.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    }

    override fun showUsefulDialog(screenContent: ScreenContent?) {
        PaceCheckerUsefulDialog.newInstance(screenContent).show(childFragmentManager, null)
    }

    override fun showExploreDialog(screenContent: ScreenContent?) {
        PaceCheckerExploreDialog.newInstance(screenContent).show(childFragmentManager, null)
    }

    override fun closePaceChecker() {
        requireActivity().finish()
    }

    override fun popViewToTimer() {
        parentFragmentManager.popBackStack("pace_checker_timer", 0)
    }

    fun onUsefulDialogClickedNo() {
        presenter.onUsefulDialogClickedNo()
    }
}
