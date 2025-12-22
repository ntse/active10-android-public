package com.flipsidegroup.active10.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import com.flipsidegroup.active10.R

class TodaysWalkHorseShoeProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : HorseShoe(context, attrs) {

    private val centerTextSize = UIUtils.getDimension(R.dimen.horse_shoe_progress_text_size)

    var shouldDrawMinMax: Boolean = true
    private val bottomText = UIUtils.getString(R.string.horse_shoe_brisk_min)

    private lateinit var borderPaint: Paint
    private lateinit var dividerPaint: Paint
    private lateinit var bottomTextPaint: Paint
    private lateinit var centerTextPaint: Paint

    init {
        initPaints()
    }

    override fun initPaints() {
        super.initPaints()
        initBottomTextPaint()
        setUpBorderPaint()
        setUpDividerPaint()
        initCenterTextPaint()
        initMaxProgressPaint()
    }

    override fun initMaxProgressPaint() {
        maxProgressPaint = TextPaint()
        maxProgressPaint.color = greyishBlack
        maxProgressPaint.typeface = robotoBold
        maxProgressPaint.textSize = minFontSize
        maxProgressPaint.isAntiAlias = true
    }

    override fun invalidate() {
        initPaints()
        super.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle = 270 - ARC_ANGLE / 2f
        val finishedSweepAngle = progress.toFloat() / maxProgress.toFloat() * ARC_ANGLE
        var finishedStartAngle = startAngle
        if (progress == 0) finishedStartAngle = 0.01f

        // Draw outer border
        canvas.drawArc(rectF, startAngle, ARC_ANGLE, false, borderPaint)
        // Draw unfinished paint
        canvas.drawArc(rectF, startAngle, ARC_ANGLE, false, unfinishedProgressPaint)

        // Draw finished paint
        if (progress == maxProgress) {
            canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, firstProgressPaint)
        } else {
            canvas.drawArc(
                rectF,
                finishedStartAngle,
                finishedSweepAngle / 2,
                false,
                firstProgressPaint
            )
            canvas.drawArc(
                rectF,
                finishedStartAngle,
                finishedSweepAngle,
                false,
                finishedProgressPaint
            )
        }

        // Draw current progress
        if (shouldDrawMinMax) {
            val currentProgressText =
                UIUtils.getQuantityString(R.plurals.progress_mins, progress)
            canvas.drawText(
                currentProgressText,
                width / 2f - width / 2 * 0.47f - currentProgressPaint.measureText(
                    currentProgressText
                ),
                height.toFloat(),
                maxProgressPaint
            )

            // Draw max progress
            val maxProgressText =
                UIUtils.getQuantityString(R.plurals.progress_mins, maxProgress)
            canvas.drawText(
                maxProgressText,
                width - finishedStrokeWidth * resources.getFraction(
                    R.fraction.today_total_walk_proportion,
                    10,
                    1
                ),
                height.toFloat(),
                maxProgressPaint
            )
        }

        // Draw bottom text
        val pos = getContentRectF().height() + getContentRectF().top
        canvas.drawText(
            bottomText,
            (width - bottomTextPaint.measureText(bottomText)) / 2f,
            pos - pos * 0.08f,
            bottomTextPaint
        )

        // Draw center text
        val centerText = progress.toString()
        if (progress == 0) {
            canvas.drawText(
                centerText,
                width / 2f - centerTextPaint.measureText(centerText) / 2,
                height / 2f + unfinishedStrokeWidth / 3,
                centerTextPaint
            )
        }

        // Draw dividers
        val initialRotateAngle = -(180 - startAngle)
        canvas.rotate(initialRotateAngle, height / 2f, width / 2f)

        val rotateAngle = ARC_ANGLE / 10f
        for (i in 1 until MAX_PROGRESS) {
            canvas.rotate(rotateAngle, height / 2f, width / 2f)
            canvas.drawLine(0f, height / 2f, unfinishedStrokeWidth, height / 2f, dividerPaint)
        }
    }


    private fun setUpBorderPaint() {
        borderPaint = Paint()
        borderPaint.color = unfinishedStrokeColor
        borderPaint.isAntiAlias = true
        borderPaint.strokeWidth = unfinishedStrokeWidth
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeCap = Paint.Cap.ROUND
    }

    private fun setUpDividerPaint() {
        dividerPaint = Paint()
        dividerPaint.color = unfinishedStrokeColor
        dividerPaint.isAntiAlias = true
        dividerPaint.strokeWidth = DIVIDER_PAINT_STROKE_WIDTH
        dividerPaint.style = Paint.Style.STROKE
    }

    private fun initBottomTextPaint() {
        bottomTextPaint = TextPaint()
        bottomTextPaint.color = greyishBrown
        bottomTextPaint.typeface = robotoCondensedBold
        bottomTextPaint.textSize = defaultTextSize
        bottomTextPaint.isAntiAlias = true
    }

    private fun initCenterTextPaint() {
        centerTextPaint = TextPaint()
        centerTextPaint.color = greyishBrown
        centerTextPaint.typeface = robotoCondensedBold
        centerTextPaint.textSize = centerTextSize
        centerTextPaint.isAntiAlias = true
    }
}
