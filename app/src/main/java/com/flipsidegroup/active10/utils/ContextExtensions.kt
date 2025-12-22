package com.flipsidegroup.active10.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat


fun Context.hasPermissions(vararg permissions: String): Boolean {
    var arePermissionsGranted = true

    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            arePermissionsGranted = false
        }
    }

    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || arePermissionsGranted
}


fun Activity.shouldShowPermission(vararg permissions: String): Boolean {
    var shouldShow = true

    for (permission in permissions) {
        if (!shouldShowRequestPermissionRationale(this, permission)) {
            shouldShow = false
        }
    }

    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || shouldShow
}

fun Context.openURL(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(browserIntent)
}

fun Context.hasInternetConnection(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo

    return activeNetwork != null && activeNetwork.isConnected
}

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.dpToPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)

fun Context.isBiometricAvailable(): Boolean {
    val manager = BiometricManager.from(this)
    val authenticators = BIOMETRIC_WEAK
    return manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
}