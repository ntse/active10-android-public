package com.flipsidegroup.active10.di.components

import android.app.Application
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.di.builders.ActivityBuilder
import com.flipsidegroup.active10.di.builders.FragmentBuilder
import com.flipsidegroup.active10.di.modules.*
import com.flipsidegroup.active10.presentation.home.dialog.DeepLinkDialog
import com.flipsidegroup.active10.presentation.walkreminder.alarmreceiver.AlarmReceiver
import com.flipsidegroup.active10.services.*
import com.flipsidegroup.active10.utils.worker.ApiWorker
import com.flipsidegroup.active10.utils.worker.LapsedWorker
import com.flipsidegroup.active10.utils.worker.LocalNotificationWorker
import com.flipsidegroup.active10.utils.worker.OnboardingWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [(AndroidInjectionModule::class), (AppModule::class), (NetworkModule::class),
        (PresenterModule::class), (RepositoryModule::class), (DatabaseModule::class), (ActivityBuilder::class), (FragmentBuilder::class)]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    fun inject(app: Active10App)

    fun inject(alarmReceiver: AlarmReceiver)

    fun inject(localNotificationWorker: LocalNotificationWorker)

    fun inject(onboardingWorker: OnboardingWorker)

    fun inject(apiWorker: ApiWorker)

    fun inject(lapsedWorker: LapsedWorker)

    fun inject(migrationService: MigrationService)

    fun inject(myWalksService: MyWalksService)

    fun inject(walkingPlanService: WalkingPlanService)

    fun inject(recoverWalksService: RecoverWalksService)

    fun inject(recoverLostWalkService: RecoverLostWalkService)

    fun inject(deepLinkDialog: DeepLinkDialog)
}
