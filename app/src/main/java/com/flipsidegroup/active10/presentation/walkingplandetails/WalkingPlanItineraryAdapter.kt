package com.flipsidegroup.active10.presentation.walkingplandetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.data.models.api.PlanItineraryItem
import com.flipsidegroup.active10.databinding.ItemWalkingPlanItineraryItemBinding

class WalkingPlanItineraryAdapter :
    ListAdapter<PlanItineraryItem, RecyclerView.ViewHolder>(Differ) {

    var onPageChangeClickCallback: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItineraryViewHolder(
            ItemWalkingPlanItineraryItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItineraryViewHolder -> bindItineraryViewHolder(holder, getItem(position))
        }
    }

    private fun bindItineraryViewHolder(holder: ItineraryViewHolder, item: PlanItineraryItem) {
        setItemHeightDynamically(holder)
        with(holder.binding) {
            content = item
            executePendingBindings()
            holder.bindingAdapterPosition.let { pos ->
                indicatorCounter.text = "${pos + 1} of $itemCount"
                backButton.setOnClickListener {
                    if (pos > 0) onPageChangeClickCallback(pos - 1)
                }
                nextButton.setOnClickListener {
                    if (pos < itemCount - 1) onPageChangeClickCallback(pos + 1)
                }
            }
        }
    }

    private fun setItemHeightDynamically(holder: ItineraryViewHolder) {
        holder.itemView.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(holder.itemView.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            holder.itemView.measure(wMeasureSpec, hMeasureSpec)
            if (holder.itemView.measuredHeight > holder.itemView.height) {
                holder.itemView.layoutParams =
                    (holder.itemView.layoutParams as ViewGroup.LayoutParams).apply {
                        height = holder.itemView.measuredHeight
                    }
            }
        }
    }

    private class ItineraryViewHolder(val binding: ItemWalkingPlanItineraryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private object Differ : DiffUtil.ItemCallback<PlanItineraryItem>() {
        override fun areItemsTheSame(
            oldItem: PlanItineraryItem,
            newItem: PlanItineraryItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: PlanItineraryItem,
            newItem: PlanItineraryItem
        ): Boolean {
            return oldItem.weekLabel == newItem.weekLabel &&
                    oldItem.totalBriskMinutes == newItem.totalBriskMinutes &&
                    oldItem.descriptionOfWeeklyTask == newItem.descriptionOfWeeklyTask
        }
    }
}