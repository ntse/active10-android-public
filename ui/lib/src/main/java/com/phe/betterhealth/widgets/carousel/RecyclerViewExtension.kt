package com.phe.betterhealth.widgets.carousel

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION

val RecyclerView.firstVisibleItem: Int
    get() = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: -1

val RecyclerView.firstPartiallyVisibleItem: Int
    get() = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: -1

val RecyclerView.lastVisibleItem: Int
    get() = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: -1

val RecyclerView.currentItem: Int
    get() = when {
        itemCount - 1 == lastVisibleItem -> lastVisibleItem
        firstVisibleItem > NO_POSITION -> firstVisibleItem
        firstVisibleItem == NO_POSITION -> firstPartiallyVisibleItem
        else -> 0
    }

val RecyclerView.itemCount: Int
    get() = adapter?.itemCount ?: 0
