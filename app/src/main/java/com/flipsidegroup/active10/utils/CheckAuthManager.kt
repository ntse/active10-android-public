package com.flipsidegroup.active10.utils

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckAuthManager @Inject constructor() {
    val invokeAuthScreen: MutableLiveData<Boolean> = MutableLiveData(false)
}