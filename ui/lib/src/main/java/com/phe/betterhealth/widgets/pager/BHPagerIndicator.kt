package com.phe.betterhealth.widgets.pager

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.button.BHButton
import com.phe.betterhealth.widgets.utils.initializeA11yNodeInfo

class BHPagerIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhPagerIndicatorStyle,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    lateinit var buttonPrev: BHButton
        private set
    lateinit var buttonNext: BHButton
        private set

    var pagerTitle: String?
        get() = titleView.text.toString()
        set(value) {
            titleView.text = value
        }

    var pagerDescription: String?
        get() = descriptionView.text.toString()
        set(value) {
            with(descriptionView) {
                isGone = value.isNullOrBlank()
                text = value
            }
        }

    var pagerButtonPrevText: String?
        get() = buttonPrev.text.toString()
        set(value) {
            buttonPrev.isInvisible = value.isNullOrBlank()
            buttonPrev.text = value
        }

    var pagerButtonPrevContentDescription: String?
        get() = buttonPrev.contentDescription.toString()
        set(value) {
            buttonPrev.contentDescription = value
        }

    var pagerButtonNextText: String?
        get() = buttonNext.text.toString()
        set(value) {
            buttonNext.isInvisible = value.isNullOrBlank()
            buttonNext.text = value
        }

    var pagerButtonNextContentDescription: String?
        get() = buttonNext.contentDescription.toString()
        set(value) {
            buttonNext.contentDescription = value
        }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHPagerIndicator, defStyleAttr, 0
        )

        inflateBaseViews()

        try {
            attributes.initializeViewsWithAttrValues()
        } finally {
            attributes.recycle()
        }
    }

    private fun inflateBaseViews() {
        LayoutInflater.from(context).inflate(R.layout.bh_calendar_navigation, this, true)
        buttonPrev = findViewById(R.id.bhPagerIndicatorPrevButton)
        buttonNext = findViewById(R.id.bhPagerIndicatorNextButton)
        titleView = findViewById(R.id.bhPagerIndicatorTitle)
        ViewCompat.setAccessibilityHeading(titleView, true)
        descriptionView = findViewById(R.id.bhPagerIndicatorDescription)

        buttonPrev.initializeA11yNodeInfo {
            setTraversalAfter(descriptionView)
            setTraversalBefore(buttonNext)
        }
        buttonNext.initializeA11yNodeInfo {
            if (buttonPrev.isVisible) setTraversalAfter(buttonPrev)
            else setTraversalAfter(descriptionView)
        }
    }

    private fun TypedArray.initializeViewsWithAttrValues() {
        pagerTitle = getString(R.styleable.BHPagerIndicator_pagerTitle)
        pagerDescription = getString(R.styleable.BHPagerIndicator_pagerDescription)
        pagerButtonPrevText = getString(R.styleable.BHPagerIndicator_pagerButtonPrevText)
        pagerButtonPrevContentDescription =
            getString(R.styleable.BHPagerIndicator_pagerButtonPrevContentDescription)
        pagerButtonNextText = getString(R.styleable.BHPagerIndicator_pagerButtonNextText)
        pagerButtonNextContentDescription =
            getString(R.styleable.BHPagerIndicator_pagerButtonNextContentDescription)
    }
}
