package com.flipsidegroup.active10.data.persistance.jsonstorage

import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.Goal
import io.reactivex.Observable
import io.reactivex.Single


interface JsonRepository {

    fun getAboutCommunity(): Observable<AboutCommunity>

    fun getTips(): Observable<List<Tip>>

    fun getFaqList(): Observable<List<FaqItem>>

    fun getHowItWorksList(): Observable<List<HowItWorks>>

    fun getWalkingMessages(): Observable<WalkingMessageResponse>

    fun getGoalsList(): Observable<List<Goal>>

    fun getRewardBadgesList(): Observable<List<RewardBadge>>

    fun getLegalList(): Observable<List<LegalRules>>

    fun getLicenses(): Single<List<DependencyLicense>>

    fun getValidationTextFile(): Single<String>
}