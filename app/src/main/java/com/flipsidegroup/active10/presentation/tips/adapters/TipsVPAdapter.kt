package com.flipsidegroup.active10.presentation.tips.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.presentation.tips.TipsFragment


class TipsVPAdapter(fragmentManager: FragmentManager, private var tipsList: List<Tip>) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return TipsFragment.newInstance(tip = tipsList[position])
    }

    override fun getCount(): Int = tipsList.size

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    fun updateTips(tipsList: List<Tip>) {
        this.tipsList = tipsList
        notifyDataSetChanged()
    }
}