package com.phe.betterhealth.widgets.common

import android.view.View
import android.view.ViewParent

interface FadingEdgeScrollView : ViewParent {

    fun scrollToBottom(afterScroll: () -> Unit = {})

    fun scrollToChild(child: View)
}
