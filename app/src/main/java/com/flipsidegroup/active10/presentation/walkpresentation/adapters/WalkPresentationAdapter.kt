package com.flipsidegroup.active10.presentation.walkpresentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.databinding.ItemBriskWalkInfoBinding
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.UIUtils

class WalkPresentationAdapter(
    private val periodType: PeriodTypeEnum,
    private val steps: MutableList<StepOverview>
) : RecyclerView.Adapter<WalkPresentationAdapter.WalkPresentationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkPresentationViewHolder {
        return WalkPresentationViewHolder(
            ItemBriskWalkInfoBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: WalkPresentationViewHolder, position: Int) {
        holder.bind(steps[position], periodType)
    }

    override fun getItemCount(): Int = steps.size

    class WalkPresentationViewHolder(
        private val binding: ItemBriskWalkInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: StepOverview, periodType: PeriodTypeEnum) {
            var date = ""
            step.timestamp?.let {
                date = if (periodType == PeriodTypeEnum.WEEKS) {
                    if (DateHelper.isSameDay(it)) {
                        UIUtils.getString(R.string.today)
                    } else {
                        DateHelper.getDayOfWeekFullName(it)
                    }
                } else {
                    DateHelper.formatDayMonth(it)
                }

                binding.walkDayTv.text = date
            }

            binding.briskTv.text = step.totalBriskMin.toString()
            binding.root.contentDescription =
                UIUtils.getString(R.string.day_brisk_minutes, date, step.totalBriskMin ?: 0)
        }
    }
}
