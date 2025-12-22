package com.flipsidegroup.active10.presentation.onboarding.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.PermissionEnum
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentPermissionBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import javax.inject.Inject

private const val IN_PERMISSION_POSITION_KEY = "in_permission_position_key"
private const val IN_PERMISSION_DESCRIPTION_KEY = "in_permission_description_KEY"

class PermissionFragment : BaseFragment<BaseView>() {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private var permissionEnum: PermissionEnum? = null
    private var description: String? = null

    private var binding: FragmentPermissionBinding by lifecycleAwareVariable()

    companion object {
        fun newInstance(position: Int, description: String?): PermissionFragment {
            val instance = PermissionFragment()
            instance.arguments = Bundle().apply {
                putInt(IN_PERMISSION_POSITION_KEY, position)
                putString(IN_PERMISSION_DESCRIPTION_KEY, description)
            }
            return instance
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPermissionBinding.bind(view)

        val permissionPos = arguments?.getInt(IN_PERMISSION_POSITION_KEY)
        permissionPos ?: return

        description = arguments?.getString(IN_PERMISSION_DESCRIPTION_KEY)

        permissionEnum = PermissionEnum.values()[permissionPos]

        permissionEnum?.let {
            setUpViews(it)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.containerLL.requestFocus()
        binding.titleTV.announceHeader()
    }

    private fun setUpViews(permissionEnum: PermissionEnum) {
        logScreenViewEvent(permissionEnum)
        binding.descriptionTV.movementMethod = ScrollingMovementMethod()
        binding.permissionImageView.background =
            ContextCompat.getDrawable(requireContext(), permissionEnum.image)

        if (permissionEnum.title != 0) {
            binding.titleTV.text = getString(permissionEnum.title)
            binding.titleTV.setHeading()
        } else {
            binding.titleTV.isVisible = false
        }

        if (permissionEnum == PermissionEnum.FITNESS_PERMISSION) {
            binding.descriptionTV.text = getString(permissionEnum.subtitle)
        } else if (permissionEnum.subtitle != 0) {
            binding.descriptionTV.text = description ?: getString(permissionEnum.subtitle)
        } else {
            binding.descriptionTV.isVisible = false
        }
    }

    private fun logScreenViewEvent(permissionEnum: PermissionEnum) {
        val nhsPrefix = if (preferenceRepository.isUserLoggedIn) "NHSLogin" else ""
        firebaseAnalyticsHelper.sendViewScreenEvent(
            when (permissionEnum) {
                PermissionEnum.FITNESS_PERMISSION -> "${nhsPrefix}FitnessPermission"
                PermissionEnum.NOTIFICATIONS_PERMISSION -> "${nhsPrefix}StayOnTrack"
                PermissionEnum.GOAL_LIST -> "${nhsPrefix}GoalList"
                PermissionEnum.USER_DETAILS -> "${nhsPrefix}UserDetails"
            }
        )
    }
}
