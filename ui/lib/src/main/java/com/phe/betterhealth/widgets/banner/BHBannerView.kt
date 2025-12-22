package com.phe.betterhealth.widgets.banner

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.common.setTint

@BindingMethods(
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerInfoText",
        method = "setInfoText"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerInfoTextColor",
        method = "setInfoTextColor"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerSubInfoText",
        method = "setSubInfoText"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerSubInfoTextColor",
        method = "setSubInfoTextColor"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerIconColor",
        method = "setIconColor"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerIconType",
        method = "setIconType"
    ),
    BindingMethod(
        type = BHBannerView::class,
        attribute = "bannerImageSrc",
        method = "setImageSrc"
    )
)

/**
 *
 * @property infoTextColor - color of info text
 * @property infoText - text of info description
 *
 * @property subInfoTextColor - color of subordinate info text
 * @property subInfoText - text of subordinate info description
 *
 * @property iconColor - color of right icon
 * @property iconType - icon type - here You need to pass one of the enum from [BHBannerIconType] class
 *
 * @property imageSrc - url for banner image
 *
 * All of those above parameters can be provided by DataBinding or normal text in XML
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
class BHBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhBannerStyle,
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var rootView: MaterialCardView
    private lateinit var containerView: ConstraintLayout
    private lateinit var infoTextView: TextView
    private lateinit var subInfoTextView: TextView
    private lateinit var imageView: ShapeableImageView
    private lateinit var closeIconView: ImageButton
    private lateinit var indicatorIconView: ImageButton

    var infoTextColor: Int = ContextCompat.getColor(context, R.color.bhWhite)
        set(value) {
            infoTextView.setTextColor(value)
        }

    var infoText: String?
        get() = infoTextView.text?.toString()
        set(value) {
            infoTextView.text = value
        }

    var subInfoTextColor: Int = ContextCompat.getColor(context, R.color.bhWhite)
        set(value) {
            subInfoTextView.setTextColor(value)
        }

    var subInfoText: String?
        get() = subInfoTextView.text?.toString()
        set(value) {
            subInfoTextView.visibility = if (value == null) GONE else VISIBLE
            subInfoTextView.text = value
        }

    var imageSrc: String? = ""
        set(value) {
            imageView.load(value)
        }

    var iconType: BHBannerIconType = BHBannerIconType.INDICATOR
        set(value) {
            when (value) {
                BHBannerIconType.CLOSE -> {
                    indicatorIconView.isVisible = false
                    closeIconView.isVisible = true
                }

                BHBannerIconType.INDICATOR -> {
                    indicatorIconView.isVisible = true
                    closeIconView.isVisible = false
                }

                BHBannerIconType.NONE -> {
                    indicatorIconView.isVisible = false
                    closeIconView.isVisible = false
                }
            }
            field = value
        }

    var iconColor: Int = ContextCompat.getColor(context, R.color.bhWhite)
        set(value) {
            indicatorIconView.setTint(value)
            closeIconView.setTint(value)
        }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHBanner, defStyleAttr, 0
        )
        inflateBaseViews()
        initializeViewsWithAttrValues(attributes)
    }

    private fun inflateBaseViews() {
        LayoutInflater.from(context).inflate(R.layout.bh_banner, this, true)
        rootView = findViewById(R.id.bh_banner_layout)
        containerView = findViewById(R.id.bh_banner_container)
        infoTextView = findViewById(R.id.bh_banner_info_text)
        subInfoTextView = findViewById(R.id.bh_banner_sub_info_text)
        imageView = findViewById(R.id.bh_banner_image)
        closeIconView = findViewById(R.id.bh_banner_close_icon)
        indicatorIconView = findViewById(R.id.bh_banner_indicator_icon)

        closeIconView.setOnClickListener {
            isVisible = false
        }
    }

    private fun initializeViewsWithAttrValues(attributes: TypedArray) {
        infoText = attributes.getString(R.styleable.BHBanner_bannerInfoText)
        infoTextColor = attributes.getColor(
            R.styleable.BHBanner_bannerInfoTextColor,
            ContextCompat.getColor(context, R.color.bhWhite)
        )

        subInfoText = attributes.getString(R.styleable.BHBanner_bannerSubInfoText)
        subInfoTextColor = attributes.getColor(
            R.styleable.BHBanner_bannerSubInfoTextColor,
            ContextCompat.getColor(context, R.color.bhWhite)
        )

        imageSrc = attributes.getString(R.styleable.BHBanner_bannerImageSrc)

        iconColor = attributes.getColor(
            R.styleable.BHBanner_bannerIconColor,
            ContextCompat.getColor(context, R.color.bhWhite)
        )
        iconType = BHBannerIconType.fromParams(
            attributes.getInt(
                R.styleable.BHBanner_bannerIconType,
                BHBannerIconType.INDICATOR.id
            )
        )
    }
}
