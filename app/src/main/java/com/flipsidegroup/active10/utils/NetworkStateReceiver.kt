package com.flipsidegroup.active10.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.flipsidegroup.active10.Active10App
import timber.log.Timber

class NetworkStateReceiver(var connectionListener: InternetConnection? = null) :
    BroadcastReceiver() {

    companion object {
        var isInternetConnected: Boolean? = null
        var isWifiConnected: Boolean? = null
        var isMobileDataConnected: Boolean? = null
        var networkState: String? = null

        init {
            initNetworkConnectivityData()
        }

        private fun initNetworkConnectivityData() {
            val connectivityManager = Active10App.instance.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                isWifiConnected = networkInfo?.type == ConnectivityManager.TYPE_WIFI
                isMobileDataConnected = networkInfo?.type == ConnectivityManager.TYPE_MOBILE
            } else {
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

                isWifiConnected =
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
                isMobileDataConnected =
                    networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        ?: false
            }


            networkState = networkInfo?.detailedState?.name ?: "null"
            isInternetConnected = networkInfo?.isConnected ?: false
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        if (isInitialStickyBroadcast) {
            return
        }

        val wasInternetConnected = isInternetConnected ?: false
        initNetworkConnectivityData()

        val hasInternetConnection = isInternetConnected ?: false
        val hasInternetConnectionChanged = wasInternetConnected != hasInternetConnection
        Timber.d(
            "Internet connectivity changed: $hasInternetConnectionChanged " +
                    "Internet available: $hasInternetConnection, Wifi on: $isWifiConnected, mobile data on: $isMobileDataConnected, state: $networkState"
        )

        if (hasInternetConnectionChanged) {
            connectionListener?.onConnectionChanged(hasInternetConnection)
        }
    }

    interface InternetConnection {
        fun onConnectionChanged(isConnected: Boolean)
    }
}
