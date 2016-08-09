package org.brainail.EverboxingTools.ui.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.brainail.EverboxingTools.utils.tool.ToolPhone;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class BezelViewPager extends ViewPager {

    private static final int DEFAULT_GUTTER_SIZE_DP = 96;
    private static final float DEFAULT_GUTTER_SIZE_PERCENT = 0.2f;

    private boolean mIsBezelSwipeEnabled = true;

    private int mDefaultGutterSize;
    private int mGutterSize;

    public BezelViewPager(Context context) {
        super(context);
        init();
    }

    public BezelViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        mGutterSize = mDefaultGutterSize = ToolPhone.dipsToPixels (getContext(), DEFAULT_GUTTER_SIZE_DP);
    }

    public void setIsBezelSwipeEnabled(final boolean isEnabled) {
        mIsBezelSwipeEnabled = isEnabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mGutterSize = Math.min(Math.round(getMeasuredWidth() * DEFAULT_GUTTER_SIZE_PERCENT), mDefaultGutterSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (! mIsBezelSwipeEnabled) {
            return super.canScroll(v, checkV, dx, x, y);
        }

        return ! (x < mGutterSize || x > getWidth() - mGutterSize);
    }

}

