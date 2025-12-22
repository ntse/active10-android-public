package com.phe.betterhealth.widgets.card

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isGone
import com.google.android.material.card.MaterialCardView
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.button.BHButton

class BHCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhCardStyle,
) : MaterialCardView(context, attrs, defStyleAttr) {

    private lateinit var headerView: LinearLayout
    private lateinit var headerTitleView: TextView
    private lateinit var headerDescriptionView: TextView
    private lateinit var headerInfoView: TextView
    lateinit var headerInfoButton: ImageView

    private lateinit var contentView: FrameLayout

    private lateinit var footerView: RelativeLayout
    lateinit var footerButtonView: BHButton
        private set

    var cardHeaderTitle: String?
        get() = headerTitleView.text?.toString()
        set(value) {
            with(headerTitleView) {
                isGone = value.isNullOrBlank()
                text = value
            }
            updateHeaderVisibility()
        }

    var cardHeaderDescription: String?
        get() = headerDescriptionView.text?.toString()
        set(value) {
            with(headerDescriptionView) {
                isGone = value.isNullOrBlank()
                text = value
            }
            updateHeaderVisibility()
        }

    var cardHeaderInfo: String?
        get() = headerInfoView.text?.toString()
        set(value) {
            with(headerInfoView) {
                isGone = value.isNullOrBlank()
                text = value
            }
        }

    var cardFooterButtonText: String?
        get() = footerButtonView.text?.toString()
        set(value) {
            footerButtonView.text = value
            footerView.isGone = value.isNullOrBlank()
        }

    var cardFooterButtonContentDescription: String?
        get() = footerButtonView.contentDescription?.toString()
        set(value) {
            footerButtonView.contentDescription = value
        }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHCardView, defStyleAttr, 0
        )

        inflateBaseViews()
        initializeViewsWithAttrValues(attributes)
    }

    private fun inflateBaseViews() {
        LayoutInflater.from(context).inflate(R.layout.bh_card, this, true)
        headerView = findViewById(R.id.bhCardHeader)
        headerTitleView = findViewById(R.id.bhCardHeaderTitle)
        headerDescriptionView = findViewById(R.id.bhCardHeaderDescription)
        headerInfoView = findViewById(R.id.bhCardHeaderInfo)
        contentView = findViewById(R.id.bhCardContent)
        footerView = findViewById(R.id.bhCardFooter)
        footerButtonView = findViewById(R.id.bhCardFooterButton)
        headerInfoButton = findViewById(R.id.bhCardInfoButton)
    }

    private fun initializeViewsWithAttrValues(attributes: TypedArray) {
        cardHeaderTitle = attributes.getString(R.styleable.BHCardView_cardHeaderTitle)
        cardHeaderDescription = attributes.getString(R.styleable.BHCardView_cardHeaderDescription)
        cardFooterButtonText = attributes.getString(R.styleable.BHCardView_cardFooterButtonText)
        cardFooterButtonContentDescription = attributes
            .getString(R.styleable.BHCardView_cardFooterButtonContentDescription)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child.id == R.id.bhCardWrapper) {
            super.addView(child, index, params)
        } else contentView.addView(child, index, params)
    }

    private fun updateHeaderVisibility() {
        val isEmptyTitle = headerTitleView.text.isNullOrBlank()
        val isEmptyDescription = headerDescriptionView.text.isNullOrBlank()
        headerView.isGone = isEmptyTitle && isEmptyDescription
    }
}
