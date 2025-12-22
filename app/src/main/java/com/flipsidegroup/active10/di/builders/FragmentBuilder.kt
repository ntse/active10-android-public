package com.flipsidegroup.active10.di.builders

import com.flipsidegroup.active10.presentation.authentication.settings.QuickUnlockSettingsDialog
import com.flipsidegroup.active10.presentation.dialogs.NhsLogBackInDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanDebugDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanTripDialog
import com.flipsidegroup.active10.presentation.discover.fragments.DiscoverFragment
import com.flipsidegroup.active10.presentation.goals.fragments.GoalsFragment
import com.flipsidegroup.active10.presentation.home.dialog.DeepLinkDialog
import com.flipsidegroup.active10.presentation.mentalhealth.mood.MentalHealthMoodActivity
import com.flipsidegroup.active10.presentation.mywalks.MyWalksFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.IntroFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.PermissionFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.TermsAndConditionsFragment
import com.flipsidegroup.active10.presentation.onboarding.fragments.WhereDoYouLiveFragment
import com.flipsidegroup.active10.presentation.pacechecker.exit.PaceCheckerExitDialog
import com.flipsidegroup.active10.presentation.pacechecker.explore.PaceCheckerExploreDialog
import com.flipsidegroup.active10.presentation.pacechecker.intro.PaceCheckerIntroDialog
import com.flipsidegroup.active10.presentation.pacechecker.pause.PaceCheckerPauseDialog
import com.flipsidegroup.active10.presentation.pacechecker.result.PaceCheckerResultFragment
import com.flipsidegroup.active10.presentation.pacechecker.start.PaceCheckerStartFragment
import com.flipsidegroup.active10.presentation.pacechecker.timer.PaceCheckerTimerFragment
import com.flipsidegroup.active10.presentation.pacechecker.useful.PaceCheckerUsefulDialog
import com.flipsidegroup.active10.presentation.settings.fragments.SettingsFragment
import com.flipsidegroup.active10.presentation.signIn.SignInMainFragment
import com.flipsidegroup.active10.presentation.signIn.SignInInfoFragment
import com.flipsidegroup.active10.presentation.stayUpdated.StayUpdatedFragment
import com.flipsidegroup.active10.presentation.todaywalk.TodayWalkFragment
import com.flipsidegroup.active10.presentation.todaywalk.TodayWalkPermissionFragment
import com.flipsidegroup.active10.presentation.userDetails.fragments.UserDetailsFragment
import com.flipsidegroup.active10.presentation.walksneardetails.dialog.GoJauntlyDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun bindDiscoverFragment(): DiscoverFragment

    @ContributesAndroidInjector
    abstract fun bindTodayWalkFragment(): TodayWalkFragment

    @ContributesAndroidInjector
    abstract fun bindGoalsFragment(): GoalsFragment

    @ContributesAndroidInjector
    abstract fun bindMyWalksFragment(): MyWalksFragment

    @ContributesAndroidInjector
    abstract fun bindTodayWalkPermissionFragment(): TodayWalkPermissionFragment

    @ContributesAndroidInjector
    abstract fun bindIntroFragment(): IntroFragment

    @ContributesAndroidInjector
    abstract fun bindPermissionFragment(): PermissionFragment

    @ContributesAndroidInjector
    abstract fun bindTermsAndConditionsFragment(): TermsAndConditionsFragment

    @ContributesAndroidInjector
    abstract fun bindWhereDoYouLiveFragment(): WhereDoYouLiveFragment

    @ContributesAndroidInjector
    abstract fun bindUserDetailsFragment(): UserDetailsFragment

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerStartFragment(): PaceCheckerStartFragment

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerTimerFragment(): PaceCheckerTimerFragment

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerResultFragment(): PaceCheckerResultFragment

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerExitDialog(): PaceCheckerExitDialog

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerUsefulDialog(): PaceCheckerUsefulDialog

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerExplore(): PaceCheckerExploreDialog

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerPauseDialog(): PaceCheckerPauseDialog

    @ContributesAndroidInjector
    abstract fun bindPaceCheckerIntroDialog(): PaceCheckerIntroDialog

    @ContributesAndroidInjector
    abstract fun bindMentalHealthMoodFragment(): MentalHealthMoodActivity

    @ContributesAndroidInjector
    abstract fun bindDeepLinkDialog(): DeepLinkDialog

    @ContributesAndroidInjector
    abstract fun bindStayUpdatedFragment(): StayUpdatedFragment

    @ContributesAndroidInjector
    abstract fun bindSignInMainFragment(): SignInMainFragment

    @ContributesAndroidInjector
    abstract fun bindSignInInfoFragment(): SignInInfoFragment

    @ContributesAndroidInjector
    abstract fun bindWalkingPlanCommonDialog(): WalkingPlanCommonDialog

    @ContributesAndroidInjector
    abstract fun bindWalkingPlanDebugDialog(): WalkingPlanDebugDialog

    @ContributesAndroidInjector
    abstract fun bindWalkingPlanTripDialog(): WalkingPlanTripDialog

    @ContributesAndroidInjector
    abstract fun bindNhsLogBackInDialog(): NhsLogBackInDialog

    @ContributesAndroidInjector
    abstract fun bindGoJauntlyDialog(): GoJauntlyDialog

    @ContributesAndroidInjector
    abstract fun bindQuickUnlockSettingsDialog(): QuickUnlockSettingsDialog

}
