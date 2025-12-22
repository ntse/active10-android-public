package com.flipsidegroup.active10.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.flipsidegroup.active10.data.models.Goal



class GoalsDiffUtil(private var oldList: List<Goal>, private var newList: List<Goal>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].goalId == newList[newItemPosition].goalId
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}