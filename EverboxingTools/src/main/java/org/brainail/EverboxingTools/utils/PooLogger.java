package org.brainail.EverboxingTools.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class PooLogger {

    private static volatile boolean sIsInitialized = false;
    private static volatile boolean LOGGABLE = false;
    private static volatile String TAG = "[PooLogger]";

    public static class PooLogScope {
        private final String mScopeName;

        public PooLogScope (final String scopeName) {
            mScopeName = scopeName;
        }

        public String scope () {
            return mScopeName;
        }

        public String scopeMsg (final String message) {
            return "[" + mScopeName + "] " + message;
        }
    }

    public static void init(final boolean isLoggable, final String moduleName) {
        if (! sIsInitialized) {
            sIsInitialized = true;
            LOGGABLE = isLoggable;

            if (! TextUtils.isEmpty (moduleName)) {
                TAG = String.format (Locale.US, "[PooLogger]:[%s]", moduleName);
            }
        }
    }
    

    public static void logV (final String message) {
        if (LOGGABLE) {
            Log.v (TAG, message);
        }
    }

    public static void logV (final String message, final Object... params) {
        if (LOGGABLE) {
            Log.v (TAG, String.format (Locale.US, message, params));
        }
    }

    public static void logD (final String message) {
        if (LOGGABLE) {
            Log.d (TAG, message);
        }
    }

    public static void logD (final String message, final Object... params) {
        if (LOGGABLE) {
            Log.d (TAG, String.format (Locale.US, message, params));
        }
    }

    public static void logI (final String message) {
        if (LOGGABLE) {
            Log.i (TAG, message);
        }
    }

    public static void logI (final String message, final Object... params) {
        if (LOGGABLE) {
            Log.i (TAG, String.format (Locale.US, message, params));
        }
    }

    public static void logW (final String message) {
        if (LOGGABLE) {
            Log.w (TAG, message);
        }
    }

    public static void logW (final Throwable error, final String message) {
        if (LOGGABLE) {
            Log.w (TAG, message, error);
        }
    }

    public static void logW (final String message, final Object... params) {
        if (LOGGABLE) {
            Log.w (TAG, String.format (Locale.US, message, params));
        }
    }

    public static void logE (final String message) {
        if (LOGGABLE) {
            Log.e (TAG, message);
        }
    }

    public static void logE (final Throwable error, final String message) {
        if (LOGGABLE) {
            Log.e (TAG, message, error);
        }
    }

    public static void logE (final String message, final Object... params) {
        if (LOGGABLE) {
            Log.e (TAG, String.format (Locale.US, message, params));
        }
    }

    public static void logV (final PooLogScope scope, final String message) {
        if (LOGGABLE) {
            Log.v (TAG, scope.scopeMsg (message));
        }
    }

    public static void logV (final PooLogScope scope, final String message, final Object... params) {
        if (LOGGABLE) {
            Log.v (TAG, scope.scopeMsg (String.format (Locale.US, message, params)));
        }
    }

    public static void logD (final PooLogScope scope, final String message) {
        if (LOGGABLE) {
            Log.d (TAG, scope.scopeMsg (message));
        }
    }

    public static void logD (final PooLogScope scope, final String message, final Object... params) {
        if (LOGGABLE) {
            Log.d (TAG, scope.scopeMsg (String.format (Locale.US, message, params)));
        }
    }

    public static void logI (final PooLogScope scope, final String message) {
        if (LOGGABLE) {
            Log.i (TAG, scope.scopeMsg (message));
        }
    }

    public static void logI (final PooLogScope scope, final String message, final Object... params) {
        if (LOGGABLE) {
            Log.i (TAG, scope.scopeMsg (String.format (Locale.US, message, params)));
        }
    }

    public static void logW (final PooLogScope scope, final String message) {
        if (LOGGABLE) {
            Log.w (TAG, scope.scopeMsg (message));
        }
    }

    public static void logW (final PooLogScope scope, final String message, final Object... params) {
        if (LOGGABLE) {
            Log.w (TAG, scope.scopeMsg (String.format (Locale.US, message, params)));
        }
    }

    public static void logE (final PooLogScope scope, final String message) {
        if (LOGGABLE) {
            Log.e (TAG, scope.scopeMsg (message));
        }
    }

    public static void logE (final PooLogScope scope, final String message, final Object... params) {
        if (LOGGABLE) {
            Log.e (TAG, scope.scopeMsg (String.format (Locale.US, message, params)));
        }
    }

}

