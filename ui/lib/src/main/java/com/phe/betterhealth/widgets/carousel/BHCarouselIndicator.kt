package com.phe.betterhealth.widgets.carousel

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.phe.betterhealth.widgets.R

/**
 * Inspired by [com.google.android.material.tabs.TabLayout]
 */
class BHCarouselIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mediator: BHCarouselMediator? = null

    private val switchPageListeners = mutableListOf<OnPageSwitchListener>()

    private val afterSwitchPageListener = mutableListOf<AfterPageSwitchListener>()

    var currentState: SavedState? = null
        private set

    var currentPagePosition = 0
        private set

    var pageCount = 0

    private var counterView: TextView? = null
    private var leftButton: ImageButton? = null
    private var rightButton: ImageButton? = null
    private var wasButtonClicked: Boolean = false

    fun interface OnPageSwitchListener {
        fun onPageSwitch(newPosition: Int)
    }

    fun interface AfterPageSwitchListener {
        fun onPageSwitched(newState: SavedState)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.bh_carousel_indicator, this, true)
        counterView = findViewById(R.id.bh_carousel_indicator_counter)
        leftButton = findViewById(R.id.bh_carousel_indicator_button_left)
        rightButton = findViewById(R.id.bh_carousel_indicator_button_right)

        initializeNavigationButtons()
    }

    fun addOnPageSwitchListener(listener: OnPageSwitchListener?) {
        listener ?: return
        switchPageListeners.add(listener)
    }

    fun removeOnPageSwitchListener(listener: OnPageSwitchListener?) {
        listener ?: return
        switchPageListeners.remove(listener)
    }

    fun addAfterPageSwitchListener(listener: AfterPageSwitchListener?) {
        listener ?: return
        afterSwitchPageListener.add(listener)
    }

    private fun initializeNavigationButtons() {
        val leftButton = findViewById<ImageButton>(R.id.bh_carousel_indicator_button_left)
        val rightButton = findViewById<ImageButton>(R.id.bh_carousel_indicator_button_right)

        leftButton.setOnClickListener {
            wasButtonClicked = true
            switchPageListeners.onEach { it.onPageSwitch(currentPagePosition - 1) }
        }
        rightButton.setOnClickListener {
            wasButtonClicked = true
            switchPageListeners.onEach { it.onPageSwitch(currentPagePosition + 1) }
        }
    }

    fun onSwitchPage(position: Int, state: Parcelable? = null) {
        check(position < pageCount)
        currentPagePosition = position
        currentState = SavedState(position, state)
        afterSwitchPageListener.onEach { it.onPageSwitched(currentState!!) }

        reloadNavigation()
    }

    fun reloadNavigation() {
        val previousItemIndex = currentPagePosition - 1
        val nextItemIndex = currentPagePosition + 1
        val nextElementsCount = pageCount - nextItemIndex
        val currentPage = if (pageCount > 0) currentPagePosition + 1 else 0

        counterView?.text = resources.getString(
            R.string.bh_carousel_counter,
            currentPage.toString(),
            pageCount.toString()
        )

        counterView?.contentDescription = "Slide $currentPage of $pageCount is currently selected"

//      Focus on the counter view when the first or last element in carousel is selected by the indicator
//        if (currentPage in arrayOf(0, pageCount) && wasButtonClicked) {
//            this.post { counterView?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED) }
//        }
        wasButtonClicked = false

        if (previousItemIndex < 0) {
            leftButton?.isInvisible = true
            leftButton?.jumpDrawablesToCurrentState()
            leftButton?.contentDescription = "No previous slides, first slide is selected"
        } else {
            leftButton?.isVisible = true
            leftButton?.jumpDrawablesToCurrentState()
            leftButton?.contentDescription = buildString {
                append("Move carousel to previous slide, ")
                append(previousItemIndex + 1)
                append(" previous slide")
                if (previousItemIndex > 0) append("s")
                append(" available")
            }
        }

        if (nextElementsCount <= 0) {
            rightButton?.isInvisible = true
            rightButton?.jumpDrawablesToCurrentState()
            rightButton?.contentDescription = "No next slides, first slide is selected"
        } else {
            rightButton?.isVisible = true
            rightButton?.jumpDrawablesToCurrentState()
            rightButton?.contentDescription = buildString {
                append("Move carousel to next slide, ")
                append(nextElementsCount)
                append(" next slide")
                if (nextElementsCount > 0) append("s")
                append(" available")
            }
        }
    }

    fun setupWithViewPager2(viewPager: ViewPager2, savedState: SavedState? = null) {
        currentState = savedState
        mediator?.detach()
        mediator = BHCarouselViewPager2Mediator(this, viewPager).attach()
        mediator?.onStateRestore(savedState)
    }

    fun setupWithRecyclerView(recyclerView: RecyclerView, savedState: SavedState? = null) {
        currentState = savedState
        mediator?.detach()
        mediator = BHCarouselRecyclerViewMediator(this, recyclerView).attach()
        mediator?.onStateRestore(savedState)
    }

    fun detachMediator() {
        mediator?.detach()
    }

    data class SavedState(
        val pagePosition: Int,
        val scrollState: Parcelable?,
    )
}
