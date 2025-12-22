package com.flipsidegroup.active10.presentation.walkingplandetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.databinding.ItemFuncInfoActionBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanChevronItemBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanOverviewBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanPrimaryButtonBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanSecondaryButtonBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanTagImageBinding
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.phe.betterhealth.widgets.common.setHtml

class WalkingPlanDetailsAdapter : ListAdapter<WalkingPlanDetailsPart, RecyclerView.ViewHolder>(Differ) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (WalkingPlanDetailsType.entries[viewType]) {
            WalkingPlanDetailsType.TAG_IMAGE -> TagImageViewHolder(
                ItemWalkingPlanTagImageBinding.inflate(inflater, parent, false)
            )
            WalkingPlanDetailsType.OVERVIEW -> OverviewViewHolder(
                ItemWalkingPlanOverviewBinding.inflate(inflater, parent, false)
            )
            WalkingPlanDetailsType.PRIMARY_BUTTON -> PrimaryButtonViewHolder(
                ItemWalkingPlanPrimaryButtonBinding.inflate(inflater, parent, false)
            )
            WalkingPlanDetailsType.SECONDARY_BUTTON -> SecondaryButtonViewHolder(
                ItemWalkingPlanSecondaryButtonBinding.inflate(inflater, parent, false)
            )
            WalkingPlanDetailsType.CHEVRON_ITEM -> ChevronItemViewHolder(
                ItemWalkingPlanChevronItemBinding.inflate(inflater, parent, false)
            )
            WalkingPlanDetailsType.FUNC_ITEM -> FuncItemViewHolder(
                ItemFuncInfoActionBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TagImageViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.TagImage)
            is OverviewViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.Overview)
            is PrimaryButtonViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.PrimaryButton)
            is SecondaryButtonViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.SecondaryButton)
            is ChevronItemViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.ChevronItem)
            is FuncItemViewHolder -> holder.bind(getItem(position) as WalkingPlanDetailsPart.FuncItem)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            onBindViewHolder(holder, position)
        } else super.onBindViewHolder(holder, position, payloads)
    }

    private class TagImageViewHolder(
        private val binding: ItemWalkingPlanTagImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanDetailsPart.TagImage) {
            with(binding) {
                content = item
                image.loadFromUrl(item.image)
                executePendingBindings()
            }
        }
    }

    private class OverviewViewHolder(
        private val binding: ItemWalkingPlanOverviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val itineraryAdapter by lazy { WalkingPlanItineraryAdapter() }
        private val pagerSnapHelper = PagerSnapHelper()

        fun bind(item: WalkingPlanDetailsPart.Overview) {
            with(itineraryAdapter) {
                submitList(item.planItineraryItems)
                onPageChangeClickCallback = {
                    binding.recyclerView.smoothScrollToPosition(it)
                }
            }
            with(binding) {
                pagerSnapHelper.attachToRecyclerView(recyclerView)
                recyclerView.adapter = itineraryAdapter
                content = item
                executePendingBindings()
            }
        }
    }

    private class PrimaryButtonViewHolder(
        private val binding: ItemWalkingPlanPrimaryButtonBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanDetailsPart.PrimaryButton) {
            with(binding) {
                button.text = item.title
                button.setOnClickListenerWithDebounce { item.onClickCallback() }
            }
        }
    }


    private class SecondaryButtonViewHolder(
        private val binding: ItemWalkingPlanSecondaryButtonBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanDetailsPart.SecondaryButton) {
            with(binding) {
                button.text = item.title
                button.setOnClickListenerWithDebounce { item.onClickCallback() }
            }
        }
    }

    private class ChevronItemViewHolder(
        private val binding: ItemWalkingPlanChevronItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanDetailsPart.ChevronItem) {
            with(binding) {
                content = item
                executePendingBindings()

                image.setImageResource(item.image)
                cardContainer.setOnClickListenerWithDebounce { item.onClickCallback() }
            }
        }
    }

    private class FuncItemViewHolder(
        private val binding: ItemFuncInfoActionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanDetailsPart.FuncItem) {
            with(binding) {
                title.text = item.title
                description.setHtml(item.description)
                button.text = item.actionTitle
                image.loadFromUrl(item.image)

                button.setOnClickListenerWithDebounce { item.onClickCallback(item.actionSlug) }
            }
        }
    }

    private object Differ : DiffUtil.ItemCallback<WalkingPlanDetailsPart>() {
        override fun areItemsTheSame(
            oldItem: WalkingPlanDetailsPart,
            newItem: WalkingPlanDetailsPart
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WalkingPlanDetailsPart,
            newItem: WalkingPlanDetailsPart
        ): Boolean = oldItem == newItem

        override fun getChangePayload(
            oldItem: WalkingPlanDetailsPart,
            newItem: WalkingPlanDetailsPart
        ): Any = true
    }
}