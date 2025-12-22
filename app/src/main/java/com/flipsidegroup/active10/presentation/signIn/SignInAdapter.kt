package com.flipsidegroup.active10.presentation.signIn

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.utils.Constants

class SignInAdapter(fragmentManager: FragmentManager, private val contentMap: Map<String, ScreenContent>) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            SignInMainFragment.newInstance(
                contentMap[Constants.SIGN_IN_MAIN_CONTENT]!!
            )
        } else {
            SignInInfoFragment.newInstance(
                contentMap[Constants.SIGN_IN_INFO_CONTENT]!!
            )
        }
    }

    override fun getCount(): Int = 2
}