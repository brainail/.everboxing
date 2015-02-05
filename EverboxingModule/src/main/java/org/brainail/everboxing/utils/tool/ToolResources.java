package org.brainail.Everboxing.utils.tool;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Window;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.manager.ThemeManager;

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
public final class ToolResources {

    public static Drawable retrieveThemeDrawable(
            final Context context,
            final int themeResId,
            final int [] attributes) {

        final TypedArray typedArray = context.obtainStyledAttributes(themeResId, attributes);

        try {
            return typedArray.getDrawable(0);
        } finally {
            typedArray.recycle();
        }
    }

    public static int retrieveThemeColor(
            final Context context,
            final int themeResId,
            final int [] attributes,
            final int defaultColor) {

        final TypedArray typedArray = context.obtainStyledAttributes(themeResId, attributes);

        try {
            return typedArray.getColor(0, context.getResources().getColor(defaultColor));
        } finally {
            typedArray.recycle();
        }
    }

    public static Drawable retrieveDrawerCover(final Context context) {
        return retrieveDrawerCover(context, ThemeManager.appTheme().getThemeResId());
    }

    public static Drawable retrieveDrawerCover(final Context context, final int themeResId) {
        return retrieveThemeDrawable(context, themeResId, new int[] {R.attr.drawerCover});
    }

    public static int retrievePrimaryColor(final Context context) {
        return retrievePrimaryColor(context, ThemeManager.appTheme().getThemeResId());
    }

    public static int retrievePrimaryColor(final Context context, final int themeResId) {
        return retrieveThemeColor(context, themeResId, new int [] {R.attr.colorPrimary}, R.color.primary_default);
    }

    public static int retrievePrimaryDarkColor(final Context context) {
        return retrievePrimaryColor(context, ThemeManager.appTheme().getThemeResId());
    }

    public static int retrievePrimaryDarkColor(final Context context, final int themeResId) {
        return retrieveThemeColor(context, themeResId, new int [] {R.attr.colorPrimaryDark}, R.color.primary_default_dark);
    }

    public static int retrieveAccentColor(final Context context) {
        return retrieveAccentColor(context, ThemeManager.appTheme().getThemeResId());
    }

    public static int retrieveAccentColor(final Context context, final int themeResId) {
        return retrieveThemeColor(context, themeResId, new int [] {R.attr.colorAccent}, R.color.accent_default);
    }

    /**
     * Computes height of status bar, only if it is presented at top of the screen and it is visible.
     *
     * @param context Any application {@link android.content.Context}.
     * @param window {@link android.view.Window} that corresponds to the {@link android.app.Activity}
     *
     * @return {@code 0} - if the status bar isn't presented
     * at top of the screen or isn't visible, otherwise height in pixels.
     */
    public static int computeTopStatusBarHeight(final Context context, final Window window) {
        // Privately get a resource Id for status bar's height from the android resources
        final int resourceId = retrieveSystemDimen(context.getResources(), "status_bar_height");

        // Retrieve the overall visible display size in which the window
        // this view is attached to has been positioned in
        final Rect displayFrame = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displayFrame);

        // If the resource is exists and the status bar is presented then grab from the resources
        if (displayFrame.top > 0 && resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }

        // Otherwise hope for value ​​of the window
        return displayFrame.top;
    }

    public static int computeNavigationBarHeight(final Context context) {
        int resourceId = retrieveSystemDimen(context.getResources(), "navigation_bar_height");
        return (resourceId > 0) ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    public static int retrieveSystemDimen(final Resources resources, final String identifier) {
        return resources.getIdentifier(identifier, "dimen", "android");
    }

}
