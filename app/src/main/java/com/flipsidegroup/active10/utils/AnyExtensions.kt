package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.persistance.AppDatabase

fun <T> Any.onDataLoaded(callback: (T) -> Unit): AppDatabase.OnDataLoadedListener<T> {
    return object : AppDatabase.OnDataLoadedListener<T> {
        override fun onDataLoaded(data: T) {
            callback(data)
        }
    }
}