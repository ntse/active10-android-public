package com.flipsidegroup.active10.utils

import kotlinx.coroutines.flow.MutableSharedFlow

object GlobalUIEvents {
    val showRootDetectedDialog = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        replay = 0
    )
}