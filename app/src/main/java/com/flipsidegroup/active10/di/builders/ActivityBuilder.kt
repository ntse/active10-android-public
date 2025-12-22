package com.flipsidegroup.active10.di.builders

import com.flipsidegroup.active10.presentation.accountCreated.AccountCreatedActivity
import com.flipsidegroup.active10.presentation.accountLoggedOut.AccountLoggedOutActivity
import com.flipsidegroup.active10.presentation.activitylevel.ActivityLevelActivity
import com.flipsidegroup.active10.presentation.authentication.unlockSetup.UnlockSetupInitActivity
import com.flipsidegroup.active10.presentation.authentication.pinAuth.PinAuthActivity
import com.flipsidegroup.active10.presentation.authentication.setPin.SetPinActivity
import com.flipsidegroup.active10.presentation.authentication.settings.QuickUnlockSettingsActivity
import com.flipsidegroup.active10.presentation.authentication.unlockSetup.UnlockSetupSuccessActivity
import com.flipsidegroup.active10.presentation.authentication.sure.AuthSureActivity
import com.flipsidegroup.active10.presentation.authentication.sure.AuthSureSignInActivity
import com.flipsidegroup.active10.presentation.authentication.systemAuth.SystemAuthActivity
import com.flipsidegroup.active10.presentation.authentication.systemAuth.SystemAuthSignInActivity
import com.flipsidegroup.active10.presentation.circularwalk.CircularWalkDetailsActivity
import com.flipsidegroup.active10.presentation.classicuserdetails.ClassicUserDetailsActivity
import com.flipsidegroup.active10.presentation.couch.CouchAdvertActivity
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsActivity
import com.flipsidegroup.active10.presentation.faq.FaqActivity
import com.flipsidegroup.active10.presentation.fulscreenphoto.FullScreenPhotoActivity
import com.flipsidegroup.active10.presentation.goals.activities.GoalsActivity
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.howitworks.activity.HowItWorksActivity
import com.flipsidegroup.active10.presentation.information.InformationActivity
import com.flipsidegroup.active10.presentation.legals.activity.LegalsActivity
import com.flipsidegroup.active10.presentation.licenses.LicensesActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.CustomGoalActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.IntroActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.PermissionActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.UserActivity
import com.flipsidegroup.active10.presentation.pacechecker.PaceCheckerActivity
import com.flipsidegroup.active10.presentation.reward.RewardsActivity
import com.flipsidegroup.active10.presentation.mywalkingplans.activity.MyWalkingPlansActivity
import com.flipsidegroup.active10.presentation.nhsuserdetails.NhsUserDetailsActivity
import com.flipsidegroup.active10.presentation.onboarding.nhs.NhsLoginWebViewActivity
import com.flipsidegroup.active10.presentation.nhsuserdetails.webview.NhsUpdateWebViewActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.TermsAndConditionsActivity
import com.flipsidegroup.active10.presentation.onboarding.nhs.sure.NoConsentSureActivity
import com.flipsidegroup.active10.presentation.progressBar.ProgressBarActivity
import com.flipsidegroup.active10.presentation.signIn.SignInActivity
import com.flipsidegroup.active10.presentation.splash.SplashActivity
import com.flipsidegroup.active10.presentation.stayUpdated.StayUpdatedActivity
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetActivity
import com.flipsidegroup.active10.presentation.tips.activities.TipsActivity
import com.flipsidegroup.active10.presentation.userDetails.activities.UserDetailsActivity
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsActivity
import com.flipsidegroup.active10.presentation.walkingplanstatistics.WalkingPlanStatisticsActivity
import com.flipsidegroup.active10.presentation.walkpresentation.activities.WalkPresentationActivity
import com.flipsidegroup.active10.presentation.walkreminder.WalkReminderActivity
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearActivity
import com.flipsidegroup.active10.presentation.walksneardetails.activity.WalksNearDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindGoalsActivity(): GoalsActivity

    @ContributesAndroidInjector
    abstract fun bindCustomGoalActivity(): CustomGoalActivity

    @ContributesAndroidInjector
    abstract fun bindWalkReminderActivity(): WalkReminderActivity

    @ContributesAndroidInjector
    abstract fun bindFaqActivity(): FaqActivity

    @ContributesAndroidInjector
    abstract fun bindHowItWorksActivity(): HowItWorksActivity

    @ContributesAndroidInjector
    abstract fun bindLegalsActivity(): LegalsActivity

    @ContributesAndroidInjector
    abstract fun bindPermissionActivity(): PermissionActivity

    @ContributesAndroidInjector
    abstract fun bindTipsActivity(): TipsActivity

    @ContributesAndroidInjector
    abstract fun bindTargetActivity(): SetTargetActivity

    @ContributesAndroidInjector
    abstract fun bindGoalUserActivity(): UserActivity

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindIntroActivity(): IntroActivity

    @ContributesAndroidInjector
    abstract fun bindRewardActivity(): RewardsActivity

    @ContributesAndroidInjector
    abstract fun bindDiscoverDetailsActivity(): DiscoverDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindWalkPresentationActivity(): WalkPresentationActivity

    @ContributesAndroidInjector
    abstract fun bindUserDetailsActivity(): UserDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerActivity(): PaceCheckerActivity

    @ContributesAndroidInjector
    abstract fun bindStayUpdatedActivity(): StayUpdatedActivity

    @ContributesAndroidInjector
    abstract fun bindClassicUserDetailsActivity(): ClassicUserDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindNhsUserDetailsActivity(): NhsUserDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindSignInActivity(): SignInActivity

    @ContributesAndroidInjector
    abstract fun bindProgressBarActivity(): ProgressBarActivity

    @ContributesAndroidInjector
    abstract fun bindNhsLoginWebViewActivity(): NhsLoginWebViewActivity

    @ContributesAndroidInjector
    abstract fun bindNhsUpdateWebViewActivity(): NhsUpdateWebViewActivity

    @ContributesAndroidInjector
    abstract fun bindInformationActivity(): InformationActivity

    @ContributesAndroidInjector
    abstract fun bindMyWalkingPlansActivity(): MyWalkingPlansActivity

    @ContributesAndroidInjector
    abstract fun bindWalkingPlanDetailsActivity(): WalkingPlanDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindWalkingPlanStatisticsActivity(): WalkingPlanStatisticsActivity

    @ContributesAndroidInjector
    abstract fun bindWalksNearActivity(): WalksNearActivity

    @ContributesAndroidInjector
    abstract fun bindWalkNearDetailsActivity(): WalksNearDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindFullScreenPhotoActivity(): FullScreenPhotoActivity

    @ContributesAndroidInjector
    abstract fun bindCouchAdvertActivity(): CouchAdvertActivity

    @ContributesAndroidInjector
    abstract fun bindCircularWalkDetailsActivity(): CircularWalkDetailsActivity

    @ContributesAndroidInjector
    abstract fun bindActivityLevelActivity(): ActivityLevelActivity

    @ContributesAndroidInjector
    abstract fun bindLicensesActivity(): LicensesActivity

    @ContributesAndroidInjector
    abstract fun bindAccountCreatedActivity(): AccountCreatedActivity

    @ContributesAndroidInjector
    abstract fun bindAccountLoggedOutActivity(): AccountLoggedOutActivity

    @ContributesAndroidInjector
    abstract fun bindUnlockSetupInitActivity(): UnlockSetupInitActivity

    @ContributesAndroidInjector
    abstract fun bindUnlockSetupSuccessActivity(): UnlockSetupSuccessActivity

    @ContributesAndroidInjector
    abstract fun bindSetPinActivity(): SetPinActivity

    @ContributesAndroidInjector
    abstract fun bindAuthSureActivity(): AuthSureActivity

    @ContributesAndroidInjector
    abstract fun bindAuthSureSignInActivity(): AuthSureSignInActivity

    @ContributesAndroidInjector
    abstract fun bindPinUnlockActivity(): PinAuthActivity

    @ContributesAndroidInjector
    abstract fun bindSystemAuthActivity(): SystemAuthActivity

    @ContributesAndroidInjector
    abstract fun bindSystemAuthSignInActivity(): SystemAuthSignInActivity

    @ContributesAndroidInjector
    abstract fun bindQuickUnlockSettingsActivity(): QuickUnlockSettingsActivity

    @ContributesAndroidInjector
    abstract fun bindTermsAndConditionsActivity(): TermsAndConditionsActivity

    @ContributesAndroidInjector
    abstract fun bindNoConsentSureActivity(): NoConsentSureActivity

}
