package com.flipsidegroup.active10.di.modules

import android.content.Context
import com.flipsidegroup.active10.data.persistance.jsonstorage.JsonRepository
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.NhsSyncRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.persistance.walksnear.WalksNearRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.activitylevel.ActivityLevelPresenter
import com.flipsidegroup.active10.presentation.activitylevel.ActivityLevelPresenterImpl
import com.flipsidegroup.active10.presentation.authentication.pinAuth.PinAuthPresenter
import com.flipsidegroup.active10.presentation.authentication.pinAuth.PinAuthPresenterImpl
import com.flipsidegroup.active10.presentation.authentication.systemAuth.SystemAuthPresenter
import com.flipsidegroup.active10.presentation.authentication.systemAuth.SystemAuthPresenterImpl
import com.flipsidegroup.active10.presentation.circularwalk.CircularWalkDetailsPresenter
import com.flipsidegroup.active10.presentation.circularwalk.CircularWalkDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.classicuserdetails.ClassicUserDetailsPresenter
import com.flipsidegroup.active10.presentation.classicuserdetails.ClassicUserDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.couch.CouchAdvertPresenter
import com.flipsidegroup.active10.presentation.couch.CouchAdvertPresenterImpl
import com.flipsidegroup.active10.presentation.discover.presenter.DiscoverPresenter
import com.flipsidegroup.active10.presentation.discover.presenter.DiscoverPresenterImpl
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsPresenter
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.faq.FaqPresenter
import com.flipsidegroup.active10.presentation.faq.FaqPresenterImpl
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsActivityPresenter
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsActivityPresenterImpl
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsFragmentPresenter
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsFragmentPresenterImpl
import com.flipsidegroup.active10.presentation.home.presenter.DeepLinkPresenter
import com.flipsidegroup.active10.presentation.home.presenter.HomePresenter
import com.flipsidegroup.active10.presentation.home.presenter.HomePresenterImpl
import com.flipsidegroup.active10.presentation.howitworks.presenter.HowItWorksPresenter
import com.flipsidegroup.active10.presentation.howitworks.presenter.HowItWorksPresenterImpl
import com.flipsidegroup.active10.presentation.information.InformationPresenter
import com.flipsidegroup.active10.presentation.legals.presenter.LegalsPresenter
import com.flipsidegroup.active10.presentation.legals.presenter.LegalsPresenterImpl
import com.flipsidegroup.active10.presentation.licenses.LicensesContract
import com.flipsidegroup.active10.presentation.licenses.LicensesPresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.presenter.MyWalkingPlansPresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.presenter.MyWalkingPlansPresenterImpl
import com.flipsidegroup.active10.presentation.mywalks.presenter.MyWalkPresenterImpl
import com.flipsidegroup.active10.presentation.mywalks.presenter.MyWalksPresenter
import com.flipsidegroup.active10.presentation.nhsuserdetails.NhsUserDetailsPresenter
import com.flipsidegroup.active10.presentation.nhsuserdetails.NhsUserDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.nhsuserdetails.webview.NhsUpdateWebViewPresenter
import com.flipsidegroup.active10.presentation.onboarding.nhs.NhsLoginWebViewPresenter
import com.flipsidegroup.active10.presentation.onboarding.presenter.PermissionPresenter
import com.flipsidegroup.active10.presentation.onboarding.presenter.PermissionPresenterImpl
import com.flipsidegroup.active10.presentation.onboarding.presenter.TermsAndConditionsPresenter
import com.flipsidegroup.active10.presentation.onboarding.presenter.TermsAndConditionsPresenterImpl
import com.flipsidegroup.active10.presentation.onboarding.presenter.WhereDoYouLivePresenter
import com.flipsidegroup.active10.presentation.onboarding.presenter.WhereDoYouLivePresenterImpl
import com.flipsidegroup.active10.presentation.progressBar.ProgressBarPresenter
import com.flipsidegroup.active10.presentation.progressBar.ProgressBarPresenterImpl
import com.flipsidegroup.active10.presentation.reward.presenter.RewardsPresenter
import com.flipsidegroup.active10.presentation.reward.presenter.RewardsPresenterImpl
import com.flipsidegroup.active10.presentation.settings.presenter.SettingsPresenter
import com.flipsidegroup.active10.presentation.settings.presenter.SettingsPresenterImpl
import com.flipsidegroup.active10.presentation.signIn.SignInPresenter
import com.flipsidegroup.active10.presentation.signIn.SignInPresenterImpl
import com.flipsidegroup.active10.presentation.splash.presenter.SplashPresenter
import com.flipsidegroup.active10.presentation.splash.presenter.SplashPresenterImpl
import com.flipsidegroup.active10.presentation.stayUpdated.presenter.StayUpdatedPresenter
import com.flipsidegroup.active10.presentation.stayUpdated.presenter.StayUpdatedPresenterImpl
import com.flipsidegroup.active10.presentation.targets.presenter.SetTargetPresenter
import com.flipsidegroup.active10.presentation.targets.presenter.SetTargetPresenterImpl
import com.flipsidegroup.active10.presentation.tips.presenter.TipsPresenter
import com.flipsidegroup.active10.presentation.tips.presenter.TipsPresenterImpl
import com.flipsidegroup.active10.presentation.todaywalk.presenter.TodayWalkPresenter
import com.flipsidegroup.active10.presentation.todaywalk.presenter.TodayWalkPresenterImpl
import com.flipsidegroup.active10.presentation.usecases.ChangePlanStateUseCase
import com.flipsidegroup.active10.presentation.usecases.IsPlanMatchToUserUseCase
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.presentation.userDetails.presenter.UserDetailsPresenter
import com.flipsidegroup.active10.presentation.userDetails.presenter.UserDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsPresenter
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsPresenterImpl
import com.flipsidegroup.active10.presentation.walkingplanstatistics.WalkingPlanStatisticsPresenter
import com.flipsidegroup.active10.presentation.walkingplanstatistics.WalkingPlanStatisticsPresenterImpl
import com.flipsidegroup.active10.presentation.walkpresentation.presenter.WalkPresentationPresenter
import com.flipsidegroup.active10.presentation.walkpresentation.presenter.WalkPresentationPresenterImp
import com.flipsidegroup.active10.presentation.walksnear.presenter.WalksNearPresenter
import com.flipsidegroup.active10.presentation.walksnear.presenter.WalksNearPresenterImpl
import com.flipsidegroup.active10.presentation.walksneardetails.presenter.WalksNearDetailsPresenter
import com.flipsidegroup.active10.presentation.walksneardetails.presenter.WalksNearDetailsPresenterImpl
import com.flipsidegroup.active10.utils.TodayWalkHeaderHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class PresenterModule {

    @Provides
    internal fun provideGoalsActivityPresenter(
        settingsUtils: SettingsUtils,
        loginRepository: LoginRepository,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        preferenceRepository: PreferenceRepository,
    ): GoalsActivityPresenter {
        return GoalsActivityPresenterImpl(
            settingsUtils,
            loginRepository,
            firebaseAnalyticsHelper,
            preferenceRepository
        )
    }

    @Provides
    @Singleton
    internal fun provideDiscoverPresenter(
        screenRepository: ScreenRepository,
        discoverRepository: DiscoverRepository,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper
    ): DiscoverPresenter {
        return DiscoverPresenterImpl(screenRepository, discoverRepository, firebaseAnalyticsHelper)
    }

    @Provides
    internal fun provideDiscoverDetailsPresenter(
        discoverRepository: DiscoverRepository,
        preferenceRepository: PreferenceRepository,
        settingsUtils: SettingsUtils,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper
    ): DiscoverDetailsPresenter {
        return DiscoverDetailsPresenterImpl(discoverRepository, preferenceRepository, settingsUtils, firebaseAnalyticsHelper)
    }

    @Provides
    internal fun provideSettingsPresenter(
        settingsUtils: SettingsUtils,
        preferenceRepository: PreferenceRepository,
        screenRepository: ScreenRepository,
        walkingPlanRepository: WalkingPlanRepository,
        loginRepository: LoginRepository,
        changePlanStateUseCase: ChangePlanStateUseCase,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
    ): SettingsPresenter {
        return SettingsPresenterImpl(settingsUtils, preferenceRepository, screenRepository, walkingPlanRepository, loginRepository, changePlanStateUseCase, removeNhsUserDataUseCase)
    }


    @Provides
    internal fun provideMyWalkPresenter(
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository,
        discoverRepository: DiscoverRepository,
        screenRepository: ScreenRepository,
        context: Context
    ): MyWalksPresenter {
        return MyWalkPresenterImpl(
            context,
            settingsUtils,
            localRepository,
            discoverRepository,
            screenRepository
        )
    }

    @Provides
    internal fun provideFaqPresenter(
        localRepository: LocalRepository
    ): FaqPresenter {
        return FaqPresenterImpl(localRepository)
    }

    @Provides
    internal fun provideHomePresenter(
        context: Context,
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        discoverRepository: DiscoverRepository,
        screenRepository: ScreenRepository,
        preferenceRepository: PreferenceRepository,
        loginRepository: LoginRepository,
        nhsSyncRepository: NhsSyncRepository,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase
    ): HomePresenter {
        return HomePresenterImpl(
            context,
            settingsUtils,
            localRepository,
            firebaseAnalyticsHelper,
            discoverRepository,
            screenRepository,
            preferenceRepository,
            loginRepository,
            nhsSyncRepository,
            removeNhsUserDataUseCase
        )
    }

    @Provides
    internal fun providesDeepLinkPresenter(
        screenRepository: ScreenRepository
    ): DeepLinkPresenter {
        return DeepLinkPresenter(screenRepository)
    }

    @Provides
    internal fun provideSplashPresenter(
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository,
        preferenceRepository: PreferenceRepository,
        localNotificationRepository: LocalNotificationRepository,
        loginRepository: LoginRepository,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
    ): SplashPresenter {
        return SplashPresenterImpl(settingsUtils, localRepository, preferenceRepository, localNotificationRepository, loginRepository, removeNhsUserDataUseCase)
    }

    @Provides
    internal fun providePermissionPresenter(
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        preferenceRepository: PreferenceRepository,
        loginRepository: LoginRepository
    ): PermissionPresenter {
        return PermissionPresenterImpl(
            settingsUtils,
            localRepository,
            firebaseAnalyticsHelper,
            preferenceRepository,
            loginRepository
        )
    }

    @Provides
    internal fun provideUserDetailsPresenter(
        localRepository: LocalRepository
    ): UserDetailsPresenter {
        return UserDetailsPresenterImpl(localRepository)
    }

    @Provides
    internal fun provideTargetPresenter(
        settingsUtils: SettingsUtils,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        preferenceRepository: PreferenceRepository,
        nhsSyncRepository: NhsSyncRepository
    ): SetTargetPresenter {
        return SetTargetPresenterImpl(settingsUtils, firebaseAnalyticsHelper, preferenceRepository, nhsSyncRepository)
    }

    @Provides
    internal fun provideTodayWalkPresenter(
        settingsUtils: SettingsUtils,
        context: Context,
        localRepository: LocalRepository,
        todayWalkHeaderHelper: TodayWalkHeaderHelper,
        preferenceRepository: PreferenceRepository,
        screenRepository: ScreenRepository,
        analyticsHelper: FirebaseAnalyticsHelper
    ): TodayWalkPresenter {
        return TodayWalkPresenterImpl(
            settingsUtils,
            context,
            localRepository,
            todayWalkHeaderHelper,
            preferenceRepository,
            screenRepository,
            analyticsHelper
        )
    }

    @Provides
    internal fun provideTipsPresenter(
        localRepository: LocalRepository
    ): TipsPresenter {
        return TipsPresenterImpl(localRepository)
    }

    @Provides
    internal fun provideHowItWorksPresenter(
        localRepository: LocalRepository
    ): HowItWorksPresenter {
        return HowItWorksPresenterImpl(localRepository)
    }


    @Provides
    internal fun provideLegalsPresenter(
        localRepository: LocalRepository
    ): LegalsPresenter {
        return LegalsPresenterImpl(localRepository)
    }

    @Provides
    internal fun provideGoalsFragmentPresenter(
        localRepository: LocalRepository,
        settingsUtils: SettingsUtils
    ): GoalsFragmentPresenter {
        return GoalsFragmentPresenterImpl(localRepository, settingsUtils)
    }

    @Provides
    internal fun provideTermsAndConditionsPresenter(
        localRepository: LocalRepository
    ): TermsAndConditionsPresenter {
        return TermsAndConditionsPresenterImpl(localRepository)
    }

    @Provides
    internal fun provideWhereDoYouLivePresenter(
        screenRepository: ScreenRepository
    ): WhereDoYouLivePresenter {
        return WhereDoYouLivePresenterImpl(screenRepository)
    }

    @Provides
    internal fun provideRewardPresenter(
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository
    ): RewardsPresenter {
        return RewardsPresenterImpl(settingsUtils, localRepository)
    }

    @Provides
    internal fun provideWalkPresentationPresenter(
        settingsUtils: SettingsUtils,
        localRepository: LocalRepository
    ): WalkPresentationPresenter {
        return WalkPresentationPresenterImp(settingsUtils, localRepository)
    }

    @Provides
    internal fun provideStayUpdatedPresenter(
        screenRepository: ScreenRepository,
        settingsUtils: SettingsUtils,
        localNotificationRepository: LocalNotificationRepository,
        loginRepository: LoginRepository,
    ): StayUpdatedPresenter {
        return StayUpdatedPresenterImpl(
            screenRepository,
            settingsUtils,
            localNotificationRepository,
            loginRepository
        )
    }

    @Provides
    internal fun provideClassicUserDetailsPresenter(
        screenRepository: ScreenRepository,
        settingsUtils: SettingsUtils,
    ): ClassicUserDetailsPresenter {
        return ClassicUserDetailsPresenterImpl(screenRepository, settingsUtils)
    }

    @Provides
    internal fun provideNhsUserDetailsPresenter(
        loginRepository: LoginRepository,
        screenRepository: ScreenRepository,
        settingsUtils: SettingsUtils,
    ): NhsUserDetailsPresenter {
        return NhsUserDetailsPresenterImpl(loginRepository, screenRepository, settingsUtils)
    }

    @Provides
    internal fun provideSignInPresenter(
        screenRepository: ScreenRepository,
        loginRepository: LoginRepository,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
    ): SignInPresenter {
        return SignInPresenterImpl(
            screenRepository,
            loginRepository,
            removeNhsUserDataUseCase
         )
    }

    @Provides
    internal fun provideProgressBarPresenter(
        settingsUtils: SettingsUtils,
        nhsSyncRepository: NhsSyncRepository,
        preferenceRepository: PreferenceRepository,
        localNotificationRepository: LocalNotificationRepository,
        screenRepository: ScreenRepository
    ): ProgressBarPresenter {
        return ProgressBarPresenterImpl(
            settingsUtils,
            nhsSyncRepository,
            preferenceRepository,
            localNotificationRepository,
            screenRepository
        )
    }

    @Provides
    internal fun provideNhsLoginWebViewPresenter(
        loginRepository: LoginRepository,
        loginNotificationRepository: LocalNotificationRepository,
        screenRepository: ScreenRepository,
    ): NhsLoginWebViewPresenter {
        return NhsLoginWebViewPresenter(
            loginRepository,
            loginNotificationRepository,
            screenRepository
        )
    }

    @Provides
    internal fun provideNhsUpdateWebViewPresenter(
        preferenceRepository: PreferenceRepository,
        settingsUtils: SettingsUtils
    ): NhsUpdateWebViewPresenter {
        return NhsUpdateWebViewPresenter(preferenceRepository, settingsUtils)
    }

    @Provides
    internal fun provideInformationPresenter(
        screenRepository: ScreenRepository,
        preferenceRepository: PreferenceRepository,
        analyticsHelper: FirebaseAnalyticsHelper
    ): InformationPresenter {
        return InformationPresenter(screenRepository, preferenceRepository, analyticsHelper)
    }

    @Provides
    internal fun provideMyWalkingPlansPresenter(
        screenRepository: ScreenRepository,
        walkingPlanRepository: WalkingPlanRepository,
        settingsUtils: SettingsUtils,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        isPlanMatchToUserUseCase: IsPlanMatchToUserUseCase
    ): MyWalkingPlansPresenter {
        return MyWalkingPlansPresenterImpl(
            screenRepository,
            walkingPlanRepository,
            settingsUtils,
            firebaseAnalyticsHelper,
            isPlanMatchToUserUseCase,
        )
    }

    @Provides
    internal fun provideWalkingPlanDetailsPresenter(
        settingsUtils: SettingsUtils,
        screenRepository: ScreenRepository,
        walkingPlanRepository: WalkingPlanRepository,
        discoverRepository: DiscoverRepository,
        preferenceRepository: PreferenceRepository,
        localNotificationRepository: LocalNotificationRepository,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        changePlanStateUseCase: ChangePlanStateUseCase,
    ): WalkingPlanDetailsPresenter {
        return WalkingPlanDetailsPresenterImpl(
            settingsUtils,
            screenRepository,
            walkingPlanRepository,
            discoverRepository,
            preferenceRepository,
            localNotificationRepository,
            firebaseAnalyticsHelper,
            changePlanStateUseCase,
        )
    }

    @Provides
    internal fun provideWalkingPlanStatisticsPresenter(
        settingsUtils: SettingsUtils,
        screenRepository: ScreenRepository,
        walkingPlanRepository: WalkingPlanRepository,
        preferenceRepository: PreferenceRepository,
        localNotificationRepository: LocalNotificationRepository,
        changePlanStateUseCase: ChangePlanStateUseCase,
    ): WalkingPlanStatisticsPresenter {
        return WalkingPlanStatisticsPresenterImpl(
            settingsUtils,
            screenRepository,
            walkingPlanRepository,
            preferenceRepository,
            localNotificationRepository,
            changePlanStateUseCase,
        )
    }

    @Provides
    internal fun provideWalksNearPresenter(
        screenRepository: ScreenRepository,
        walksNearRepository: WalksNearRepository,
        settingsUtils: SettingsUtils,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    ): WalksNearPresenter {
        return WalksNearPresenterImpl(
            screenRepository,
            walksNearRepository,
            settingsUtils,
            firebaseAnalyticsHelper,
        )
    }

    @Provides
    internal fun provideWalkNearDetailsPresenter(
        walksNearRepository: WalksNearRepository,
    ): WalksNearDetailsPresenter {
        return WalksNearDetailsPresenterImpl(walksNearRepository)
    }

    @Provides
    internal fun provideCouchAdvertPresenter(
        screenRepository: ScreenRepository,
    ): CouchAdvertPresenter {
        return CouchAdvertPresenterImpl(screenRepository)
    }

    @Provides
    internal fun provideCircularWalkDetailsPresenter(
        screenRepository: ScreenRepository,
    ): CircularWalkDetailsPresenter {
        return CircularWalkDetailsPresenterImpl(screenRepository)
    }

    @Provides
    internal fun provideActivityLevelPresenter(
        screenRepository: ScreenRepository,
        settingsUtils: SettingsUtils,
        loginRepository: LoginRepository,
        preferenceRepository: PreferenceRepository,
    ): ActivityLevelPresenter {
        return ActivityLevelPresenterImpl(
            screenRepository,
            settingsUtils,
            loginRepository,
            preferenceRepository
        )
    }

    @Provides
    internal fun provideLicensesPresenter(
        jsonRepository: JsonRepository
    ): LicensesContract.Presenter {
        return LicensesPresenter(
            jsonRepository
        )
    }

    @Provides
    internal fun providePinAuthPresenter(
        loginRepository: LoginRepository,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase
    ): PinAuthPresenter {
        return PinAuthPresenterImpl(
            loginRepository,
            removeNhsUserDataUseCase
        )
    }

    @Provides
    internal fun provideSystemAuthPresenter(
        loginRepository: LoginRepository,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase
    ): SystemAuthPresenter {
        return SystemAuthPresenterImpl(
            loginRepository,
            removeNhsUserDataUseCase
        )
    }

}
