package org.brainail.EverboxingLexis.utils.tool;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import org.brainail.EverboxingLexis.JApplication;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.Sdk;

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
public final class ToolPhone {

    // Converts dp/dip to pixels.
    public static int dipsToPixels(final float dips) {
        return dipsToPixels(JApplication.appContext(), dips);
    }

    // Converts dp/dip to pixels.
    public static int dipsToPixels(final Context context, final float dips) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, displayMetrics) + 0.5f);
    }

    // The absolute width of the display in pixels.
    public static int screenWidthInPixels(final Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    // The absolute height of the display in pixels.
    public static int screenHeightInPixels(final Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    // The absolute width of the display in dp/dip.
    public static int screenWidth(final Context context) {
        return (int) (((screenWidthInPixels(context) + .0f) / screenDensity(context)) + 0.5f);
    }

    // The absolute height of the display in dp/dip.
    public static int screenHeight(final Context context) {
        return (int) (((screenHeightInPixels(context) + .0f) / screenDensity(context)) + 0.5f);
    }

    // The logical density of the display.
    public static float screenDensity(final Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int screenSw(final Context context) {
        return Math.min(screenWidth(context), screenHeight(context));
    }

    // http://stackoverflow.com/a/23861333
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Point realScreenSize(Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        int screenWidth, screenHeight;

        if (Sdk.isSdkSupported(Sdk.JELLY_BEAN_MR1)) {
            final DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            screenWidth = realMetrics.widthPixels;
            screenHeight = realMetrics.heightPixels;
        } else if (Sdk.isSdkSupported(Sdk.ICE_CREAM_SANDWICH)) {
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                screenHeight = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
            } catch (Exception exception) {
                screenWidth = display.getWidth();
                screenHeight = display.getHeight();
            }
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }

        return new Point(screenWidth, screenHeight);
    }

    public static boolean isTablet() {
        return JApplication.appContext().getResources().getBoolean(R.bool.is_tablet);
    }

}
