package com.flipsidegroup.active10.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.flipsidegroup.active10.data.FaqItem


class FaqDiffUtil(private var oldList: List<FaqItem>, private var newList: List<FaqItem>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].description == newList[newItemPosition].description
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}