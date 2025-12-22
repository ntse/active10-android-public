package com.flipsidegroup.active10.presentation.mywalkingplans.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.databinding.ItemFuncInfoActionBinding
import com.flipsidegroup.active10.databinding.ItemMyWalkingPlanBinding
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.phe.betterhealth.widgets.common.setHtml

class MyWalkingPlansAdapter : ListAdapter<MyWalkingPlansPart, RecyclerView.ViewHolder>(Differ) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (MyWalkingPlansType.entries[viewType]) {
            MyWalkingPlansType.PLAN -> PlanViewHolder(
                ItemMyWalkingPlanBinding.inflate(inflater, parent, false)
            )

            MyWalkingPlansType.FUNC_ITEM -> FuncItemViewHolder(
                ItemFuncInfoActionBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlanViewHolder -> holder.bind(getItem(position) as MyWalkingPlansPart.Plan)
            is FuncItemViewHolder -> holder.bind(getItem(position) as MyWalkingPlansPart.FuncItem)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            onBindViewHolder(holder, position)
        } else super.onBindViewHolder(holder, position, payloads)
    }

    private class PlanViewHolder(
        private val binding: ItemMyWalkingPlanBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyWalkingPlansPart.Plan) {
            with(binding) {
                tagline.text = item.planTagline
                duration.text = item.planDuration
                title.text = item.planName
                description.setHtml(item.planDescription)
                image.loadFromUrl(item.image)
                button.text = item.buttonTitle

                pausedState.isVisible = item.state == WalkingPlanState.PAUSED
                activeState.isVisible = item.state == WalkingPlanState.ACTIVE
                duration.isVisible = item.state == WalkingPlanState.IDLE
                executePendingBindings()

                button.setOnClickListenerWithDebounce { item.onClickCallback(item.planId) }
            }
        }
    }

    private class FuncItemViewHolder(
        private val binding: ItemFuncInfoActionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyWalkingPlansPart.FuncItem) {
            with(binding) {
                title.text = item.title
                description.setHtml(item.description)
                button.text = item.actionTitle
                image.loadFromUrl(item.image)
                executePendingBindings()

                button.setOnClickListenerWithDebounce { item.onClickCallback(item.actionSlug) }
            }
        }
    }

    private object Differ : DiffUtil.ItemCallback<MyWalkingPlansPart>() {
        override fun areItemsTheSame(
            oldItem: MyWalkingPlansPart,
            newItem: MyWalkingPlansPart
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MyWalkingPlansPart,
            newItem: MyWalkingPlansPart
        ): Boolean = oldItem == newItem

        override fun getChangePayload(
            oldItem: MyWalkingPlansPart,
            newItem: MyWalkingPlansPart
        ): Any = true
    }
}