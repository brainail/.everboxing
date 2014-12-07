package org.brainail.Everboxing.utils.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.kenny.snackbar.SnackBar;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;

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
public final class ToolUI {

    @SuppressLint("RtlHardcoded")
    public final static int GRAVITY_START = Gravity.START | Gravity.LEFT;

    public static boolean USE_SNACKBARS = true;

    public static boolean toggleMenuDrawer(final DrawerLayout drawerLayout, final boolean twoDirections) {
        if (drawerLayout.isDrawerOpen(GRAVITY_START)) {
            drawerLayout.closeDrawer(GRAVITY_START);
            return true;
        } else if (twoDirections) {
            drawerLayout.openDrawer(GRAVITY_START);
            return true;
        }

        return false;
    }

    public static void showToast(final Activity activity, final String message) {
        if (null != activity) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!USE_SNACKBARS) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    } else {
                        SnackBar.show(activity, message);
                    }
                }
            });
        }
    }

    public static void showToast(final Activity activity, final int resId) {
        showToast(activity, JApplication.appContext().getString(resId));
    }

    public static void fixSettingsTopPaddingWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            final ViewGroup parent = (ViewGroup) allPreferences.getParent();
            parent.setPadding(parent.getPaddingLeft(), 0, parent.getPaddingRight(), 0);
        } catch (Exception exception) {
            // Do nothing
        }
    }

    public static void fixSettingsSelectorWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            allPreferences.setSelector(R.drawable.preference_selector);
        } catch (Exception exception) {
            // Do nothing
        }
    }

    public static void fixSettingsDividersWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            allPreferences.setDivider(null);
            allPreferences.setDividerHeight(0);
        } catch (Exception exception) {
            // Do nothing
        }
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
        final int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

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

}
