package com.flipsidegroup.active10.presentation.onboarding.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.PermissionEnum
import com.flipsidegroup.active10.presentation.goals.fragments.GoalsFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.PermissionFragment
import com.flipsidegroup.active10.presentation.userDetails.fragments.UserDetailsFragment


class PermissionAdapter(
    fragmentManager: FragmentManager,
    private val onboarding: Onboarding?
) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (getItemTypes()[position]) {
            PermissionEnum.FITNESS_PERMISSION -> PermissionFragment.newInstance(
                position,
                onboarding?.onboardingPermission?.motionFitness
            )
            PermissionEnum.NOTIFICATIONS_PERMISSION -> PermissionFragment.newInstance(
                position,
                onboarding?.onboardingPermission?.notifications
            )
            PermissionEnum.GOAL_LIST -> GoalsFragment.newInstance(position, true)
            PermissionEnum.USER_DETAILS -> UserDetailsFragment.newInstance(position, onboarding?.aboutYou ?: "")
        }
    }

    fun getPermissionTypeForPosition(position: Int) = getItemTypes()[position]

    fun getItemTypes() = PermissionEnum.entries.toTypedArray()

    override fun getCount(): Int = getItemTypes().size

}

