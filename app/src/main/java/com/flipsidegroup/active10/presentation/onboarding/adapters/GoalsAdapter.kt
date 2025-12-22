package com.flipsidegroup.active10.presentation.onboarding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.databinding.ItemGoalBinding
import com.flipsidegroup.active10.utils.UIUtils.getString
import com.flipsidegroup.active10.utils.diffUtils.GoalsDiffUtil


class GoalsAdapter(
    private val selectedGoals: List<Goal>?,
    var goalList: List<Goal>,
    private val onGoalChangeListener: (goalsList: List<Goal>) -> (Unit)
) : RecyclerView.Adapter<GoalsAdapter.GoalVH>() {

    private val goalsHolder: ArrayList<GoalItemHolder> = ArrayList()

    init {
        populateHolders()
    }

    fun updateGoals(list: List<Goal>) {
        val diffResult = DiffUtil.calculateDiff(GoalsDiffUtil(goalList, list), false)
        goalList = list
        populateHolders()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalVH {
        return GoalVH(
            ItemGoalBinding.inflate(
                LayoutInflater.from(parent.context), parent, false,
            )
        )
    }

    override fun getItemCount(): Int = goalsHolder.size

    override fun onBindViewHolder(holder: GoalVH, position: Int) {
        val goal = goalsHolder[position]
        holder.bind(goal)
    }

    private fun populateHolders() {
        goalsHolder.clear()
        goalList.forEach { goal ->
            goalsHolder.add(GoalItemHolder(goal, goal.isSelected))
        }
    }

    inner class GoalVH(
        private val binding: ItemGoalBinding,
    ) : RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {

        internal fun bind(goalItemHolder: GoalItemHolder) {
            binding.goalCheckboxView.text = goalItemHolder.goal.goal
            binding.goalCheckboxView.isChecked = goalItemHolder.isSelected
            binding.goalCheckboxView.setOnCheckedChangeListener(this)

            if (goalItemHolder.goal.isCustomGoal) {
                binding.goalCheckboxView.contentDescription =
                    getString(R.string.opens_in_new_window, goalItemHolder.goal.goal)
            } else {
                binding.goalCheckboxView.contentDescription = null
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            val clickedItem = goalsHolder[adapterPosition]
            clickedItem.isSelected = isChecked
            clickedItem.goal.isSelected = isChecked

            val goalIndex = goalList.indexOf(clickedItem.goal)
            if (goalIndex >= 0) {
                goalList[goalIndex].isSelected = isChecked
            }

            onGoalChangeListener.invoke(goalList)
        }
    }

    data class GoalItemHolder(
        val goal: Goal,
        var isSelected: Boolean
    )
}
