package com.phe.betterhealth.widgets.carousel

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.max

class BHCarouselRecyclerViewMediator(
    private val bhIndicator: BHCarouselIndicator,
    private val recyclerView: RecyclerView,
    private val autoRefresh: Boolean = true,
) : BHCarouselMediator {

    private var attached = false

    private var adapter: RecyclerView.Adapter<*>? = null

    private var onPageChangeCallback: RecyclerView.OnScrollListener? = null

    private var onTabSelectedListener: BHCarouselIndicator.OnPageSwitchListener? = null

    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null

    override fun attach(): BHCarouselRecyclerViewMediator {
        check(!attached) { "BHCarouselRecyclerViewMediator is already attached" }
        adapter = recyclerView.adapter
        checkNotNull(adapter) { "BHCarouselRecyclerViewMediator attached before RecyclerView has an adapter" }
        attached = true

        // Add our custom OnPageChangeCallback to the RecyclerView
        onPageChangeCallback = IndicatorOnPageChangeCallback(bhIndicator)
        recyclerView.addOnScrollListener(onPageChangeCallback!!)

        // Now we'll add a tab selected listener to set RecyclerView's current item
        onTabSelectedListener = RecyclerViewOnPageSwitchedListener(recyclerView)
        bhIndicator.addOnPageSwitchListener(onTabSelectedListener)

        // Now we'll populate ourselves from the recycler adapter, adding an observer if
        // autoRefresh is enabled
        if (autoRefresh) {
            // Register our observer on the new adapter
            pagerAdapterObserver = RecyclerViewAdapterObserver {
                initializeIndicatorBasedOnRecyclerAdapter()
            }
            adapter!!.registerAdapterDataObserver(pagerAdapterObserver!!)
        }

        initializeIndicatorBasedOnRecyclerAdapter(initSet = true)

        return this
    }

    override fun detach() {
        if (autoRefresh && adapter != null) {
            adapter?.unregisterAdapterDataObserver(pagerAdapterObserver!!)
            pagerAdapterObserver = null
        }
        bhIndicator.removeOnPageSwitchListener(onTabSelectedListener)
        onPageChangeCallback?.let { recyclerView.removeOnScrollListener(it) }
        onTabSelectedListener = null
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    override fun onStateRestore(savedState: BHCarouselIndicator.SavedState?) {
        recyclerView.layoutManager?.onRestoreInstanceState(savedState?.scrollState)
    }

    private fun initializeIndicatorBasedOnRecyclerAdapter(initSet: Boolean = false) {
        val adapterCount = adapter?.itemCount ?: return

        bhIndicator.pageCount = adapterCount

        if (adapterCount <= 0) return bhIndicator.reloadNavigation()

        initialIndicatorNotify(initSet)
        if (initSet) recyclerView.post { initialIndicatorNotify(initSet) }
    }

    private fun initialIndicatorNotify(initSet: Boolean) {
        // Make sure we reflect the currently set RecyclerView item
        val currItem = max(recyclerView.currentItem, 0)
        if (currItem != bhIndicator.currentPagePosition || initSet) {
            bhIndicator.onSwitchPage(
                position = currItem,
                state = recyclerView.layoutManager?.onSaveInstanceState()
            )
        }
    }

    // when indicator switched page, update RecyclerView
    private class RecyclerViewOnPageSwitchedListener constructor(
        private val recyclerView: RecyclerView,
    ) : BHCarouselIndicator.OnPageSwitchListener {

        override fun onPageSwitch(newPosition: Int) {
            if (newPosition !in 0..recyclerView.itemCount) {
                return Timber.tag("BH").w("Invalid new position: $newPosition")
            }

            val smoothScroller = CenterSmoothScroller(recyclerView.context)
            smoothScroller.targetPosition = newPosition
            recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
        }
    }

    // when RecyclerView switched page, update indicator
    private class IndicatorOnPageChangeCallback(
        bhIndicator: BHCarouselIndicator?
    ) : RecyclerView.OnScrollListener() {

        private val indicatorRef = WeakReference(bhIndicator)
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) return
            val indicator = indicatorRef.get() ?: return

            indicator.onSwitchPage(
                position = recyclerView.currentItem,
                state = recyclerView.layoutManager?.onSaveInstanceState()
            )
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = Unit
    }

    /**
     * @see [https://stackoverflow.com/a/53756296/6695449]
     */
    private class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
        ) = boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
    }
}
