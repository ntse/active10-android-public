package com.phe.betterhealth.widgets.carousel

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapterObserver(val callback: () -> Unit) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        callback()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        callback()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        callback()
    }
}
