package org.brainail.EverboxingLexis.ui.views;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import org.brainail.EverboxingLexis.utils.tool.ToolStrings;

import java.lang.reflect.Field;

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
public class MarqueeSubtitleToolbar extends Toolbar {

    private TextView mSubtitle;

    public MarqueeSubtitleToolbar (Context context) {
        super (context);
        setSubtitle(! TextUtils.isEmpty (getSubtitle ()) ? getSubtitle () : ToolStrings.EMPTY);
    }

    public MarqueeSubtitleToolbar (Context context, AttributeSet attrs) {
        super (context, attrs);
        setSubtitle(! TextUtils.isEmpty (getSubtitle ()) ? getSubtitle () : ToolStrings.EMPTY);
    }

    public MarqueeSubtitleToolbar (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        setSubtitle(! TextUtils.isEmpty (getSubtitle ()) ? getSubtitle () : ToolStrings.EMPTY);
    }

    @Override
    public void setSubtitle (CharSequence title) {
        if (! reflected) {
            reflected = reflectSubtitle ();
        }

        super.setSubtitle (title);
        selectSubtitle ();
    }

    @Override
    public void setSubtitle (int resId) {
        if (! reflected) {
            reflected = reflectSubtitle ();
        }

        super.setSubtitle (resId);
        selectSubtitle ();
    }

    boolean reflected = false;

    private boolean reflectSubtitle () {
        try {
            Field field = Toolbar.class.getDeclaredField ("mSubtitleTextView");
            field.setAccessible (true);
            mSubtitle = (TextView) field.get (this);
            mSubtitle.setEllipsize (TextUtils.TruncateAt.MARQUEE);
            mSubtitle.setMarqueeRepeatLimit (-1);
            return true;
        } catch (final Exception exception) {
            return false;
        }
    }

    public void selectSubtitle () {
        if (mSubtitle != null) {
            mSubtitle.setSelected (true);
        }
    }

}
