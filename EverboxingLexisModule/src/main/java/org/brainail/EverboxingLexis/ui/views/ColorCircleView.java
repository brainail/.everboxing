package org.brainail.EverboxingLexis.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.brainail.EverboxingLexis.R;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 *
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class ColorCircleView extends View {

    private int mCircleRadius = 0;
    private int mStrokeWidth = 0;
    private int mCircleInnerGap = 0;
    private int mFillColor = Color.TRANSPARENT;
    private int mStrokeColor = Color.TRANSPARENT;

    public ColorCircleView(Context context) {
        super(context, null);
    }

    public ColorCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        init();
    }

    private void initAttrs(final Context context, AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView, defStyleAttr, 0);

            try {
                mStrokeColor = typedArray.getColor(R.styleable.ColorCircleView_strokeColor, mStrokeColor);
                mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ColorCircleView_strokeWidth, mStrokeWidth);
                mFillColor = typedArray.getColor(R.styleable.ColorCircleView_fillColor, mFillColor);
                mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.ColorCircleView_circleRadius, mCircleRadius);
                mCircleInnerGap = typedArray.getDimensionPixelSize(R.styleable.ColorCircleView_circleInnerGap, mCircleInnerGap);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init() {
        setSaveEnabled(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth(), height = getHeight();
        final int areaSize = Math.min(width, height);
        final int circleSize = Math.min(areaSize, mCircleRadius);
        final float cX = areaSize / 2.0f, cY = areaSize / 2.0f;

        canvas.drawCircle(cX, cY, circleSize, getFill());
        canvas.drawCircle(cX, cY, circleSize - mCircleInnerGap, getStroke());
    }

    private Paint getStroke() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setColor(mStrokeColor);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getFill() {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mFillColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public void setCircleRadius(int circleRadius) {
        mCircleRadius = circleRadius;
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        invalidate();
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
        invalidate();
    }

    public void setCircleInnerGap(int circleInnerGap) {
        mCircleInnerGap = circleInnerGap;
    }

}