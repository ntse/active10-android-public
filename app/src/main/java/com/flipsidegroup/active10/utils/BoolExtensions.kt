package com.flipsidegroup.active10.utils

fun Boolean.asLong(): Long {
    return if(this) 1 else 0
}
