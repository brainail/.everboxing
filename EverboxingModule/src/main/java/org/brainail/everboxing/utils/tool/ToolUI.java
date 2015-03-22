package org.brainail.Everboxing.utils.tool;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.notice.NoticeController;
import org.brainail.Everboxing.utils.Sdk;

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
                        NoticeController.from(activity).notifyScene(message);
                    }
                }
            });
        }
    }

    public static void showToast(final Activity activity, final int resId) {
        showToast(activity, JApplication.appContext().getString(resId));
    }

    public static void fixSettingsPaddingWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            allPreferences.setPadding(0, 0, 0, 0);
            final ViewGroup parent = (ViewGroup) allPreferences.getParent();
            parent.setPadding(parent.getPaddingLeft(), 0, parent.getPaddingRight(), 0);
        } catch (Exception exception) {
            // Do nothing
        }
    }

    public static void fixSettingsSelectorWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            if (!Sdk.isSdkSupported(Sdk.LOLLIPOP)) allPreferences.setSelector(R.drawable.list_item_default_selector);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    // @see https://developer.android.com/training/system-ui/navigation.html#behind
    public static boolean hasHideNavigationFlag(final ViewGroup root) {
        if (Sdk.isSdkSupported(Sdk.JELLY_BEAN) && null != root) {
            final int visibilityFlags = root.getWindowSystemUiVisibility();
            final int hideNavigationFlag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            return hideNavigationFlag == (visibilityFlags & hideNavigationFlag);
        }

        return false;
    }

    public static void removeViewFully(final View view) {
        if (null != view) {
            try {
                if (view.getParent() instanceof ViewGroup) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            } catch (Exception exception) {
                // Do nothing
            }

            try {
                if (view instanceof ViewGroup) {
                    ((ViewGroup) view).removeAllViews();
                }
            } catch (Exception exception) {
                // Do nothing
            }
        }
    }

    // Computes the coordinates of view on the screen. You should use it only after the view has been located.
    public static Rect computeLocationOnScreen(final View view) {
        if (null != view) {
            final int [] locationOnScreen = new int [2];
            view.getLocationOnScreen(locationOnScreen);
            int leftPosition = locationOnScreen [0], topPosition = locationOnScreen [1];
            return new Rect(leftPosition, topPosition, leftPosition + view.getWidth(), topPosition + view.getHeight());
        }

        return null;
    }

    public static boolean isLocatedWithinScreen(final Context context, final View view) {
        if (null != view) {
            final Rect location = computeLocationOnScreen(view);

            final int screenHeight = ToolPhone.screenHeightInPixels(context);
            final boolean isOutsideVertically = location.bottom <= 0 || location.top >= screenHeight;

            final int screenWidth = ToolPhone.screenWidthInPixels(context);
            final boolean isOutsideHorizontally = location.right <= 0 || location.left >= screenWidth;

            return !isOutsideVertically && !isOutsideHorizontally;
        }

        return false;
    }

    public static void dismissDialog(final Dialog dialog) {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // Converts view to bitmap.
    public static Bitmap takeViewScreenshot(final WebView view) {
        try {
            if (null != view) {
                view.setDrawingCacheEnabled(true);
                final Bitmap screenshot = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);
                return screenshot;
            }
        } catch (Exception exception) {
            // Do nothing
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void addOnGlobalLayoutListenerOnce(final View view, final ViewTreeObserver.OnGlobalLayoutListener callback) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                callback.onGlobalLayout();

                if (Sdk.isSdkSupported(Sdk.JELLY_BEAN)) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }

        });
    }

    public static View linearWrapper(final Context context, final int layoutId) {
        final LinearLayout layoutWrapper = new LinearLayout(context);
        LayoutInflater.from(context).inflate(layoutId, layoutWrapper, true);
        return layoutWrapper;
    }

    public static boolean isVisible(final View view) {
        return null != view && View.VISIBLE == view.getVisibility();
    }
    
}
