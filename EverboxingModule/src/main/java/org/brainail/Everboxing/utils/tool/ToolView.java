package org.brainail.Everboxing.utils.tool;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.LinearLayout;

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
public final class ToolView {

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

}
