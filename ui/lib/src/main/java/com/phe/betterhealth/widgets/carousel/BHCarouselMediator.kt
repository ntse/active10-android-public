package com.phe.betterhealth.widgets.carousel

interface BHCarouselMediator {

    fun attach(): BHCarouselMediator

    fun onStateRestore(savedState: BHCarouselIndicator.SavedState?)

    fun detach()
}
