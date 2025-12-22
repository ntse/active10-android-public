package com.flipsidegroup.active10.presentation.reward.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.databinding.ItemRewardsDialogBinding

class RewardCardAdapter(
    private var listener: (reward: RewardBadge) -> Unit,
) : RecyclerView.Adapter<RewardCardAdapter.CardViewHolder>() {

    private val rewardDiff: ItemCallback<Pair<String, List<RewardBadge>>> =
        object : ItemCallback<Pair<String, List<RewardBadge>>>() {
            override fun areItemsTheSame(
                oldItem: Pair<String, List<RewardBadge>>,
                newItem: Pair<String, List<RewardBadge>>
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: Pair<String, List<RewardBadge>>,
                newItem: Pair<String, List<RewardBadge>>
            ): Boolean {
                return false
            }

        }

    private val differ = AsyncListDiffer(this, rewardDiff)

    fun submitList(list: List<Pair<String, List<RewardBadge>>>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardViewHolder(
        ItemRewardsDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(
            differ.currentList[position].first,
            differ.currentList[position].second
        )
    }

    private fun getTitleByType(type: String) =
        when (type) {
            "goalGetter" -> R.string.goal_getter
            "highAchiever" -> R.string.high_achiever
            "targetChaser" -> R.string.target_chaser
            "briskMinutes" -> R.string.brisk_minutes
            "steppingUp" -> R.string.stepping_up
            else -> R.string.goal_getter
        }

    override fun getItemCount(): Int = differ.currentList.size

    inner class CardViewHolder(
        private val binding: ItemRewardsDialogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headerText: String, list: List<RewardBadge>) {
            binding.rcRewards.layoutManager = GridLayoutManager(binding.rcRewards.context, 3)
            binding.rcRewards.adapter = RewardAdapter(list, listener)
            ViewCompat.setAccessibilityHeading(binding.header, true)
            binding.header.text = binding.root.context.getText(getTitleByType(headerText))
        }
    }
}