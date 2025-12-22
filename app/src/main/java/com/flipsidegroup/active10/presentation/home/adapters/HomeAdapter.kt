package com.flipsidegroup.active10.presentation.home.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.presentation.discover.fragments.DiscoverFragment
import com.flipsidegroup.active10.presentation.mywalks.MyWalksFragment
import com.flipsidegroup.active10.presentation.settings.fragments.SettingsFragment
import com.flipsidegroup.active10.presentation.todaywalk.TodayWalkFragment
import com.flipsidegroup.active10.presentation.todaywalk.TodayWalkPermissionFragment


private const val COUNT = 4

const val TODAY_WALK_SCREEN_POSITION = 0
const val MY_WALK_SCREEN_POSITION = 1
const val DISCOVER_SCREEN_POSITION = 2
const val BETTER_HEALTH_SCREEN_POSITION = 21
const val SETTINGS_SCREEN_POSITION = 3

@SuppressLint("WrongConstant")
class HomeAdapter(
    fragmentManager: FragmentManager,
    private var isFitnessMotionEnabled: Boolean?,
    private val periodType: PeriodTypeEnum = PeriodTypeEnum.DAYS
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            TODAY_WALK_SCREEN_POSITION -> getTodayFragment()
            MY_WALK_SCREEN_POSITION -> MyWalksFragment.newInstance(periodType)
            DISCOVER_SCREEN_POSITION -> DiscoverFragment.newInstance()
            SETTINGS_SCREEN_POSITION -> SettingsFragment.newInstance()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = COUNT

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun updatePermission(isEnabled: Boolean) {
        isFitnessMotionEnabled = isEnabled
        notifyDataSetChanged()
    }

    private fun getTodayFragment(): Fragment {
        return if (isFitnessMotionEnabled != true) {
            TodayWalkPermissionFragment.newInstance()
        } else {
            TodayWalkFragment.newInstance()
        }
    }
}