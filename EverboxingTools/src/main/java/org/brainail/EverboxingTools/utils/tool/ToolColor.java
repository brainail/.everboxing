package org.brainail.EverboxingTools.utils.tool;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

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
public final class ToolColor {

    private static final float HSV_DARK_COLOR_VALUE_LIMIT = 0.5f;
    private static final float HSV_DARKEN_COLOR_COEFF = 0.8f;

    public static boolean isDarkColor (final int color) {
        final float[] hsvComponents = new float[3];
        Color.colorToHSV (color, hsvComponents);
        return hsvComponents[2] < HSV_DARK_COLOR_VALUE_LIMIT;
    }

    public static int darkenColor (final int color) {
        final float[] hsvComponents = new float[3];
        Color.colorToHSV (color, hsvComponents);
        hsvComponents[2] *= HSV_DARKEN_COLOR_COEFF;
        return Color.HSVToColor (hsvComponents);
    }

    /**
     * Returns color with some alpha percent.
     *
     * @param alphaPercent Alpha component [0% .. 100%] of the color.
     * @see android.graphics.Color#argb(int, int, int, int)
     */
    public static int withAlpha (final int color, final float alphaPercent) {
        final float opaquePercent = 100 - alphaPercent;
        final int alpha = Math.max (0, Math.min (255, (int) (255 * opaquePercent / 100.0f)));
        return Color.argb (alpha, Color.red (color), Color.green (color), Color.blue (color));
    }

    public static int by (final Context context, final int resId) {
        return context.getResources ().getColor (resId);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult @ColorInt int modifyAlpha (
            @ColorInt int color,
            @IntRange (from = 0, to = 255) int alpha) {

        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult @ColorInt int modifyAlpha (
            @ColorInt int color,
            @FloatRange (from = 0f, to = 1f) float alpha) {

        return modifyAlpha (color, (int) (255f * alpha));
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 0.0 will return {@code color1}, 0.5 will give an even blend,
     *              1.0 will return {@code color2}.
     */
    public static @CheckResult @ColorInt int blendColors (
            @ColorInt int color1,
            @ColorInt int color2,
            @FloatRange (from = 0f, to = 1f) float ratio) {

        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha (color1) * inverseRatio) + (Color.alpha (color2) * ratio);
        float r = (Color.red (color1) * inverseRatio) + (Color.red (color2) * ratio);
        float g = (Color.green (color1) * inverseRatio) + (Color.green (color2) * ratio);
        float b = (Color.blue (color1) * inverseRatio) + (Color.blue (color2) * ratio);
        return Color.argb ((int) a, (int) r, (int) g, (int) b);
    }


}
