package com.flipsidegroup.active10.presentation.mywalks.presenter

import android.content.Context
import com.flipsidegroup.active10.data.IntervalWalk
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.mywalks.view.MyWalksView

interface MyWalksPresenter : LifecycleAwarePresenter<MyWalksView> {

    fun getDaysIntervals(context: Context?)

    fun getWeeksIntervals(context: Context?)

    fun getMonthsIntervals(context: Context?)

    fun getPeriodRewards(currentPeriodType: PeriodTypeEnum, interval: IntervalWalk)

    fun getTodayRewards(briskWalk: Int, totalWalk: Int)

    fun registerDataListener()

    fun unregisterDataListener()

    fun getWalkingMessages(targetHit: Int, briskWalk: Int, active10: Int)

    fun getGlobalRules()

    fun getAllRewardsList()

    fun updateCurrentPeriod(period: PeriodTypeEnum)

    fun goToArticle150()

    fun getMyWalksChildrenItems()
}