package com.flipsidegroup.active10.presentation.onboarding.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.MigratingUserEnum
import com.flipsidegroup.active10.data.NewUserEnum
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentIntroBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.Constants.IN_IS_NEW_USER
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import com.flipsidegroup.active10.utils.setTextWithUnderlineSpan
import javax.inject.Inject

private const val INTRO_POSITION_KEY = "intro_position_key"

class IntroFragment : BaseFragment<BaseView>() {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    private var introPos: Int? = null

    private var binding: FragmentIntroBinding by lifecycleAwareVariable()

    companion object {
        fun newInstance(position: Int, isNewUser: Boolean = true): IntroFragment {
            val instance = IntroFragment()
            instance.arguments = Bundle().apply {
                putInt(INTRO_POSITION_KEY, position)
                putBoolean(IN_IS_NEW_USER, isNewUser)
            }
            return instance
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIntroBinding.bind(view)

        introPos = arguments?.getInt(INTRO_POSITION_KEY)

        val isNewUser = arguments?.getBoolean(IN_IS_NEW_USER, true)
        isNewUser ?: return

        if (isNewUser) {
            val newUserEnum = NewUserEnum.values()[introPos ?: return]
            setUpNewUserViews(newUserEnum)
        } else {
            val migratingUserEnum = MigratingUserEnum.values()[introPos ?: return]
            setUpMigratingUserViews(migratingUserEnum)
        }
    }

    override fun onResume() {
        super.onResume()
        val am: AccessibilityManager? =
            context?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
        if (am == null || !am.isEnabled) {
            binding.descriptionTV.movementMethod = ScrollingMovementMethod()
        }
    }

    private fun setUpNewUserViews(newUserEnum: NewUserEnum) {
        binding.introImageView.background = ContextCompat.getDrawable(requireContext(), newUserEnum.image)
        binding.descriptionTV.text = getString(newUserEnum.subtitle)

        val fullText = getString(newUserEnum.title)
        val highlightedText = getString(newUserEnum.highlightedWord)

        if (introPos == 0) {
            binding.titleTV.text = fullText
        } else
            binding.titleTV.setTextWithUnderlineSpan(
                highlightedText,
                fullText,
                UIUtils.getColor(R.color.colorPrimary)
            )
    }

    private fun setUpMigratingUserViews(migratingUserEnum: MigratingUserEnum) {
        binding.descriptionTV.text = getString(migratingUserEnum.subtitle)

        val fullText = getString(migratingUserEnum.title)
        val highlightedText = getString(migratingUserEnum.highlightedWord)

        if (introPos == 0) {
            binding.titleTV.text = fullText
        } else
            binding.titleTV.setTextWithUnderlineSpan(
                highlightedText,
                fullText,
                UIUtils.getColor(R.color.colorPrimary)
            )
    }
}
