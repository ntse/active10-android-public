package com.phe.betterhealth.widgets.common

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * @see [https://medium.com/@rituel521/improving-accuracy-of-computeverticalscrolloffset-for-linearlayoutmanager-38699a9d03b]
 */
class ExactScrollAwareLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    // map of child adapter position to its height.
    private var childSizesMap = ConcurrentHashMap<Int, Int>()

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        for (i in 0 until childCount) {
            getChildAt(i)?.let {
                childSizesMap[getPosition(it)] = it.height
            }
        }
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        if (childCount == 0) {
            return 0
        }
        val firstChild = getChildAt(0) ?: return 0
        val firstChildPosition = getPosition(firstChild)
        var scrolledY = -firstChild.y.toInt()
        for (i in 0 until firstChildPosition) {
            scrolledY += childSizesMap[i] ?: 0
        }
        return scrolledY
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val items = savedInstanceState?.getIntegerArrayList(LM_SIZES).orEmpty()
            .mapIndexed { index, i -> index to i }.toMap()
        Timber.d("")
        if (items.isNotEmpty()) {
            childSizesMap = ConcurrentHashMap(items)
        }
    }

    fun onSaveInstanceState(outState: Bundle?) {
        val items = childSizesMap.map { it.value }.let(::ArrayList)
        outState?.putIntegerArrayList(LM_SIZES, items)
    }

    companion object {
        private const val LM_SIZES = "lm_sizes"
    }
}
