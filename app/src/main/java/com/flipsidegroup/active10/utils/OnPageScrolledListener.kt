package com.flipsidegroup.active10.utils

import androidx.viewpager.widget.ViewPager


class OnPageScrolledListener(private val listener: (position: Int, positionOffset: Float) -> Unit) :
    ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {
        // Intentionally empty
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        listener.invoke(position, positionOffset)
    }

    override fun onPageSelected(position: Int) {
        // Intentionally empty
    }
}