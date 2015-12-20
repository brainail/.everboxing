package org.brainail.EverboxingLexis.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.brainail.EverboxingLexis.R;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class WaveProgress extends View {

    /**
     * +------------------------+
     * | wave length            |__________
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|_______
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|_______
     */

    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;
    private static final int DEFAULT_WAVE_PROGRESS_VALUE = 50;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor ("#212121");
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor ("#212121");
    private static final float DEFAULT_BORDER_WIDTH = 0;

    private static final int DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE.ordinal ();
    private static final float DEFAULT_TITLE_TOP_SIZE = 18.0f;
    private static final float DEFAULT_TITLE_CENTER_SIZE = 22.0f;
    private static final float DEFAULT_TITLE_BOTTOM_SIZE = 18.0f;

    public enum ShapeType {
        CIRCLE,
        SQUARE
    }

    // Dynamic Properties.
    private int mCanvasSize;
    private float mAmplitudeRatio;
    private int mWaveColor;
    private int mShapeType;

    // Properties.
    private String mTopTitle;
    private String mCenterTitle;
    private String mBottomTitle;
    private float mDefaultWaterLevel;
    private float mWaterLevelRatio = 1f;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    private int mProgressValue = DEFAULT_WAVE_PROGRESS_VALUE;

    // Object used to draw.
    // Shader containing repeated waves.
    private BitmapShader mWaveShader;
    // Shader matrix.
    private Matrix mShaderMatrix;
    // Paint to draw wave.
    private Paint mWavePaint;
    // Paint to draw border.
    private Paint mBorderPaint;
    // Point to draw title.
    private Paint mTopTitlePaint;
    private Paint mBottomTitlePaint;
    private Paint mCenterTitlePaint;

    // Animation.
    private AnimatorSet mAnimatorSet;

    private Context mContext;

    // Constructor & Init Method.
    public WaveProgress (final Context context) {
        this (context, null);
    }

    public WaveProgress (Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public WaveProgress (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        init (context, attrs, defStyleAttr);
    }

    private void init (Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        // Init Wave.
        mShaderMatrix = new Matrix ();
        mWavePaint = new Paint ();
        // The ANTI_ALIAS_FLAG bit AntiAliasing smooths out the edges of what is being drawn,
        // but is has no impact on the interior of the shape.
        mWavePaint.setAntiAlias (true);

        // Init Animation
        initAnimation ();

        // Load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes (attrs, R.styleable.WaveProgress, defStyleAttr, 0);

        // Init ShapeType
        mShapeType = attributes.getInteger (R.styleable.WaveProgress_mlv_shapeType, DEFAULT_WAVE_SHAPE);

        // Init Wave
        mWaveColor = attributes.getColor (R.styleable.WaveProgress_mlv_waveColor, DEFAULT_WAVE_COLOR);

        // Init AmplitudeRatio
        float amplitudeRatioAttr = attributes.getFloat (
                R.styleable.WaveProgress_mlv_waveAmplitude, DEFAULT_AMPLITUDE_RATIO) / 1000;
        mAmplitudeRatio = (amplitudeRatioAttr > DEFAULT_AMPLITUDE_RATIO)
                ? DEFAULT_AMPLITUDE_RATIO : amplitudeRatioAttr;

        // Init Progress
        mProgressValue = attributes.getInteger (
                R.styleable.WaveProgress_mlv_progressValue, DEFAULT_WAVE_PROGRESS_VALUE);
        setProgressValue (mProgressValue);

        // Init Border
        mBorderPaint = new Paint ();
        mBorderPaint.setAntiAlias (true);
        mBorderPaint.setStyle (Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth (attributes.getDimension (
                R.styleable.WaveProgress_mlv_borderGateWidth, dp2px (DEFAULT_BORDER_WIDTH)));
        mBorderPaint.setColor (attributes.getColor (
                R.styleable.WaveProgress_mlv_borderColor, DEFAULT_WAVE_COLOR));

        // Init Title
        mTopTitlePaint = new Paint ();
        mTopTitlePaint.setColor (attributes.getColor (
                R.styleable.WaveProgress_mlv_titleTopColor, DEFAULT_TITLE_COLOR));
        mTopTitlePaint.setStyle (Paint.Style.FILL);
        mTopTitlePaint.setAntiAlias (true);
        mTopTitlePaint.setTextSize (attributes.getDimension (
                R.styleable.WaveProgress_mlv_titleTopSize, sp2px (DEFAULT_TITLE_TOP_SIZE)));
        mTopTitle = attributes.getString (
                R.styleable.WaveProgress_mlv_titleTop);

        mCenterTitlePaint = new Paint ();
        mCenterTitlePaint.setColor (attributes.getColor (
                R.styleable.WaveProgress_mlv_titleCenterColor, DEFAULT_TITLE_COLOR));
        mCenterTitlePaint.setStyle (Paint.Style.FILL);
        mCenterTitlePaint.setAntiAlias (true);
        mCenterTitlePaint.setTextSize (attributes.getDimension (
                R.styleable.WaveProgress_mlv_titleCenterSize, sp2px (DEFAULT_TITLE_CENTER_SIZE)));
        mCenterTitle = attributes.getString (R.styleable.WaveProgress_mlv_titleCenter);

        mBottomTitlePaint = new Paint ();
        mBottomTitlePaint.setColor (attributes.getColor (
                R.styleable.WaveProgress_mlv_titleBottomColor, DEFAULT_TITLE_COLOR));
        mBottomTitlePaint.setStyle (Paint.Style.FILL);
        mBottomTitlePaint.setAntiAlias (true);
        mBottomTitlePaint.setTextSize (attributes.getDimension (
                R.styleable.WaveProgress_mlv_titleBottomSize, sp2px (DEFAULT_TITLE_BOTTOM_SIZE)));
        mBottomTitle = attributes.getString (R.styleable.WaveProgress_mlv_titleBottom);
    }

    @Override
    public void onDraw (Canvas canvas) {
        mCanvasSize = canvas.getWidth ();
        if (canvas.getHeight () < mCanvasSize) {
            mCanvasSize = canvas.getHeight ();
        }
        // Draw Wave.
        // Modify paint shader according to mShowWave state.
        if (mWaveShader != null) {
            // First call after mShowWave, assign it to our paint.
            if (mWavePaint.getShader () == null) {
                mWavePaint.setShader (mWaveShader);
            }

            // Sacle shader according to waveLengthRatio and amplitudeRatio.
            // This decides the size(waveLengthRatio for width, amplitudeRatio for height) of waves.
            mShaderMatrix.setScale (1, mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0, mDefaultWaterLevel);
            // Translate shader according to waveShiftRatio and waterLevelRatio.
            // This decides the start position(waveShiftRatio for x, waterLevelRatio for y) of waves.
            mShaderMatrix.postTranslate (mWaveShiftRatio * getWidth (),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * getHeight ());

            // Assign matrix to invalidate the shader.
            mWaveShader.setLocalMatrix (mShaderMatrix);

            // Get borderWidth.
            float borderWidth = mBorderPaint.getStrokeWidth ();

            // The default type is circle.
            switch (mShapeType) {
                case 0:
                    if (borderWidth > 0) {
                        canvas.drawCircle (getWidth () / 2f, getHeight () / 2f,
                                (getWidth () - borderWidth) / 2f - 1f, mBorderPaint);
                    }
                    float radius = getWidth () / 2f - borderWidth;
                    canvas.drawCircle (getWidth () / 2f, getHeight () / 2f, radius, mWavePaint);
                    break;
                case 1:
                    if (borderWidth > 0) {
                        canvas.drawRect (
                                borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth () - borderWidth / 2f - 0.5f,
                                getHeight () - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }
                    canvas.drawRect (borderWidth, borderWidth, getWidth () - borderWidth,
                            getHeight () - borderWidth, mWavePaint);
                    break;
            }

            // I know, the code written here is very shit.
            if (!TextUtils.isEmpty (mTopTitle)) {
                float top = mTopTitlePaint.measureText (mTopTitle);
                canvas.drawText (mTopTitle, (getWidth () - top) / 2,
                        getHeight () * 2 / 10.0f, mTopTitlePaint);
            }

            if (!TextUtils.isEmpty (mCenterTitle)) {
                float middle = mCenterTitlePaint.measureText (mCenterTitle);
                // Draw the text centered.
                canvas.drawText (mCenterTitle, (getWidth () - middle) / 2,
                        getHeight () / 2
                                - ((mCenterTitlePaint.descent () + mCenterTitlePaint.ascent ()) / 2), mCenterTitlePaint);
            }

            if (!TextUtils.isEmpty (mBottomTitle)) {
                float bottom = mBottomTitlePaint.measureText (mBottomTitle);
                canvas.drawText (mBottomTitle, (getWidth () - bottom) / 2,
                        getHeight () * 8 / 10.0f
                                - ((mBottomTitlePaint.descent () + mBottomTitlePaint.ascent ()) / 2), mBottomTitlePaint);
            }
        } else {
            mWavePaint.setShader (null);
        }
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged (w, h, oldw, oldh);
        mCanvasSize = w;
        if (h < mCanvasSize)
            mCanvasSize = h;
        updateWaveShader ();
    }


    private void updateWaveShader () {
        double defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / getWidth ();
        float defaultAmplitude = getHeight () * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = getHeight () * DEFAULT_WATER_LEVEL_RATIO;
        float defaultWaveLength = getWidth ();

        Bitmap bitmap = Bitmap.createBitmap (getWidth (), getHeight (), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);

        Paint wavePaint = new Paint ();
        wavePaint.setStrokeWidth (2);
        wavePaint.setAntiAlias (true);

        // Draw default waves into the bitmap.
        // y=Asin(ωx+φ)+h
        final int endX = getWidth () + 1;
        final int endY = getHeight () + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor (adjustAlpha (mWaveColor, 0.3f));
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * defaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + defaultAmplitude * Math.sin (wx));
            canvas.drawLine (beginX, beginY, beginX, endY, wavePaint);
            waveY[beginX] = beginY;
        }

        wavePaint.setColor (mWaveColor);
        final int wave2Shift = (int) (defaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine (beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        // Use the bitamp to create the shader.
        mWaveShader = new BitmapShader (bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        this.mWavePaint.setShader (mWaveShader);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth (widthMeasureSpec);
        int height = measureHeight (heightMeasureSpec);
        int imageSize = (width < height) ? width : height;
        setMeasuredDimension (imageSize, imageSize);
    }

    private int measureWidth (int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode (measureSpec);
        int specSize = MeasureSpec.getSize (measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = mCanvasSize;
        }
        return result;
    }

    private int measureHeight (int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode (measureSpecHeight);
        int specSize = MeasureSpec.getSize (measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number).
            result = mCanvasSize;
        }
        return (result + 2);
    }

    public void setWaveColor (int color) {
        mWaveColor = color;
        // Need to recreate shader when color changed ?
//        mWaveShader = null;
        updateWaveShader ();
        invalidate ();
    }

    public int getWaveColor () {
        return mWaveColor;
    }

    public void setBorderWidth (float width) {
        mBorderPaint.setStrokeWidth (width);
        invalidate ();
    }

    public float getBorderWidth () {
        return mBorderPaint.getStrokeWidth ();
    }

    public void setBorderColor (int color) {
        mBorderPaint.setColor (color);
        updateWaveShader ();
        invalidate ();
    }

    public int getBorderColor () {
        return mBorderPaint.getColor ();
    }

    public void setShapeType (ShapeType shapeType) {
        mShapeType = shapeType.ordinal ();
        invalidate ();
    }

    public int getShapeType () {
        return mShapeType;
    }

    /**
     * Set vertical size of wave according to amplitudeRatio.
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     */
    public void setAmplitudeRatio (int amplitudeRatio) {
        if (this.mAmplitudeRatio != (float) amplitudeRatio / 1000) {
            this.mAmplitudeRatio = (float) amplitudeRatio / 1000;
            invalidate ();
        }
    }

    public float getAmplitudeRatio () {
        return mAmplitudeRatio;
    }

    /**
     * Water level increases from 0 to the value of WaveView.
     *
     * @param progress Default to be 50.
     */
    public void setProgressValue (int progress) {
        mProgressValue = progress;
        ObjectAnimator waterLevelAnim
                = ObjectAnimator.ofFloat (this, "waterLevelRatio", mWaterLevelRatio, 1f - ((float) progress / 100));
        waterLevelAnim.setDuration (1000);
        waterLevelAnim.setInterpolator (new DecelerateInterpolator ());
        AnimatorSet animatorSetProgress = new AnimatorSet ();
        animatorSetProgress.play (waterLevelAnim);
        animatorSetProgress.start ();
    }

    public int getProgressValue () {
        return mProgressValue;
    }

    public void setWaveShiftRatio (float waveShiftRatio) {
        if (this.mWaveShiftRatio != waveShiftRatio) {
            this.mWaveShiftRatio = waveShiftRatio;
            invalidate ();
        }
    }

    public float getWaveShiftRatio () {
        return mWaveShiftRatio;
    }

    public void setWaterLevelRatio (float waterLevelRatio) {
        if (this.mWaterLevelRatio != waterLevelRatio) {
            this.mWaterLevelRatio = waterLevelRatio;
            invalidate ();
        }
    }

    public float getWaterLevelRatio () {
        return mWaterLevelRatio;
    }

    /**
     * Set the title within the WaveView.
     *
     * @param topTitle Default to be null.
     */
    public void setTopTitle (String topTitle) {
        mTopTitle = topTitle;
    }

    public String getTopTitle () {
        return mTopTitle;
    }

    public void setCenterTitle (String centerTitle) {
        mCenterTitle = centerTitle;
    }

    public String getCenterTitle () {
        return mCenterTitle;
    }

    public void setBottomTitle (String bottomTitle) {
        mBottomTitle = bottomTitle;
    }

    public String getBottomTitle () {
        return mBottomTitle;
    }

    public void setTopTitleColor (int topTitleColor) {
        mTopTitlePaint.setColor (topTitleColor);
    }

    public int getTopTitleColor () {
        return mTopTitlePaint.getColor ();
    }

    public void setCenterTitleColor (int centerTitleColor) {
        mCenterTitlePaint.setColor (centerTitleColor);
    }

    public int getCenterTitleColor () {
        return mCenterTitlePaint.getColor ();
    }

    public void setBottomTitleColor (int bottomTitleColor) {
        mBottomTitlePaint.setColor (bottomTitleColor);
    }

    public int getBottomTitleColor () {
        return mBottomTitlePaint.getColor ();
    }

    public void setTopTitleSize (float topTitleSize) {
        mTopTitlePaint.setTextSize (sp2px (topTitleSize));
    }

    public float getsetTopTitleSize () {
        return mTopTitlePaint.getTextSize ();
    }

    public void setCenterTitleSize (float centerTitleSize) {
        mCenterTitlePaint.setTextSize (sp2px (centerTitleSize));
    }

    public float getCenterTitleSize () {
        return mCenterTitlePaint.getTextSize ();
    }

    public void setBottomTitleSize (float bottomTitleSize) {
        mBottomTitlePaint.setTextSize (sp2px (bottomTitleSize));
    }

    public float getBottomTitleSize () {
        return mBottomTitlePaint.getTextSize ();
    }

    private void startAnimation () {
        if (mAnimatorSet != null) {
            mAnimatorSet.start ();
        }
    }

    private void initAnimation () {
        // Wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat (this, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount (ValueAnimator.INFINITE);
        waveShiftAnim.setDuration (1000);
        waveShiftAnim.setInterpolator (new LinearInterpolator ());

        mAnimatorSet = new AnimatorSet ();
        mAnimatorSet.play (waveShiftAnim);
    }

    private void cancel () {
        if (mAnimatorSet != null) {
            mAnimatorSet.end ();
        }
    }

    @Override
    protected void onAttachedToWindow () {
        startAnimation ();
        super.onAttachedToWindow ();
    }

    @Override
    protected void onDetachedFromWindow () {
        cancel ();
        super.onDetachedFromWindow ();
    }

    /**
     * Transparent the given color by the factor
     * The more the factor closer to zero the more the color gets transparent
     *
     * @param color  The color to transparent
     * @param factor 1.0f to 0.0f
     * @return int - A transplanted color
     */
    private int adjustAlpha (int color, float factor) {
        int alpha = Math.round (Color.alpha (color) * factor);
        int red = Color.red (color);
        int green = Color.green (color);
        int blue = Color.blue (color);
        return Color.argb (alpha, red, green, blue);
    }

    /**
     * Paint.setTextSize(float textSize) default unit is px.
     *
     * @param spValue The real size of text
     * @return int - A transplanted sp
     */
    public int sp2px (float spValue) {
        final float fontScale = mContext.getResources ().getDisplayMetrics ().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    protected int dp2px (float dp) {
        final float scale = mContext.getResources ().getDisplayMetrics ().density;
        return (int) (dp * scale + 0.5f);
    }

}
