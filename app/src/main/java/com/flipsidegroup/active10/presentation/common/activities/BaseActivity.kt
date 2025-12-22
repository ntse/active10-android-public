package com.flipsidegroup.active10.presentation.common.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import com.flipsidegroup.active10.presentation.common.widgets.ContentLoadingProgressLayout
import com.flipsidegroup.active10.presentation.root.RootDetectionAlertDialog
import com.flipsidegroup.active10.utils.AlertHelper
import com.flipsidegroup.active10.utils.CheckAuthManager
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.GlobalUIEvents
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.worker.WorkSchedulerHelper
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


const val IN_NOTIFICATION_ANALYTICS = "IN_NOTIFICATION_ANALYTICS"

abstract class BaseActivity<V : LifecycleAwareView> : ToolbarActivity(), HasAndroidInjector,
    BaseView {

    @Inject
    internal lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var localRepository: LocalRepository

    private lateinit var loadingWidget: ContentLoadingProgressLayout

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    internal lateinit var checkAuthManager: CheckAuthManager

    private var globalEventJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        performInjection()
        super.onCreate(savedInstanceState)
        attachPresenter()
        setUpLoadingWidget()

        checkAndSendEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPresenter()?.unbind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return childFragmentInjector
    }

    override fun showLoading() {
        loadingWidget.show()
    }

    override fun hideLoading() {
        loadingWidget.hide()
    }

    override fun showAlert(throwable: Throwable?) {
        AlertHelper.showErrorToast(throwable?.message)
    }

    override fun showAlert(message: String?) {
        AlertHelper.showErrorToast(message)
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachPresenter() {
        getPresenter()?.bind(this as V)
    }

    private fun performInjection() {
        AndroidInjection.inject(this)
    }

    private fun setUpLoadingWidget() {
        loadingWidget = ContentLoadingProgressLayout(this)
        (window.decorView as ViewGroup).addView(loadingWidget)
    }

    protected abstract fun getPresenter(): LifecycleAwarePresenter<V>?

    protected fun setContentLoadingProgressMinDelay(minDelay: Int) {
        loadingWidget.minDelay = minDelay
    }

    override fun onResume() {
        super.onResume()

        // Start collecting event
        globalEventJob = lifecycleScope.launch {
            GlobalUIEvents.showRootDetectedDialog.collect {
                    RootDetectionAlertDialog().show(
                        supportFragmentManager,
                        "RootDetectionAlertDialog"
                    )
            }
        }

        (application as Active10App).updateUserInteraction()

        //this functionality will be done only in home screen
        if (!BuildConfig.DEBUG) return

        val isNotificationMin = settingsUtils.getSettingsHolder().notificationMin ?: false
        if (isNotificationMin) {
            settingsUtils.updateSettings(SettingsDataHolder(lastTimeInApp = DateHelper.getCurrentTimestamp()))
            WorkSchedulerHelper.startLapsedNotificationIfNeeded(
                localRepository,
                settingsUtils,
                isNotificationMin
            )
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            (application as Active10App).updateUserInteraction()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onPause() {
        super.onPause()

        globalEventJob?.cancel()
        globalEventJob = null
    }

    private fun checkAndSendEvent() {
        if (intent.hasExtra(IN_NOTIFICATION_ANALYTICS) &&
            intent.getStringExtra(IN_NOTIFICATION_ANALYTICS) != null
        ) {
            val event = intent.getStringExtra(IN_NOTIFICATION_ANALYTICS) ?: ""
            firebaseAnalyticsHelper.saveEvent(event, null)
        }
    }
}