package com.flipsidegroup.active10.data.persistance.jsonstorage

import android.content.Context
import com.flipsidegroup.active10.data.AboutCommunity
import com.flipsidegroup.active10.data.DependencyLicense
import com.flipsidegroup.active10.data.FaqItem
import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.data.LegalRules
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Type

private const val WALKING_MESSAGES_JSON_FILE_NAME = "walking_messages.json"
private const val ABOUT_YOU_JSON_FILE_NAME = "community.json"
private const val HOW_IT_WORKS_FILE_NAME = "howitworks.json"
private const val MY_GOALS_JSON_FILE_NAME = "mygoals.json"
private const val TIPS_JSON_FILE_NAME = "tips.json"
private const val FAQ_JSON_FILE_NAME = "faq.json"
private const val REWARDS_JSON_FILE_NAME = "rewards.json"
private const val LEGALS_JSON_FILE_NAME = "legals.json"

private const val LICENSES_JSON_FILE_NAME = "licenses/artifacts.json"
private const val LICENSES_VALIDATION_FILE_NAME = "licenses/validation.txt"

class JsonRepositoryImpl(
    private val context: Context,
    private val gson: Gson,
    private val localRepository: LocalRepository,
) : JsonRepository {

    override fun getTips(): Observable<List<Tip>> {
        return Observable.fromCallable(this::readTips)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistTipsContent(it) }
    }

    override fun getAboutCommunity(): Observable<AboutCommunity> {
        return Observable.fromCallable(this::readAboutCommunity)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistAboutCommunity(it) }
    }

    override fun getHowItWorksList(): Observable<List<HowItWorks>> {
        return Observable.fromCallable(this::readHowItWorksList)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistHowItWorksContent(it) }
    }

    override fun getFaqList(): Observable<List<FaqItem>> {
        return Observable.fromCallable(this::readFaqList)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistFaqContent(it) }
    }

    override fun getWalkingMessages(): Observable<WalkingMessageResponse> {
        return Observable.fromCallable(this::readWalkingMessages)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                localRepository.persistWalkingMessages(it)
            }
    }

    override fun getGoalsList(): Observable<List<Goal>> {
        return Observable.fromCallable(this::readGoalsList)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistGoalsContent(it) }
    }

    override fun getRewardBadgesList(): Observable<List<RewardBadge>> {
        return Observable.fromCallable(this::readRewardBadgesList)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistRewardBadges(it) }
    }

    override fun getLegalList(): Observable<List<LegalRules>> {
        return Observable.fromCallable(this::readLegalRules)
            .subscribeOn(Schedulers.io())
            .doOnNext { localRepository.persistLegalRules(it) }
    }

    override fun getLicenses(): Single<List<DependencyLicense>> {
        return Single.fromCallable(this::readLicenses)
            .subscribeOn(Schedulers.io())
    }

    override fun getValidationTextFile(): Single<String> {
        return Single.fromCallable(this::readValidationTextFile)
            .subscribeOn(Schedulers.io())
    }

    private fun <T> readJsonFromFile(fileName: String, typeToken: Type, defaultValue: T): T {
        return try {
            val inputStream = context.assets.open(fileName)
            val json = inputStream.bufferedReader().use { it.readText() }
            gson.fromJson<T>(json, typeToken)
        } catch (e: Exception) {
            Timber.e(e)
            defaultValue
        }
    }

    private fun readTips(): List<Tip> {
        val typeToken = object : TypeToken<List<Tip>>() {}.type
        return readJsonFromFile(TIPS_JSON_FILE_NAME, typeToken, emptyList())
    }

    private fun readHowItWorksList(): List<HowItWorks> {
        val typeToken = object : TypeToken<List<HowItWorks>>() {}.type
        return readJsonFromFile(HOW_IT_WORKS_FILE_NAME, typeToken, emptyList())
    }

    private fun readAboutCommunity(): AboutCommunity {
        val typeToken = AboutCommunity::class.java
        return readJsonFromFile(ABOUT_YOU_JSON_FILE_NAME, typeToken, AboutCommunity())
    }

    private fun readFaqList(): List<FaqItem> {
        val typeToken = object : TypeToken<List<FaqItem>>() {}.type
        return readJsonFromFile(FAQ_JSON_FILE_NAME, typeToken, emptyList())
    }

    private fun readWalkingMessages(): WalkingMessageResponse {
        val typeToken = WalkingMessageResponse::class.java
        return readJsonFromFile(WALKING_MESSAGES_JSON_FILE_NAME, typeToken, WalkingMessageResponse())
    }

    private fun readGoalsList(): List<Goal> {
        val typeToken = object : TypeToken<List<Goal>>() {}.type
        return readJsonFromFile(MY_GOALS_JSON_FILE_NAME, typeToken, emptyList())
    }

    private fun readRewardBadgesList(): List<RewardBadge> {
        val typeToken = object : TypeToken<List<RewardBadge>>() {}.type
        return readJsonFromFile(REWARDS_JSON_FILE_NAME, typeToken, emptyList())
    }

    private fun readLicenses(): List<DependencyLicense> {
        val typeToken = object : TypeToken<List<DependencyLicense>>() {}.type
        return readJsonFromFile(LICENSES_JSON_FILE_NAME, typeToken, emptyList())
    }

    private fun readValidationTextFile(): String {
        return try {
            val inputStream = context.assets.open(LICENSES_VALIDATION_FILE_NAME)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Timber.e(e)
            ""
        }
    }

    private fun readLegalRules(): List<LegalRules> {
        val typeToken = object : TypeToken<ArrayList<LegalRules>>() {}.type
        return readJsonFromFile(LEGALS_JSON_FILE_NAME, typeToken, emptyList())
    }
}
