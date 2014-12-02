package org.brainail.Everboxing.utils;

import android.os.Build;

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
public final class Sdk {

    /**
     * @see android.os.Build.VERSION_CODES#GINGERBREAD
     */
    public static final int GINGERBREAD = Build.VERSION_CODES.GINGERBREAD;

    /**
     * @see android.os.Build.VERSION_CODES#GINGERBREAD_MR1
     */
    public static final int GINGERBREAD_MR1 = Build.VERSION_CODES.GINGERBREAD_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB
     */
    public static final int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB_MR1
     */
    public static final int HONEYCOMB_MR1 = Build.VERSION_CODES.HONEYCOMB_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB_MR2
     */
    public static final int HONEYCOMB_MR2 = Build.VERSION_CODES.HONEYCOMB_MR2;

    /**
     * @see android.os.Build.VERSION_CODES#ICE_CREAM_SANDWICH
     */
    public static final int ICE_CREAM_SANDWICH = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    /**
     * @see android.os.Build.VERSION_CODES#ICE_CREAM_SANDWICH_MR1
     */
    public static final int ICE_CREAM_SANDWICH_MR1 = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN
     */
    public static final int JELLY_BEAN = Build.VERSION_CODES.JELLY_BEAN;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN_MR1
     */
    public static final int JELLY_BEAN_MR1 = Build.VERSION_CODES.JELLY_BEAN_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN_MR2
     */
    public static final int JELLY_BEAN_MR2 = Build.VERSION_CODES.JELLY_BEAN_MR2;

    /**
     * @see android.os.Build.VERSION_CODES#KITKAT
     */
    public static final int KITKAT = Build.VERSION_CODES.KITKAT;

    /**
     * @see android.os.Build.VERSION_CODES#LOLLIPOP
     */
    public static final int LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;

    public static boolean isSdkSupported(final int sdkVersion) {
        return Build.VERSION.SDK_INT >= sdkVersion;
    }

}
