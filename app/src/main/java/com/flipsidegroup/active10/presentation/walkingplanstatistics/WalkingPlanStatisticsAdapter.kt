package com.flipsidegroup.active10.presentation.walkingplanstatistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ItemFuncInfoActionBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanAdviceItemBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanPrimaryButtonSmallBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanSecondaryButtonSmallBinding
import com.flipsidegroup.active10.databinding.ItemWalkingPlanStatisticsBinding
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.phe.betterhealth.widgets.common.setHtml

class WalkingPlanStatisticsAdapter :
    ListAdapter<WalkingPlanStatisticsPart, RecyclerView.ViewHolder>(Differ) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (WalkingPlanStatisticsType.entries[viewType]) {
            WalkingPlanStatisticsType.STATISTICS -> StatisticsViewHolder(
                ItemWalkingPlanStatisticsBinding.inflate(inflater, parent, false)
            )

            WalkingPlanStatisticsType.PRIMARY_BUTTON -> PrimaryButtonViewHolder(
                ItemWalkingPlanPrimaryButtonSmallBinding.inflate(inflater, parent, false)
            )

            WalkingPlanStatisticsType.SECONDARY_BUTTON -> SecondaryButtonViewHolder(
                ItemWalkingPlanSecondaryButtonSmallBinding.inflate(inflater, parent, false)
            )

            WalkingPlanStatisticsType.ADVICE_ITEM -> AdviceItemViewHolder(
                ItemWalkingPlanAdviceItemBinding.inflate(inflater, parent, false)
            )

            WalkingPlanStatisticsType.FUNC_ITEM -> FuncItemViewHolder(
                ItemFuncInfoActionBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StatisticsViewHolder -> holder.bind(getItem(position) as WalkingPlanStatisticsPart.Statistics)
            is PrimaryButtonViewHolder -> holder.bind(getItem(position) as WalkingPlanStatisticsPart.PrimaryButton)
            is SecondaryButtonViewHolder -> holder.bind(getItem(position) as WalkingPlanStatisticsPart.SecondaryButton)
            is AdviceItemViewHolder -> holder.bind(getItem(position) as WalkingPlanStatisticsPart.AdviceItem)
            is FuncItemViewHolder -> holder.bind(getItem(position) as WalkingPlanStatisticsPart.FuncItem)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            onBindViewHolder(holder, position)
        } else super.onBindViewHolder(holder, position, payloads)
    }

    private class StatisticsViewHolder(
        private val binding: ItemWalkingPlanStatisticsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val grey = UIUtils.getColor(R.color.light_grey_two)
        private val black = UIUtils.getColor(R.color.black_33)
        private var tabPosition = 0
        private var totalWeeksCount = 0
        private var selectedWeekIndex = -1
        lateinit var tabListener: TabLayout.OnTabSelectedListener

        fun bind(item: WalkingPlanStatisticsPart.Statistics) {
            with(binding) {
                content = item
                tabPosition = tabLayout.selectedTabPosition
                if (selectedWeekIndex == -1 || item.showCurrentWeek) selectedWeekIndex = item.currentWeekIndex
                totalWeeksCount = item.totalWeeks.count()

                totalDaysCounter.setHtml(
                    "Day <b>${item.currentDayIndex + 1}</b> of <b>${item.totalDays}</b>"
                )
                nextButton.setOnClickListener {
                    selectedWeekIndex += if (selectedWeekIndex in 0..<(totalWeeksCount-1)) 1 else 0
                    setMaskOfState(item)
                }
                prevButton.setOnClickListener {
                    selectedWeekIndex += if (selectedWeekIndex in 1..<totalWeeksCount) -1 else 0
                    setMaskOfState(item)
                }

                setMaskOfState(item)
            }
        }

        private fun setMaskOfState(item: WalkingPlanStatisticsPart.Statistics) {
            when (item.state) {
                WalkingPlanState.PAUSED -> {
                    setOfCases(item)
                    setStatisticsColorForState(R.color.light_grey_two, R.color.light_grey)
                }
                else -> {
                    setStatisticsColorForState(R.color.black_33, R.color.black)
                    setOfCases(item)
                }
            }
            binding.executePendingBindings()
        }

        private fun setOfCases(item: WalkingPlanStatisticsPart.Statistics) {
            setUpTabListener(item)
            setUpWalkType(item.totalWeeks[selectedWeekIndex])
            checkNextPrevBtn(item)
            checkNoNonBriskTargetToday(item)
            checkNoTargetsToday(item)
            checkIsEmptyWalk(item.totalWeeks[selectedWeekIndex])
        }

        private fun checkNoNonBriskTargetToday(item: WalkingPlanStatisticsPart.Statistics) {
            with(binding) {
                if (item.totalDayNonBriskMin == 0) {
                    nonBriskTitle.setTextColor(grey)
                    nonBriskCounter.setTextColor(grey)
                } else {
                    nonBriskTitle.setTextColor(black)
                    nonBriskCounter.setTextColor(black)
                }
            }
        }

        private fun setUpTabListener(item: WalkingPlanStatisticsPart.Statistics) {
            with(binding) {
                if (this@StatisticsViewHolder::tabListener.isInitialized) {
                    tabLayout.removeOnTabSelectedListener(tabListener)
                }
                tabListener = object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            tabPosition = tab.position
                            setMaskOfState(item)
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
                tabLayout.addOnTabSelectedListener(tabListener)
            }
        }

        private fun checkNextPrevBtn(item: WalkingPlanStatisticsPart.Statistics) {
            val isBriskTargetReached = item.totalWeeks[selectedWeekIndex].briskData.let {
                it.currentWeekMinutes >= it.totalWeekMinutes
            }

            with(binding) {
                nextButton.isVisible = selectedWeekIndex in 0..<(totalWeeksCount-1)
                prevButton.isVisible = selectedWeekIndex in 1..<totalWeeksCount

                if (selectedWeekIndex < (item.currentWeekIndex)
                    || (isBriskTargetReached && item.currentDayIndex == 6)) {
                    nextButton.setTextColor(black)
                    nextButton.setIconTintResource(R.color.black_33)
                    nextButton.isClickable = true
                } else {
                    nextButton.setTextColor(grey)
                    nextButton.setIconTintResource(R.color.light_grey)
                    nextButton.isClickable = false
                }
            }
        }

        private fun setUpWalkType(item: StatisticsWeek) {
            with(binding) {
                if (tabPosition == 0) {
                    nonBriskWeeklyTarget.isInvisible = true
                    currentProgressTV.text = "Brisk walking total"
                    currentProgressTV.isInvisible = false
                    currentProgressValueTV.isInvisible = false
                    maxProgressTV.text = "Brisk walking target"
                    maxProgressTV.isInvisible = false
                    maxProgressValueTV.isInvisible = false
                    setUpWalk(item.briskData)
                } else {
                    nonBriskWeeklyTarget.isInvisible = item.nonBriskData.totalWeekMinutes > 0
                    currentProgressTV.text = "Non-brisk walking total"
                    currentProgressTV.isInvisible = item.nonBriskData.totalWeekMinutes == 0
                    currentProgressValueTV.isInvisible = item.nonBriskData.totalWeekMinutes == 0
                    maxProgressTV.text = "Non-brisk walking target"
                    maxProgressTV.isInvisible = item.nonBriskData.totalWeekMinutes == 0
                    maxProgressValueTV.isInvisible = item.nonBriskData.totalWeekMinutes == 0
                    setUpWalk(item.nonBriskData)
                }
            }
        }

        private fun setUpWalk(item: StatisticsInfo) {
            with(binding) {
                currentProgressValueTV.text = "${item.currentWeekMinutes} MINS"
                maxProgressValueTV.text = "${item.totalWeekMinutes} MINS"

                myWalkProgress.setUpMyWalks()
                myWalkProgress.setMaximumProgress(item.totalWeekMinutes)
                myWalkProgress.updateProgress(
                    if (item.currentWeekMinutes > item.totalWeekMinutes)
                        item.totalWeekMinutes
                    else item.currentWeekMinutes
                )

                val briskMinutesString =
                    UIUtils.getQuantityString(
                        R.plurals.accessibility_my_walks_horse_shoe_plural,
                        item.currentWeekMinutes
                    )
                val totalMinutesString =
                    UIUtils.getQuantityString(
                        R.plurals.accessibility_my_walks_horse_shoe_plural,
                        item.totalWeekMinutes
                    )
                myWalkProgress.contentDescription = UIUtils.getString(
                    R.string.accessibility_my_walks_horse_shoe,
                    totalMinutesString,
                    briskMinutesString
                )

                noBriskMessageTv.isVisible = false
                currentProgressTV.setTextColor(UIUtils.getColor(R.color.black))
                maxProgressTV.setTextColor(UIUtils.getColor(R.color.black))

                totalWeeksCounter.setHtml(
                    "Week <b>${selectedWeekIndex + 1}</b> of <b>${totalWeeksCount}</b>"
                )
            }
        }

        private fun checkNoTargetsToday(item: WalkingPlanStatisticsPart.Statistics) {
            with(binding) {
                if (item.totalDayBriskMin == 0) {
                    dayProgressDetailsContainer.visibility = View.INVISIBLE
                    dayNoTargetsContainer.visibility = View.VISIBLE
                } else {
                    dayProgressDetailsContainer.visibility = View.VISIBLE
                    dayNoTargetsContainer.visibility = View.INVISIBLE
                }
            }
        }

        private fun checkIsEmptyWalk(item: StatisticsWeek) {
            with(binding) {
                if (item.briskData.currentWeekMinutes <= 0
                    && item.nonBriskData.currentWeekMinutes <= 0) {
                    myWalkProgress.setUpEmptyWalkingPlan()
                    noBriskMessageTv.isVisible = true
                }
            }
        }

        fun setStatisticsColorForState(@ColorRes textColorRes: Int, @ColorRes iconColorRes: Int) {
            with(binding) {
                totalWeeksCounter.setTextColor(UIUtils.getColor(textColorRes))
                tabLayout.setTabTextColors(
                    UIUtils.getColor(R.color.light_grey_two),
                    UIUtils.getColor(textColorRes),
                )
                tabLayout.setSelectedTabIndicatorColor(UIUtils.getColor(iconColorRes))
                currentProgressTV.setTextColor(UIUtils.getColor(textColorRes))
                currentProgressValueTV.setTextColor(UIUtils.getColor(textColorRes))
                maxProgressTV.setTextColor(UIUtils.getColor(textColorRes))
                maxProgressValueTV.setTextColor(UIUtils.getColor(textColorRes))
                nextButton.setTextColor(UIUtils.getColor(textColorRes))
                nextButton.setIconTintResource(iconColorRes)
                prevButton.setTextColor(UIUtils.getColor(textColorRes))
                prevButton.setIconTintResource(iconColorRes)
                totalDaysTitle.setTextColor(UIUtils.getColor(textColorRes))
                totalDaysCounter.setTextColor(UIUtils.getColor(textColorRes))
                briskCounter.setTextColor(UIUtils.getColor(textColorRes))
                briskTitle.setTextColor(UIUtils.getColor(textColorRes))
                nonBriskCounter.setTextColor(UIUtils.getColor(textColorRes))
                nonBriskTitle.setTextColor(UIUtils.getColor(textColorRes))
                nonBriskWeeklyTarget.setTextColor(UIUtils.getColor(textColorRes))
                dayNoTargetsText1.setTextColor(UIUtils.getColor(textColorRes))
                dayNoTargetsText2.setTextColor(UIUtils.getColor(textColorRes))
            }
        }
    }

    private class PrimaryButtonViewHolder(
        private val binding: ItemWalkingPlanPrimaryButtonSmallBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanStatisticsPart.PrimaryButton) {
            with(binding) {
                (button as MaterialButton).icon = item.icon?.let {
                    ContextCompat.getDrawable(
                        root.context,
                        it
                    )
                }
                button.text = item.title
                button.setOnClickListenerWithDebounce { item.onClickCallback() }
            }
        }
    }


    private class SecondaryButtonViewHolder(
        private val binding: ItemWalkingPlanSecondaryButtonSmallBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanStatisticsPart.SecondaryButton) {
            with(binding) {
                button.text = item.title
                button.setOnClickListenerWithDebounce { item.onClickCallback() }
            }
        }
    }

    private class AdviceItemViewHolder(
        private val binding: ItemWalkingPlanAdviceItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanStatisticsPart.AdviceItem) {
            with(binding) {
                content = item
                executePendingBindings()
            }
        }
    }

    private class FuncItemViewHolder(
        private val binding: ItemFuncInfoActionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkingPlanStatisticsPart.FuncItem) {
            with(binding) {
                title.text = item.title
                description.setHtml(item.description)
                button.text = item.actionTitle
                image.loadFromUrl(item.image)

                button.setOnClickListenerWithDebounce { item.onClickCallback(item.actionSlug) }
            }
        }
    }

    private object Differ : DiffUtil.ItemCallback<WalkingPlanStatisticsPart>() {
        override fun areItemsTheSame(
            oldItem: WalkingPlanStatisticsPart,
            newItem: WalkingPlanStatisticsPart
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WalkingPlanStatisticsPart,
            newItem: WalkingPlanStatisticsPart
        ): Boolean = oldItem == newItem

        override fun getChangePayload(
            oldItem: WalkingPlanStatisticsPart,
            newItem: WalkingPlanStatisticsPart
        ): Any = true
    }
}