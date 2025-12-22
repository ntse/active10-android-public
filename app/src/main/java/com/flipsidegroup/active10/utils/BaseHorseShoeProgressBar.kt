package com.flipsidegroup.active10.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.flipsidegroup.active10.R
import kotlin.math.min
import kotlin.math.sqrt

const val DIVIDER_PAINT_STROKE_WIDTH = 2f
const val ARC_ANGLE = 360 * 0.8f
const val MAX_PROGRESS = 10

open class HorseShoe @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    protected val defaultTextSize = min(
        UIUtils.getDimension(R.dimen.text_size_26),
        UIUtils.getDimension(R.dimen.horse_shoe_max_text_size)
    )

    protected open val minFontSize = UIUtils.getDimension(R.dimen.text_size_20)
    protected open val robotoCondensedBold = ResourcesCompat.getFont(context, R.font.roboto_condensed_bold)
    protected open val robotoBold = ResourcesCompat.getFont(context, R.font.roboto_bold)
    protected open var finishedStrokeColor = UIUtils.getColor(R.color.colorAccent)
    protected open var unfinishedStrokeColor = UIUtils.getColor(R.color.horseshoe_stroke_color)
    protected open var greyishBrown = UIUtils.getColor(R.color.greyish_brown)
    protected var greyishBlack = UIUtils.getColor(R.color.black_33)
    protected open var black = UIUtils.getColor(R.color.black)
    protected open var unfinishedProgressColor = UIUtils.getColor(R.color.white)

    protected open val finishedStrokeWidth =
        UIUtils.getDimension(R.dimen.arc_progress_finished_stroke_width)
    protected open val unfinishedStrokeWidth =
        UIUtils.getDimension(R.dimen.arc_progress_unfinished_stroke_width)
    protected open val myWalksFinishedStrokeWidth: Float? = null

    protected lateinit var unfinishedProgressPaint: Paint
    protected lateinit var firstProgressPaint: Paint
    protected lateinit var finishedProgressPaint: Paint

    protected lateinit var currentProgressPaint: Paint
    protected lateinit var minTextPaint: Paint
    protected lateinit var maxProgressPaint: Paint

    protected var progress: Int = 0
    protected var maxProgress: Int = MAX_PROGRESS

    protected val rectF = RectF()
    private val contentRectF = RectF()

    init {
        initPaints()
    }

    protected open fun initPaints() {
        setUpUnfinishedProgressPaint()
        setUpFirstProgressPaint()
        setUpFinishedProgressPaint()
        initCurrentProgressPaint()
        initMinPaint()
        initMaxProgressPaint()
    }

    override fun invalidate() {
        initPaints()
        super.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val floatWidth = width.toFloat()

        val offset = unfinishedStrokeWidth / 2f

        rectF.set(
            0f + offset,
            0f + offset,
            floatWidth - offset,
            floatWidth - offset
        )

        val radius = width / 2f - unfinishedStrokeWidth
        val length = radius / sqrt(2f)
        contentRectF.set(
            width / 2 - length,
            width / 2 - length,
            width / 2 + length,
            width / 2 + length
        )
    }


    fun getContentRectF(): RectF = contentRectF

    fun getMaximumProgress(): Int {
        return MAX_PROGRESS
    }

    open fun updateProgress(progress: Int) {
        this.progress = progress
        if (progress > maxProgress) {
            this.progress = maxProgress
        }
        invalidate()
    }

    protected fun setUpUnfinishedProgressPaint() {
        unfinishedProgressPaint = Paint()
        unfinishedProgressPaint.color = unfinishedProgressColor
        unfinishedProgressPaint.isAntiAlias = true
        unfinishedProgressPaint.strokeWidth = finishedStrokeWidth
        unfinishedProgressPaint.style = Paint.Style.STROKE
        unfinishedProgressPaint.strokeCap = Paint.Cap.ROUND
    }

    protected fun setUpFirstProgressPaint() {
        firstProgressPaint = Paint()
        firstProgressPaint.color = finishedStrokeColor
        firstProgressPaint.isAntiAlias = true
        firstProgressPaint.strokeWidth = myWalksFinishedStrokeWidth ?: finishedStrokeWidth
        firstProgressPaint.style = Paint.Style.STROKE
        firstProgressPaint.strokeCap = Paint.Cap.ROUND
    }

    protected fun setUpFinishedProgressPaint() {
        finishedProgressPaint = Paint()
        finishedProgressPaint.color = finishedStrokeColor
        finishedProgressPaint.isAntiAlias = true
        finishedProgressPaint.strokeWidth = finishedStrokeWidth
        finishedProgressPaint.style = Paint.Style.STROKE
        finishedProgressPaint.strokeCap = Paint.Cap.BUTT
    }

    protected fun initCurrentProgressPaint() {
        currentProgressPaint = TextPaint()
        currentProgressPaint.color = greyishBrown
        currentProgressPaint.typeface = robotoCondensedBold
        currentProgressPaint.textSize = defaultTextSize
        currentProgressPaint.isAntiAlias = true
    }

    private fun initMinPaint() {
        minTextPaint = TextPaint()
        minTextPaint.color = black
        minTextPaint.typeface = robotoBold
        minTextPaint.textSize = minFontSize
        minTextPaint.isAntiAlias = true
    }

    protected open fun initMaxProgressPaint() {
        maxProgressPaint = TextPaint()
        maxProgressPaint.color = greyishBrown
        maxProgressPaint.typeface = robotoCondensedBold
        maxProgressPaint.textSize = defaultTextSize
        maxProgressPaint.isAntiAlias = true
    }
}
