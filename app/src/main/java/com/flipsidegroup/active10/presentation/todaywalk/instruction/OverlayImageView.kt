package com.flipsidegroup.active10.presentation.todaywalk.instruction

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.graphics.toRectF
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.UIUtils

private const val OVERLAY_ALPHA = 245

class OverlayImageView : ImageView {

    private var circleRect: Rect? = null
    private var radius: Float = 0f
    private var bitmap: Bitmap? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun init(width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = UIUtils.getColor(R.color.colorPrimary)
        paint.style = Paint.Style.FILL
        paint.alpha = OVERLAY_ALPHA
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawPaint(paint)
            circleRect?.let { rect ->
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                canvas.drawRoundRect(rect.toRectF(), radius, radius, paint)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    fun setCircle(rect: Rect?, radius: Float, width: Int, height: Int) {
        this.circleRect = rect
        this.radius = radius
        init(width, height)
        //Redraw after defining circle
        postInvalidate()
    }

}