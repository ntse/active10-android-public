package com.flipsidegroup.active10.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import com.flipsidegroup.active10.R
import kotlin.math.abs

private const val ANIMATION_STEP = 10

class MyWalkHorseShoeProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : HorseShoe(context, attrs) {

    private var isEmpty: Boolean = false

    var animate = true
    var useGreyBackground = false

    override val finishedStrokeWidth =
        UIUtils.getDimension(R.dimen.arc_walk_progress_finished_stroke_width)
    override val unfinishedStrokeWidth =
        UIUtils.getDimension(R.dimen.arc_walk_progress_unfinished_stroke_width)
    override val myWalksFinishedStrokeWidth = if (useGreyBackground)
        UIUtils.getDimension(R.dimen.arc_my_walk_progress_finished_stroke_width)
    else
        UIUtils.getDimension(R.dimen.widget_inner_stroke)

    override val minFontSize: Float = UIUtils.getDimension(R.dimen.text_size_28)

    private var mHandler: Handler
    private var finishedSweepAngle: Int = 0

    private var valueAnimator: ValueAnimator? = null

    init {
        initPaints()
        mHandler = Handler(Looper.getMainLooper())
    }

    override fun initPaints() {
        setUpUnfinishedProgressPaint()
        setUpFirstProgressPaint()
        setUpFinishedProgressPaint()
        initCurrentProgressPaint()
        initMaxProgressPaint()
    }

    override fun invalidate() {
        initPaints()
        super.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle = 270 - ARC_ANGLE / 2f

        // Draw unfinished paint
        canvas.drawArc(rectF, startAngle, ARC_ANGLE, false, unfinishedProgressPaint)
        // Draw finished paint
        canvas.drawArc(rectF, startAngle, finishedSweepAngle.toFloat(), false, firstProgressPaint)
    }

    private fun animate(currentSweep: Int, increase: Int, finalSweep: Int, offset: Int) {
        valueAnimator?.cancel()

        valueAnimator = ValueAnimator.ofInt(currentSweep, finalSweep)
            .apply {
                duration = abs(finalSweep - currentSweep).toLong() * 5
                addUpdateListener {
                    finishedSweepAngle = it.animatedValue as Int
                    invalidate()
                }
                start()
            }
    }

    private fun sweep(currentSweep: Int, increase: Int, finalSweep: Int, offset: Int) {
        if (currentSweep + offset == finalSweep) {
            this.finishedSweepAngle = finalSweep
        } else {
            this.finishedSweepAngle = currentSweep
            sweep(currentSweep + increase, increase, finalSweep, offset)
        }
    }

    override fun updateProgress(progress: Int) {
        this.progress = progress
        if (progress > maxProgress) {
            this.progress = maxProgress
        }
        val oldSweepAngle = finishedSweepAngle
        val newSweepAngle = (progress.toFloat() / maxProgress.toFloat() * ARC_ANGLE).toInt()
        val increase = newSweepAngle.compareTo(oldSweepAngle)
        val offset = (newSweepAngle - oldSweepAngle) % ANIMATION_STEP
        if (animate)
            animate(oldSweepAngle, ANIMATION_STEP * increase, newSweepAngle, offset)
        else
            sweep(oldSweepAngle, ANIMATION_STEP * increase, newSweepAngle, offset)
    }

    fun setUpMyWalks() {
        isEmpty = false
        unfinishedProgressPaint.color =
            if (useGreyBackground)
                UIUtils.getColor(R.color.light_gray)
            else
                UIUtils.getColor(R.color.colorPrimary)
        finishedStrokeColor = UIUtils.getColor(R.color.colorAccent)
        unfinishedProgressColor = if (useGreyBackground)
            UIUtils.getColor(R.color.colorAccent)
        else
            UIUtils.getColor(R.color.colorPrimary)
        greyishBrown = UIUtils.getColor(R.color.black)
    }

    fun setMaximumProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
    }

    fun setUpEmptyMyWalks() {
        isEmpty = true

        unfinishedStrokeColor = UIUtils.getColor(R.color.very_light_grey)
        finishedStrokeColor = UIUtils.getColor(R.color.black_transparent_20)
        unfinishedProgressColor = UIUtils.getColor(R.color.very_light_grey)

        maxProgress = 10
        updateProgress(2)

        greyishBrown = UIUtils.getColor(R.color.black_transparent_20)
        maxProgressPaint.color = UIUtils.getColor(R.color.black_transparent_20)
        currentProgressPaint.color = UIUtils.getColor(R.color.black_transparent_20)
    }
}
