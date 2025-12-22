package com.phe.betterhealth.demo.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.phe.betterhealth.compose.carousel.BHCarouselIndicator as ComposeCarouselIndicator
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.widgets.carousel.BHCarouselIndicator
import com.phe.betterhealth.widgets.carousel.BHCarouselOffsetPageTransformer
import com.phe.betterhealth.widgets.utils.dpToPx


class CarouselDemo : Fragment(R.layout.fragment_carousel) {

    private val scrollState = hashMapOf<Int, BHCarouselIndicator.SavedState?>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().findViewById<ComposeView>(R.id.view_pager_compose).setContent {
            val state = rememberPagerState(
                initialPage = 0,
                pageCount = { 5 },
            )
            HorizontalPager(state) {
                Box {
                    Text("Page ${it + 1}")
                }
            }
            ComposeCarouselIndicator(state)
        }

        val viewPagers = listOf(
            R.id.view_pager1 to R.id.bh_indicator1,
            R.id.view_pager2 to R.id.bh_indicator2,
            R.id.view_pager3 to R.id.bh_indicator3,
            R.id.view_pager4 to R.id.bh_indicator4,
        )

        initializeViewPagerWithIndicatorButEmpty(R.id.view_pager00, R.id.bh_indicator00)

        viewPagers.forEach { (viewPagerId, indicatorId) ->
            initializeViewPagerWithIndicator(viewPagerId, indicatorId)
        }

        val recyclerViews = listOf(
            R.id.recycler_view0 to R.id.bh_indicator0,
            R.id.recycler_view1 to R.id.bh_indicator11,
        )

        recyclerViews.forEach { (recyclerViewId, indicatorId) ->
            initializeRecyclerViewWithIndicator(recyclerViewId, indicatorId)
        }
    }

    private fun initializeViewPagerWithIndicatorButEmpty(viewPagerId: Int, indicatorId: Int) {
        val (viewPager, indicator) = getViewPagerWithIndicator(
            viewPagerId = viewPagerId,
            indicatorId = indicatorId
        )
        with(viewPager) {
            adapter = EmptyCarouselAdapter()
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPageTransformer(
                BHCarouselOffsetPageTransformer(50.dpToPx(context), 20.dpToPx(context))
            )
        }
        with(indicator) {
            addAfterPageSwitchListener { scrollState[viewPagerId] = it }
            setupWithViewPager2(viewPager, scrollState[viewPagerId])
        }
    }

    private fun initializeViewPagerWithIndicator(viewPagerId: Int, indicatorId: Int) {
        val (viewPager, indicator) = getViewPagerWithIndicator(viewPagerId, indicatorId)

        with(viewPager) {
            adapter = CarouselAdapter(R.layout.item_carousel_viewpager)
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPageTransformer(
                BHCarouselOffsetPageTransformer(50.dpToPx(context), 20.dpToPx(context))
            )
        }
        with(indicator) {
            addAfterPageSwitchListener { scrollState[viewPagerId] = it }
            setupWithViewPager2(viewPager, scrollState[viewPagerId])
        }
    }

    private fun getViewPagerWithIndicator(
        viewPagerId: Int, indicatorId: Int
    ): Pair<ViewPager2, BHCarouselIndicator> {
        val viewPager = requireView().findViewById<ViewPager2>(viewPagerId)
        val indicator = requireView().findViewById<BHCarouselIndicator>(indicatorId)

        return viewPager to indicator
    }

    private fun initializeRecyclerViewWithIndicator(recyclerViewId: Int, indicatorId: Int) {
        val recyclerView = requireView().findViewById<RecyclerView>(recyclerViewId)
        val indicator = requireView().findViewById<BHCarouselIndicator>(indicatorId)

        with(recyclerView) {
            adapter = CarouselAdapter(
                when (recyclerViewId) {
                    R.id.recycler_view0 -> R.layout.item_carousel_recyclerview_large
                    else -> R.layout.item_carousel_recyclerview
                }
            )
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        with(indicator) {
            addAfterPageSwitchListener { scrollState[recyclerViewId] = it }
            setupWithRecyclerView(recyclerView, scrollState[recyclerViewId])
        }
    }

    private class CarouselAdapter(@LayoutRes private val id: Int) :
        RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

        private val colors = listOf(
            R.color.purple_500, R.color.teal_200,
        )

        override fun getItemCount() = colors.size * 5

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(id, parent, false)
        )

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val description = holder.itemView.findViewById<TextView>(R.id.pheCardDescription)
            val root = holder.itemView.rootView

            description.text = "Page ${position + 1}"
            root.setBackgroundColor(colors.random())
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    private class EmptyCarouselAdapter : RecyclerView.Adapter<EmptyCarouselAdapter.ViewHolder>() {
        override fun getItemCount() = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_demo, parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            throw IllegalStateException("EMPTY ADAPTER!")
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
