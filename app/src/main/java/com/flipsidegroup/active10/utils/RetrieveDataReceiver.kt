package com.flipsidegroup.active10.utils

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import com.flipsidegroup.active10.presentation.home.interfaces.RetrieveLostDataListener
import java.lang.ref.WeakReference

class RetrieveDataReceiver(handler: Handler?, listener: RetrieveLostDataListener) :
    ResultReceiver(handler) {

    private var listenerRef: WeakReference<RetrieveLostDataListener>? = null

    init {
        listenerRef = WeakReference(listener)
    }

    companion object {
        const val KEY_IN_HAD_MISSING_DATA = "KEY_IN_HAD_MISSING_DATA"
        const val RETRIEVE_DATA_COMPLETED_CODE = 600
        const val RETRIEVE_DATA_ERROR_CODE = 601
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)
        if (resultCode == RETRIEVE_DATA_COMPLETED_CODE) {
            listenerRef?.get()?.onLostDataReceived()
        } else if (resultCode == RETRIEVE_DATA_ERROR_CODE) {
            listenerRef?.get()?.onLostDataError()
        }
    }

    init {
        listenerRef = WeakReference(listener)
    }
}