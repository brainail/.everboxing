package org.brainail.EverboxingTools.utils.text.spannable;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

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
@SuppressLint ("ParcelCreator")
public class ColoredURLSpan extends URLSpan {

    private final int mUrlColor;
    private final boolean mIsUnderline;

    public ColoredURLSpan(String url, int color) {
        this(url, color, true);
    }

    public ColoredURLSpan(String url, int color, boolean isUnderline) {
        super(url);
        mUrlColor = color;
        mIsUnderline = isUnderline;
    }

    public ColoredURLSpan(Parcel src) {
        super(src);
        mUrlColor = src.readInt();
        mIsUnderline = 0 != src.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mUrlColor);
        dest.writeByte((byte) (mIsUnderline ? 1 : 0));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mUrlColor);
        ds.setUnderlineText(mIsUnderline);
    }

}
