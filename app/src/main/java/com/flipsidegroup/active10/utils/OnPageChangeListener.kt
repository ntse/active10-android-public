package com.flipsidegroup.active10.utils

import androidx.viewpager.widget.ViewPager

class OnPageChangeListener(private val listener: (position: Int) -> Unit) :
    ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        listener.invoke(position)
    }
}