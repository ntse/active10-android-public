package com.phe.betterhealth.components.carousel

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * inspired by [androidx.recyclerview.widget.ListAdapter]
 */
abstract class RecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
    private val diffCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {

    private val items = mutableListOf<T>()

    val currentList: List<T> get() = items

    override fun getItemCount() = items.size

    fun submitList(list: List<T>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(list, items.toMutableList()))

        with(items) {
            clear()
            addAll(list)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    protected open fun getItem(position: Int): T = items[position]

    private inner class DiffCallback(
        private val newList: List<T>,
        private val oldList: List<T>
    ) : DiffUtil.Callback() {

        override fun getNewListSize() = newList.size

        override fun getOldListSize() = oldList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItemPosition
                .takeIf { oldList.size <= newList.size }?.let { getItem(it) } ?: return false
            val newItem = newItemPosition
                .takeIf { newList.size <= oldList.size }?.let { getItem(it) } ?: return false
            return diffCallback.areContentsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItemPosition
                .takeIf { oldList.size <= newList.size }?.let { getItem(it) } ?: return false
            val newItem = newItemPosition
                .takeIf { newList.size <= oldList.size }?.let { getItem(it) } ?: return false
            return diffCallback.areItemsTheSame(oldItem, newItem)
        }
    }
}
