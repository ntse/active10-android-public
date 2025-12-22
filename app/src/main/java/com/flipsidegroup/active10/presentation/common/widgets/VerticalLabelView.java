package com.flipsidegroup.active10.presentation.common.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.flipsidegroup.active10.R;


public class VerticalLabelView extends View {
    final static int DEFAULT_TEXT_SIZE = 15;
    private String mText = "";
    private TextPaint mTextPaint;
    private Rect text_bounds = new Rect();

    public VerticalLabelView(Context context) {
        super(context);
        initLabelView();
    }

    public VerticalLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLabelView();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalLabelView);

        CharSequence s = a.getString(R.styleable.VerticalLabelView_text);
        if (s != null) setText(s.toString());

        setTextColor(a.getColor(R.styleable.VerticalLabelView_textColor, 0xFF000000));

        int textSize = a.getDimensionPixelOffset(R.styleable.VerticalLabelView_textSize, 0);
        if (textSize > 0) setTextSize(textSize);

        a.recycle();
    }

    private void initLabelView() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_regular));
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setColor(0xFF000000);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        setPadding(10, 10, 10, 10);
    }

    public void setText(String text) {
        mText = text;
        requestLayout();
        invalidate();
    }

    public void setTextSize(int size) {
        mTextPaint.setTextSize(size);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.getTextBounds(mText, 0, mText.length(), text_bounds);
        int desiredWidth = text_bounds.height() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = text_bounds.width() + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float text_horizontally_centered_origin_x = getWidth() / 2f;
        float text_horizontally_centered_origin_y = getHeight() / 2f;

        canvas.save();
        canvas.translate(text_horizontally_centered_origin_x, text_horizontally_centered_origin_y);
        canvas.rotate(-90);
        canvas.drawText(mText, -text_bounds.width() / 2f, text_bounds.height() / 2f, mTextPaint);
        canvas.restore();
    }
}