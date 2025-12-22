package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.phe.betterhealth.widgets.utils.dpToPx

class HighlightView(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    private var horizontalMargin = 20f
    private var cornerRadius = 20f

    var topPosition = 200f
    var bottomPosition = 200f

    private val eraserPaint: Paint
    private val basicPaint: Paint

    init {
        val xFerMode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        basicPaint = Paint()
        eraserPaint = Paint()
        eraserPaint.color = 0xFFFFFF
        eraserPaint.alpha = 0
        eraserPaint.xfermode = xFerMode
        eraserPaint.isAntiAlias = true

        context?.let {
            horizontalMargin = 12.dpToPx(it)
            cornerRadius = 10.dpToPx(it)
        }

    }

    override fun dispatchDraw(canvas: Canvas) {
        val location = IntArray(2)
        getLocationOnScreen(location)
        val overlay = Bitmap.createBitmap(
            measuredWidth, measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val overlayCanvas = Canvas(overlay)
        overlayCanvas.drawColor(-0x47ffffff) // 0x3333B5E5);
        eraserPaint.alpha = 0

        val left = horizontalMargin
        val right = width - horizontalMargin
        overlayCanvas.drawRoundRect(
            left,
            topPosition,
            right,
            bottomPosition,
            cornerRadius,
            cornerRadius,
            eraserPaint
        )

        canvas.drawBitmap(overlay, 0f, 0f, basicPaint)
        super.dispatchDraw(canvas)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}