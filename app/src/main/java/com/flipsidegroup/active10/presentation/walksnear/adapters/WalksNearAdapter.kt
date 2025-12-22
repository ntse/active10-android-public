package com.flipsidegroup.active10.presentation.walksnear.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.databinding.ItemWalksNearBannerBinding
import com.flipsidegroup.active10.databinding.ItemWalksNearCircularWalkBinding
import com.flipsidegroup.active10.databinding.ItemWalksNearCredentialsBinding
import com.flipsidegroup.active10.databinding.ItemWalksNearCuratedWalkBinding
import com.flipsidegroup.active10.databinding.ItemWalksNearIntroBinding
import com.flipsidegroup.active10.databinding.ItemWalksNearNoPostcodeBinding
import com.flipsidegroup.active10.utils.convertSecondsToMinAndHoursString
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setHeading
import com.phe.betterhealth.widgets.common.setHtml
import java.math.RoundingMode

class WalksNearAdapter : ListAdapter<WalksNearPart, RecyclerView.ViewHolder>(Differ) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (WalksNearType.entries[viewType]) {
            WalksNearType.NO_POSTCODE -> NoPostcodeViewHolder(
                ItemWalksNearNoPostcodeBinding.inflate(inflater, parent, false)
            )

            WalksNearType.CREDENTIALS -> CredentialsViewHolder(
                ItemWalksNearCredentialsBinding.inflate(inflater, parent, false)
            )

            WalksNearType.INTRO -> IntroViewHolder(
                ItemWalksNearIntroBinding.inflate(inflater, parent, false)
            )

            WalksNearType.BANNER -> BannerViewHolder(
                ItemWalksNearBannerBinding.inflate(inflater, parent, false)
            )

            WalksNearType.CIRCULAR_WALK -> CircularWalkViewHolder(
                ItemWalksNearCircularWalkBinding.inflate(inflater, parent, false)
            )

            WalksNearType.CURATED_WALK -> CuratedWalkViewHolder(
                ItemWalksNearCuratedWalkBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoPostcodeViewHolder -> holder.bind(getItem(position) as WalksNearPart.NoPostcode)
            is IntroViewHolder -> holder.bind(getItem(position) as WalksNearPart.Intro)
            is BannerViewHolder -> holder.bind(getItem(position) as WalksNearPart.Banner)
            is CircularWalkViewHolder -> holder.bind(getItem(position) as WalksNearPart.CircularWalk)
            is CuratedWalkViewHolder -> holder.bind(getItem(position) as WalksNearPart.CuratedWalk)
        }
    }

    private class NoPostcodeViewHolder(
        private val binding: ItemWalksNearNoPostcodeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalksNearPart.NoPostcode) {
            with(binding) {
                description.setHtml(item.description)
            }
        }
    }

    private class CredentialsViewHolder(
        private val binding: ItemWalksNearCredentialsBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    private class IntroViewHolder(
        private val binding: ItemWalksNearIntroBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalksNearPart.Intro) {
            with(binding) {
                title.setHtml(item.title)
                title.setHeading()
                description.setHtml(item.description)
            }
        }
    }

    private class BannerViewHolder(
        private val binding: ItemWalksNearBannerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalksNearPart.Banner) {
            binding.description.setHtml(
                item.description.replace("{postcode}", "<strong>${item.postcode}</strong>")
            )
        }
    }

    private class CircularWalkViewHolder(
        private val binding: ItemWalksNearCircularWalkBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalksNearPart.CircularWalk) {
            with(binding) {
                content = item
                image.loadFromUrl(item.image)
                val duration = item.duration.convertSecondsToMinAndHoursString()
                val distance =
                    item.distance.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
                measurableDetailsText.text = "$duration / ${distance}km"
                walkDetailsButton.setOnClickListener {
                    item.onClickCallback(item.id.toLong())
                }
                executePendingBindings()
            }
        }
    }

    private class CuratedWalkViewHolder(
        private val binding: ItemWalksNearCuratedWalkBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalksNearPart.CuratedWalk) {
            with(binding) {
                content = item
                image.loadFromUrl(item.image)
                val duration = item.duration.convertSecondsToMinAndHoursString()
                val distance =
                    item.distance.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
                binding.measurableDetailsText.text = "$duration / ${distance}km"
                binding.postcodeDistance.text = "${item.postcodeDistance}km from you"
                walkDetailsButton.setOnClickListener {
                    item.onClickCallback(item.id.toLong())
                }
                executePendingBindings()
            }
        }
    }

    private object Differ : DiffUtil.ItemCallback<WalksNearPart>() {
        override fun areItemsTheSame(
            oldItem: WalksNearPart, newItem: WalksNearPart
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WalksNearPart, newItem: WalksNearPart
        ): Boolean = oldItem == newItem
    }
}