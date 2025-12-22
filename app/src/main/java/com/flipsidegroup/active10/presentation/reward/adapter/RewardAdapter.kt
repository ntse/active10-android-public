package com.flipsidegroup.active10.presentation.reward.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.databinding.ItemRewardCheckBinding
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.hide
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setRoleDescription
import com.flipsidegroup.active10.utils.show
import com.phe.betterhealth.widgets.utils.textColor

class RewardAdapter(
    val rewards: List<RewardBadge>,
    val listener: (reward: RewardBadge) -> Unit
) :
    RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder =
        RewardViewHolder(
            ItemRewardCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) =
        holder.bind(rewards[position])

    override fun getItemCount(): Int = rewards.size

    inner class RewardViewHolder(private val binding: ItemRewardCheckBinding) :
        RecyclerView.ViewHolder(binding.mainContainer) {

        fun bind(reward: RewardBadge) = with(itemView) {

            if (reward.isEarned) {
                binding.rewardImage.loadFromUrl(reward.onImage)
                binding.checkReward.show()
                binding.mainContainer.setRoleDescription(
                    UIUtils.getString(
                        R.string.achieved_badge_with_position,
                        rewards.indexOf(reward).plus(1),
                        rewards.size
                    )
                )
            } else {
                binding.rewardImage.loadFromUrl(reward.offImage)
                binding.checkReward.hide()
                binding.mainContainer.setRoleDescription(
                    UIUtils.getString(
                        R.string.not_achieved_badge_with_position,
                        rewards.indexOf(reward).plus(1),
                        rewards.size
                    )
                )
            }

            dynamicNumber(reward)

            setOnClickListener { listener(reward) }
            binding.descriptionReward.text = reward.title
            binding.mainContainer.contentDescription = reward.title
        }

        private fun dynamicNumber(reward: RewardBadge) {
            if (reward.slug != RewardBadgeEnum.TARGET_HITTER.slug) return
            if (reward.repetitions == 0) return

            binding.numberTV.textColor = if (reward.isEarned) R.color.white else R.color.light_grey_two
            binding.numberTV.text = reward.repetitions.toString()
            binding.numberTV.isVisible = true
        }
    }
}