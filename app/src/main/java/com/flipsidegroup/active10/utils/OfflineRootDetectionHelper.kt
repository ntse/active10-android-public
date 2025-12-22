package com.flipsidegroup.active10.utils

import android.content.Context
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.root.RootException
import com.flipsidegroup.active10.data.models.root.RootItemResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils
import timber.log.Timber

object OfflineRootDetectionHelper {

    private const val SKIP_OFFLINE_ROOT_CHECK_FOR_DEBUG = true

    fun checkRootedDeviceOffline(applicationContext: Context) {
        val rootBeer = RootBeer(applicationContext)
        if (rootBeer.isRooted) {
            getRootResults(rootBeer)
                .map { "Root detection offline : ${it.text} = ${it.result}" }
                .forEach { message ->
                    Timber.d(message)
                    FirebaseCrashlytics.getInstance().log(message)
                }

            if (BuildConfig.DEBUG && SKIP_OFFLINE_ROOT_CHECK_FOR_DEBUG) {
                Timber.d("Offline root detection : Device is not secure! Don't crash Debug version.")
                return
            }

            throw RootException("Offline root detection :  Device is not secure!")
        } else {
            Timber.d("Root detection offline : root not detected")
        }
    }

    private fun getRootResults(rootBeer: RootBeer) = listOf(
        RootItemResult("Root Management Apps", rootBeer.detectRootManagementApps()),
        RootItemResult("Potentially Dangerous Apps", rootBeer.detectPotentiallyDangerousApps()),
        RootItemResult("Root Cloaking Apps", rootBeer.detectRootCloakingApps()),
        RootItemResult("TestKeys", rootBeer.detectTestKeys()),
        RootItemResult("BusyBoxBinary", rootBeer.checkForBusyBoxBinary()),
        RootItemResult("SU Binary", rootBeer.checkForSuBinary()),
        RootItemResult("2nd SU Binary check", rootBeer.checkSuExists()),
        RootItemResult("For RW Paths", rootBeer.checkForRWPaths()),
        RootItemResult("Dangerous Props", rootBeer.checkForDangerousProps()),
        RootItemResult("Root via native check", rootBeer.checkForRootNative()),
        RootItemResult("SE linux Flag Is Enabled", !Utils.isSelinuxFlagInEnabled()),
        RootItemResult("Magisk specific checks", rootBeer.checkForMagiskBinary())
    )

}