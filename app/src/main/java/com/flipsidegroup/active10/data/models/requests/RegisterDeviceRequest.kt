package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.utils.Constants
import com.google.gson.annotations.SerializedName


data class RegisterDeviceRequest(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("os") val deviceOs: String = Constants.DEVICE_OS
)