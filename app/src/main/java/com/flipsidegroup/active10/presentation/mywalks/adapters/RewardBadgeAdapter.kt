package com.flipsidegroup.active10.presentation.mywalks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.databinding.ItemRewardBadgeBinding
import com.flipsidegroup.active10.databinding.ItemRewardsCenterBinding
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setRoleDescription
import com.phe.betterhealth.widgets.utils.textColor

data class RewardWithPosition(
    val reward: RewardBadge,
    val position: Int,
    val itemsCount: Int
)

class RewardBadgeAdapter(
    private val isSmallBadge: Boolean = false,
    private var listener: (reward: RewardBadge) -> (Unit)
) : ListAdapter<RewardWithPosition, RecyclerView.ViewHolder>(DiffCallback()) {

    private var earnedBadges: List<EarnRewardBadge> = emptyList()

    fun addRewardsMyWalk(rewards: List<RewardBadge>, earnedBadges: List<EarnRewardBadge>) {
        this.earnedBadges = earnedBadges
        submitList(rewards.mapIndexed { idx, item ->
            RewardWithPosition(item, idx + 1, rewards.size)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when {
            isSmallBadge -> RewardBadgeSmall(
                ItemRewardBadgeBinding.inflate(inflater, parent, false)
            )

            else -> RewardBadgeVH(ItemRewardsCenterBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RewardBadgeSmall -> holder.bind(getItem(position))
            is RewardBadgeVH -> holder.bind(getItem(position))
        }
    }

    inner class RewardBadgeSmall(
        private val binding: ItemRewardBadgeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RewardWithPosition) {
            binding.root.setOnClickListener {
                listener(item.reward)
            }
            val isEarned = earnedBadges.find { it.id == item.reward.id } != null
            val imgUrl = if (isEarned) {
                binding.badgeTitleTv.setRoleDescription(
                    UIUtils.getString(
                        R.string.achieved_badge_with_position,
                        item.position,
                        item.itemsCount
                    )
                )
                item.reward.onImage
            } else {
                binding.badgeTitleTv.setRoleDescription(
                    UIUtils.getString(
                        R.string.not_achieved_badge_with_position,
                        item.position,
                        item.itemsCount
                    )
                )
                item.reward.offImage
            }
            if (!isSmallBadge) {
                val params = binding.badgeIconIv.layoutParams as ConstraintLayout.LayoutParams
                params.height = UIUtils.getDimensionInPx(R.dimen.badge_height)
                params.height = UIUtils.getDimensionInPx(R.dimen.badge_width)
                binding.root.invalidate()
            }
            binding.badgeIconIv.loadFromUrl(imgUrl)
            binding.badgeTitleTv.text = item.reward.title

            dynamicNumber(binding.numberTV, item.reward)
        }
    }

    inner class RewardBadgeVH(
        private val binding: ItemRewardsCenterBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: RewardWithPosition) {
            binding.root.setOnClickListener {
                listener(item.reward)
            }
            val isEarned = earnedBadges.find { it.id == item.reward.id } != null
            val imgUrl = if (isEarned) {
                binding.badgeTitleTv.setRoleDescription(
                    UIUtils.getString(
                        R.string.achieved_badge_with_position,
                        item.position,
                        item.itemsCount
                    )
                )
                item.reward.onImage
            } else {
                binding.badgeTitleTv.setRoleDescription(
                    UIUtils.getString(
                        R.string.not_achieved_badge_with_position,
                        item.position,
                        item.itemsCount
                    )
                )
                item.reward.offImage
            }
            if (!isSmallBadge) {
                val params = binding.badgeIconIv.layoutParams as ConstraintLayout.LayoutParams
                params.height = UIUtils.getDimensionInPx(R.dimen.badge_height)
                params.height = UIUtils.getDimensionInPx(R.dimen.badge_width)
                binding.root.invalidate()
            }
            binding.badgeIconIv.loadFromUrl(imgUrl)
            binding.badgeTitleTv.text = item.reward.title

            dynamicNumber(binding.numberTV, item.reward)
        }
    }

    private fun dynamicNumber(numberTV: TextView, reward: RewardBadge) {
        if (reward.slug != RewardBadgeEnum.TARGET_HITTER.slug) return
        if (reward.repetitions == 0) return

        numberTV.textColor = R.color.white
        numberTV.text = reward.repetitions.toString()
        numberTV.isVisible = true
    }

    private class DiffCallback : DiffUtil.ItemCallback<RewardWithPosition>() {

        override fun areContentsTheSame(oldItem: RewardWithPosition, newItem: RewardWithPosition) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: RewardWithPosition, newItem: RewardWithPosition) =
            oldItem.reward.id == newItem.reward.id
    }
}
