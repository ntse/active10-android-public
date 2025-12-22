package com.flipsidegroup.active10.presentation.howitworks.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.presentation.howitworks.fragments.HowItWorksFragment


class HowItWorksAdapter(
    fragmentManager: FragmentManager,
    private var howItWorksList: List<HowItWorks>
) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return HowItWorksFragment.newInstance(howItWorks = howItWorksList[position])
    }

    override fun getCount(): Int = howItWorksList.size

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    fun updateHowItWorks(list: List<HowItWorks>) {
        this.howItWorksList = list
        notifyDataSetChanged()
    }
}