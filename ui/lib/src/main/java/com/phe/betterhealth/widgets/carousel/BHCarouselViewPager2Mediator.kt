package com.phe.betterhealth.widgets.carousel

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import java.lang.ref.WeakReference
import kotlin.math.max

/**
 * Inspired by [com.google.android.material.tabs.TabLayoutMediator]
 */
class BHCarouselViewPager2Mediator(
    private val bhIndicator: BHCarouselIndicator,
    private val viewPager: ViewPager2,
    private val autoRefresh: Boolean = true,
) : BHCarouselMediator {

    private var attached = false

    private var adapter: RecyclerView.Adapter<*>? = null

    private var onPageChangeCallback: OnPageChangeCallback? = null

    private var onTabSelectedListener: BHCarouselIndicator.OnPageSwitchListener? = null

    private var pagerAdapterObserver: AdapterDataObserver? = null

    override fun attach(): BHCarouselViewPager2Mediator {
        check(!attached) { "BHCarouselMediator is already attached" }
        adapter = viewPager.adapter
        checkNotNull(adapter) { "BHCarouselMediator attached before ViewPager2 has an " + "adapter" }
        attached = true

        // Add our custom OnPageChangeCallback to the ViewPager
        onPageChangeCallback = IndicatorOnPageChangeCallback(bhIndicator, viewPager)
        viewPager.registerOnPageChangeCallback(onPageChangeCallback!!)

        // Now we'll add a tab selected listener to set ViewPager's current item
        onTabSelectedListener = ViewPagerOnPageSwitchedListener(viewPager)
        bhIndicator.addOnPageSwitchListener(onTabSelectedListener)

        // Now we'll populate ourselves from the pager adapter, adding an observer if
        // autoRefresh is enabled
        if (autoRefresh) {
            // Register our observer on the new adapter
            pagerAdapterObserver = RecyclerViewAdapterObserver {
                initializeIndicatorBasedOnPagerAdapter()
            }
            adapter!!.registerAdapterDataObserver(pagerAdapterObserver!!)
        }

        initializeIndicatorBasedOnPagerAdapter()

        return this
    }

    override fun onStateRestore(savedState: BHCarouselIndicator.SavedState?) {
        viewPager.setCurrentItem(savedState?.pagePosition ?: 0, false)
    }

    override fun detach() {
        if (autoRefresh && adapter != null) {
            adapter?.unregisterAdapterDataObserver(pagerAdapterObserver!!)
            pagerAdapterObserver = null
        }
        bhIndicator.removeOnPageSwitchListener(onTabSelectedListener)
        onPageChangeCallback?.let { viewPager.unregisterOnPageChangeCallback(it) }
        onTabSelectedListener = null
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    private fun initializeIndicatorBasedOnPagerAdapter() {
        adapter ?: return

        val adapterCount = adapter!!.itemCount

        bhIndicator.pageCount = adapterCount

        if (adapterCount <= 0) return bhIndicator.reloadNavigation()

        // Make sure we reflect the currently set ViewPager item
        val currItem = max(viewPager.currentItem, 0)
        if (currItem != bhIndicator.currentPagePosition) {
            bhIndicator.onSwitchPage(currItem, null)
        }
    }

    // when indicator switched page, update viewpager
    private class ViewPagerOnPageSwitchedListener constructor(
        private val viewPager: ViewPager2,
    ) : BHCarouselIndicator.OnPageSwitchListener {

        override fun onPageSwitch(newPosition: Int) {
            viewPager.setCurrentItem(newPosition, true)
        }
    }

    // when viewpager switched page, update indicator
    private class IndicatorOnPageChangeCallback(
        bhIndicator: BHCarouselIndicator?,
        viewPager: ViewPager2
    ) : OnPageChangeCallback() {

        private val indicatorRef = WeakReference(bhIndicator)
        private val viewPagerRef = WeakReference(viewPager)

        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int, positionOffset: Float, positionOffsetPixels: Int
        ) = Unit

        override fun onPageSelected(position: Int) {
            val indicator = indicatorRef.get() ?: return

            indicator.onSwitchPage(position, null)

            val viewPager = viewPagerRef.get() ?: return

            viewPager.setAccessibilityFocusOnPage(position)
        }
    }
}
