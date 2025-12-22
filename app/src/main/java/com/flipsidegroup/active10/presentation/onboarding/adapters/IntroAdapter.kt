package com.flipsidegroup.active10.presentation.onboarding.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flipsidegroup.active10.presentation.onboarding.fragments.IntroFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.WhereDoYouLiveFragment


@SuppressLint("WrongConstant")
class IntroAdapter(
    fragmentManager: FragmentManager,
    private var isNewUser: Boolean
) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var items = listOf(IntroItemType.INFO, IntroItemType.INFO, IntroItemType.INFO, IntroItemType.WHERE_DO_YOU_LIVE)

    override fun getItem(position: Int): Fragment {
        return when (items[position]) {
            IntroItemType.INFO -> IntroFragment.newInstance(position, isNewUser)
            IntroItemType.WHERE_DO_YOU_LIVE -> WhereDoYouLiveFragment.newInstance(isNewUser)
        }
    }

    override fun getCount(): Int = items.size

    fun getItemType(position: Int) = items[position]
}

enum class IntroItemType {
    INFO, WHERE_DO_YOU_LIVE
}