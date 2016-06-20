package org.brainail.EverboxingTools.utils.tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.WindowManager;

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
public final class ToolOrientation {
    public static void lockScreenOrientation (final @Nullable Activity scene) {
        if (null == scene) {
            // Nothing to lock
            return;
        }

        final WindowManager windowManager = (WindowManager) scene.getSystemService (Context.WINDOW_SERVICE);
        final Configuration actualConfiguration = scene.getResources ().getConfiguration ();
        final int naturalRotation = windowManager.getDefaultDisplay ().getRotation ();

        if ((actualConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE
                && (naturalRotation == Surface.ROTATION_0 || naturalRotation == Surface.ROTATION_180))
                || (actualConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                        (naturalRotation == Surface.ROTATION_90 || naturalRotation == Surface.ROTATION_270))) {

            switch (naturalRotation) {
                case Surface.ROTATION_0:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        } else {
            switch (naturalRotation) {
                case Surface.ROTATION_0:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

    public static void unlockScreenOrientation (final @Nullable Activity scene) {
        if (null == scene) {
            // Nothing to unlock
            return;
        }

        scene.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
