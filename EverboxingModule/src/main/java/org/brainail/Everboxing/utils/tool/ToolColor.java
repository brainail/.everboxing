package org.brainail.Everboxing.utils.tool;

import android.graphics.Color;

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
public final class ToolColor {

    private static final float HSV_DARK_COLOR_VALUE_LIMIT = 0.5f;

    public static boolean isDarkColor(final int color) {
        final float [] hsvComponents = new float [3];
        Color.colorToHSV(color, hsvComponents);
        return hsvComponents [2] < HSV_DARK_COLOR_VALUE_LIMIT;
    }

    /**
     * Returns color with some alpha percent.
     *
     * @param alphaPercent Alpha component [0% .. 100%] of the color.
     *
     * @see android.graphics.Color#argb(int, int, int, int)
     */
    public static int colorWithAlpha(final int color, final float alphaPercent) {
        final float opaquePercent = 100 - alphaPercent;
        final int alpha = Math.max(0, Math.min(255, (int) (255 * opaquePercent / 100.0f)));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

}
