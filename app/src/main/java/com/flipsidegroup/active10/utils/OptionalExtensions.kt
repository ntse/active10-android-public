package com.flipsidegroup.active10.utils

import com.google.firebase.perf.util.Optional


fun <T> Optional<T>.valueOrNull() = if (this.isAvailable) this.get() else null