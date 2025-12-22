package com.phe.betterhealth.widgets.moodselector

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.google.android.material.button.MaterialButton
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialogType
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.button.BHButton
import com.phe.betterhealth.widgets.common.HeadingTextView
import com.phe.betterhealth.widgets.common.setMarginTop

@BindingMethods(
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorStartColor",
        method = "setStartColor"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorEndColor",
        method = "setEndColor"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorTitleText",
        method = "setTitleText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorSubtitleText",
        method = "setSubtitleText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorAnswerFirstText",
        method = "setAnswerFirstText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorAnswerSecondText",
        method = "setAnswerSecondText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorAnswerThirdText",
        method = "setAnswerThirdText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorAnswerFourthText",
        method = "setAnswerFourthText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorFirstButtonText",
        method = "setFirstButtonText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorSecondButtonText",
        method = "setSecondButtonText"
    ),
    BindingMethod(
        type = BHMoodSelector::class,
        attribute = "moodSelectorBackButtonTopMargin",
        method = "setBackButtonTopMargin"
    )
)

/**
 *
 * @property titleText - title text of view
 * @property subtitleText - subtitle text of view
 * @property startColor - start color for background gradient
 * @property endColor - end color for background gradient
 * @property answerFirstText - text visible in first answer button
 * @property answerSecondText - text visible in second answer button
 * @property answerThirdText - text visible in third answer button
 * @property answerFourthText - text visible in fourth answer button
 * @property firstButtonText - text visible in first button
 * @property secondButtonText - text visible in second button
 *
 * All of those above parameters can be provided by DataBinding or normal text in XML
 *
 * @property backButtonTopMargin - top margin of back button
 *
 * @property setOnAnswerClickListener - action after click on one of answers
 * @property setOnFirstAnswerClickListener - action after click on first answer button
 * @property setOnSecondAnswerClickListener - action after click on second answer button
 * @property setOnThirdAnswerClickListener - action after click on third answer button
 * @property setOnFourthAnswerClickListener - action after click on fourth answer button
 *
 * @property setOnBackButtonClickListener - action after click on back button
 *
 * @property setOnFirstButtonClickListener - action after click on first bottom button
 * @property setOnSecondButtonClickListener - action after click on second bottom button
 *
 * @property clickedAnswerType - we can set or get answerType
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
class BHMoodSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhMoodSelectorStyle,
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var rootView: ConstraintLayout
    private lateinit var backButtonView: MaterialButton
    private lateinit var titleView: HeadingTextView
    private lateinit var subtitleView: TextView
    private lateinit var answerGreatView: BHButton
    private lateinit var answerGoodView: BHButton
    private lateinit var answerOkView: BHButton
    private lateinit var answerBetterView: BHButton
    private lateinit var firstBottomButtonView: BHButton
    private lateinit var secondBottomButtonView: TextView

    lateinit var topGuideline: Guideline

    lateinit var clickedAnswerType: BHMoodBottomDialogType

    var startColor: Int = R.color.bhMoodSelectorStartColor
        set(value) {
            field = value
            setBackgroundGradient()
        }

    var endColor: Int = R.color.bhMoodSelectorEndColor
        set(value) {
            field = value
            setBackgroundGradient()
        }

    var backButtonTopMargin: Float?
        get() = resources.getDimension(R.dimen.bh_mood_selector_back_button_margin)
        set(value) {
            backButtonView.setMarginTop(value)
        }

    var titleText: String?
        get() = titleView.text?.toString()
        set(value) {
            titleView.text = value
        }

    var subtitleText: String?
        get() = subtitleView.text?.toString()
        set(value) {
            subtitleView.text = value
        }

    var answerFirstText: String?
        get() = answerGreatView.text?.toString()
        set(value) {
            answerGreatView.text = value
        }

    var answerSecondText: String?
        get() = answerGoodView.text?.toString()
        set(value) {
            answerGoodView.text = value
        }

    var answerThirdText: String?
        get() = answerOkView.text?.toString()
        set(value) {
            answerOkView.text = value
        }

    var answerFourthText: String?
        get() = answerBetterView.text?.toString()
        set(value) {
            answerBetterView.text = value
        }

    var firstButtonText: String?
        get() = firstBottomButtonView.text?.toString()
        set(value) {
            firstBottomButtonView.apply {
                text = value
                isVisible = !value.isNullOrEmpty()
            }
        }

    var secondButtonText: String?
        get() = secondBottomButtonView.text?.toString()
        set(value) {
            secondBottomButtonView.apply {
                text = value
                isVisible = !value.isNullOrEmpty()
            }
        }

    fun getTitleComponent(): HeadingTextView? = titleView

    fun setOnBackButtonClickListener(onClick: () -> Unit) {
        backButtonView.setOnClickListener { onClick() }
    }

    fun setOnAnswerClickListener(onClick: () -> Unit) {
        setOnFirstAnswerClickListener { onClick() }
        setOnSecondAnswerClickListener { onClick() }
        setOnThirdAnswerClickListener { onClick() }
        setOnFourthAnswerClickListener { onClick() }
    }

    fun setOnFirstAnswerClickListener(onClick: () -> Unit) {
        answerGreatView.setOnClickListener {
            clickedAnswerType = BHMoodBottomDialogType.GREAT
            onClick()
        }
    }

    fun setOnSecondAnswerClickListener(onClick: () -> Unit) {
        answerGoodView.setOnClickListener {
            clickedAnswerType = BHMoodBottomDialogType.GOOD
            onClick()
        }
    }

    fun setOnThirdAnswerClickListener(onClick: () -> Unit) {
        answerOkView.setOnClickListener {
            clickedAnswerType = BHMoodBottomDialogType.OK
            onClick()
        }
    }

    fun setOnFourthAnswerClickListener(onClick: () -> Unit) {
        answerBetterView.setOnClickListener {
            clickedAnswerType = BHMoodBottomDialogType.TIRED
            onClick()
        }
    }

    fun setOnFirstButtonClickListener(onClick: () -> Unit) {
        firstBottomButtonView.setOnClickListener { onClick() }
    }

    fun setOnSecondButtonClickListener(onClick: () -> Unit) {
        secondBottomButtonView.setOnClickListener { onClick() }
    }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHMoodSelector, defStyleAttr, 0
        )
        inflateBaseViews()
        initializeViewsWithAttrValues(attributes)
    }

    private fun inflateBaseViews() {
        LayoutInflater.from(context).inflate(R.layout.bh_mood_selector, this, true)
        rootView = findViewById(R.id.bh_mood_selector_root_view)
        backButtonView = findViewById(R.id.bh_mood_selector_back_button)
        titleView = findViewById(R.id.bh_mood_selector_title)
        subtitleView = findViewById(R.id.bh_mood_selector_subtitle)
        answerGreatView = findViewById(R.id.bh_mood_selector_answer_great)
        answerGoodView = findViewById(R.id.bh_mood_selector_answer_good)
        answerOkView = findViewById(R.id.bh_mood_selector_answer_ok)
        answerBetterView = findViewById(R.id.bh_mood_selector_answer_better)
        firstBottomButtonView = findViewById(R.id.bh_mood_selector_button_first_button)
        secondBottomButtonView = findViewById(R.id.bh_mood_selector_button_second_button)
        topGuideline = findViewById(R.id.bh_mood_selector_top_guideline)
    }

    private fun setBackgroundGradient() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(startColor, endColor)
        ).apply {
            shape = GradientDrawable.RECTANGLE
        }
        rootView.background = gradientDrawable
    }

    private fun initializeViewsWithAttrValues(attributes: TypedArray) {
        startColor =
            attributes.getColor(
                R.styleable.BHMoodSelector_moodSelectorStartColor,
                ContextCompat.getColor(context, R.color.bhMoodSelectorStartColor)
            )

        endColor =
            attributes.getColor(
                R.styleable.BHMoodSelector_moodSelectorEndColor,
                ContextCompat.getColor(context, R.color.bhMoodSelectorEndColor)
            )

        backButtonTopMargin = attributes.getDimension(R.styleable.BHMoodSelector_moodSelectorBackButtonTopMargin, resources.getDimension(R.dimen.bh_mood_selector_back_button_margin))

        titleText = attributes.getString(R.styleable.BHMoodSelector_moodSelectorTitleText)
        subtitleText = attributes.getString(R.styleable.BHMoodSelector_moodSelectorSubtitleText)

        answerFirstText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorAnswerFirstText)
        answerSecondText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorAnswerSecondText)
        answerThirdText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorAnswerThirdText)
        answerFourthText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorAnswerFourthText)

        firstButtonText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorFirstButtonText)
        secondButtonText =
            attributes.getString(R.styleable.BHMoodSelector_moodSelectorSecondButtonText)

    }
}
