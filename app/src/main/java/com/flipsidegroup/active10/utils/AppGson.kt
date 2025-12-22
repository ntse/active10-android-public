package com.flipsidegroup.active10.utils

import com.google.gson.Gson


object AppGson {

    val instance: Gson by lazy { Gson() }
}