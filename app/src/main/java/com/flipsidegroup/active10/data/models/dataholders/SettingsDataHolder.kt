package com.flipsidegroup.active10.data.models.dataholders

import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.data.models.requests.UserGoalRequest
import com.google.gson.annotations.SerializedName
import java.util.*



data class SettingsDataHolder(

    @SerializedName("a")
    var goalsList: List<Goal>? = null,

    @SerializedName("b")
    var goalReason: String? = null,

    @SerializedName("c")
    var targetList: ArrayList<TargetHolder>? = null,

    @SerializedName("d")
    var isBriskReminderSet: Boolean? = null,

    @SerializedName("e")
    var isLocationEnabled: Boolean? = null,

    @SerializedName("f")
    var isFitnessMotionEnabled: Boolean? = null,

    @SerializedName("g")
    var briskReminderTimestamp: Long? = null,

    @SerializedName("h")
    var resetTimestamp: Long? = null,

    @SerializedName("i")
    var firstTimeInApp: Boolean? = null,

    var generateRandomWalkData: Boolean? = null,

    var generate1Active10WalkData: Boolean? = null,

    var generate5Active10WalkData: Boolean? = null,

    @SerializedName("k")
    var deviceId: String? = null,

    @SerializedName("m")
    var lastTimeInApp: Long? = null,

    @SerializedName("n")
    var notificationMin: Boolean? = null,

    @SerializedName("o")
    var lapsedCount: Int? = null,

    @SerializedName("p")
    var nextOnboardingNotification: Int? = null,

    @SerializedName("q")
    var deviceLocationHolder: DeviceLocationHolder? = null,

    @SerializedName("r")
    var totalBriskMinutes: Int? = null,

    @SerializedName("s")
    var lastSaved: Date? = null,

    @SerializedName("t")
    var isDeviceRegistered: Boolean? = null,

    @SerializedName("u")
    var lastSavedTarget: Date? = null,

    @SerializedName("v")
    var showSetTarget: Boolean? = null,

    @SerializedName("w")
    var isMigrationDataSent: Boolean? = null,

    @SerializedName("x")
    var lastMyWalksSavedTimestamp: Long? = null,

    @SerializedName("y")
    var lastReviewShownTimestamp: Long? = null,

    @SerializedName("z")
    var hasLeftReview: Boolean? = null,

    @SerializedName("A")
    var isJsonContentPersisted: Boolean? = null,

    @SerializedName("B")
    var lastCMSRefresh: Long? = null,

    @SerializedName("C")
    var useProdCMS: Boolean? = null,

    @SerializedName("D")
    var recoverDataLastTimestamp: Long? = null,

    @SerializedName("E")
    var recoverDataLastTimeAccessed: Long? = null,

    @SerializedName("F")
    var versionCode: Int? = null,

    @SerializedName("G")
    var termsAndConditionsVersion: Int? = null,

    @SerializedName("H")
    var myWalksTimestamp: Long? = null,

    @SerializedName("I")
    var appUpdateLastTimestamp: Long? = null,

    @SerializedName("J")
    var wasAskedMotionPermission: Boolean? = null,

    @SerializedName("K")
    var rewardBadgesStartTimestamp: Long? = null,

    @SerializedName("L")
    var earnedBadges: ArrayList<EarnRewardBadge>? = null,

    @SerializedName("M")
    var readArticles: ArrayList<Int>? = null,

    @SerializedName("N")
    var shouldShowRewardOnboarding: Boolean? = null,

    @SerializedName("O")
    var shouldCheckForLostData: Boolean? = null,

    @SerializedName("P")
    var showMissingAlertSooner: Boolean? = null,

    @SerializedName("Q")
    var hadLostData: Boolean? = null,

    @SerializedName("R")
    var nrOfRetryLostDada: Int? = null,

    @SerializedName("S")
    var lastRetryLostData: Long? = null,

    @SerializedName("T")
    var shouldSeeRetrieveErrorAlerted: Boolean? = null,

    @SerializedName("U")
    var shouldFailRetrieveDataTest: Boolean? = null,

    @SerializedName("X")
    var areUserDetailsSet: Boolean? = null,

    @SerializedName("Y")
    var isOnboardingFinished: Boolean? = null,

    var isWidgetIntroduceShown: Boolean? = null,

    var heroPopupsShown: List<String>? = null,

    var campaignBannersShown: List<String>? = null,

    var shouldShowUpdateAppDialog: Boolean? = null,

    var shouldShowRetrieveDataDialog: Boolean? = null,

    var shouldShowUpdateTermsDialog: Boolean? = null,

    var highAchieversLastShowDate: Long? = null,

    var highAchieversDoNotAskMeAgain: Boolean? = null,

    var earnedHighAchieversBadgeTarget: Int? = null,

    var classicUser: ClassicUser? = null,

    var nhsUser: NhsUserDetails? = null,

    var nhsToken: String? = null,

    var nhsLastSyncTime: Long? = null,

    var nhsLastSyncUserId: String? = null,
)
