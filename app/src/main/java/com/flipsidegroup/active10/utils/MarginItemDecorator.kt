package com.flipsidegroup.active10.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class MarginItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {

            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                right = space
            }
        }
    }
}