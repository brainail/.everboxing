package org.brainail.EverboxingTools.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
public class AutoFitGridRecyclerView extends RecyclerView {

    // Min width of each column
    private int mColumnWidth = -1;

    private GridLayoutManager mLayoutManager;

    public AutoFitGridRecyclerView (Context context) {
        super (context);
        init (context, null);
    }

    public AutoFitGridRecyclerView (Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        init (context, attrs);
    }

    public AutoFitGridRecyclerView (Context context, @Nullable AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        init (context, attrs);
    }

    private void init (final Context context, final @Nullable AttributeSet attrs) {
        // From attrs
        if (null != attrs) {
            final int [] attrsArray = {android.R.attr.columnWidth};
            final TypedArray styledAttrs = context.obtainStyledAttributes (attrs, attrsArray);
            mColumnWidth = styledAttrs.getDimensionPixelSize (0, -1);
            styledAttrs.recycle ();
        }

        if (mColumnWidth < 0) {
            mColumnWidth = 1 << 7;
        }

        mLayoutManager = new GridLayoutManager (context, 1);
        setLayoutManager (mLayoutManager);
    }

    @Override
    protected void onMeasure (int widthSpec, int heightSpec) {
        super.onMeasure (widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            final int spanCount = Math.max (1, getMeasuredWidth () / mColumnWidth);
            mLayoutManager.setSpanCount (spanCount);
        }
    }

}
