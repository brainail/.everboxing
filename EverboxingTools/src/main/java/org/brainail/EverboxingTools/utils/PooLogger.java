package org.brainail.EverboxingTools.utils;

import android.util.Log;

import org.brainail.EverboxingTools.utils.tool.ToolStrings;

import static java.util.Locale.US;

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

    private static final String TAG = "[PooLogger]";

    private static volatile boolean sIsInitialized = false;
    private static volatile boolean sIsLoggable = false;
    private static volatile String sModule = null;

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

    private static String moduleMsg (final String message) {
        return "[" + sModule + "] ⭆⭆⭆ " + message;
    }

    private static String specificLineMsg (final String message) {
        return "(" + TraceHelper.previousCallLine (-2) + ") ⭆⭆⭆ \n ⭆⭆⭆ " + message;
    }

    public static void init (final boolean isLoggable, final String moduleName) {
        if (! sIsInitialized) {
            sIsInitialized = true;
            sIsLoggable = isLoggable;
            sModule = moduleName;
        }
    }

    public static void verb (final String message) {
        if (sIsLoggable) {
            Log.v (TAG, moduleMsg (specificLineMsg (message)));
        }
    }

    public static void verb (final String message, final Object... params) {
        if (sIsLoggable) {
            Log.v (TAG, moduleMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params))));
        }
    }

    public static void debug (final String message) {
        if (sIsLoggable) {
            Log.d (TAG, moduleMsg (specificLineMsg (message)));
        }
    }

    public static void debug (final String message, final Object... params) {
        if (sIsLoggable) {
            Log.d (TAG, moduleMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params))));
        }
    }

    public static void info (final String message) {
        if (sIsLoggable) {
            Log.i (TAG, moduleMsg (specificLineMsg (message)));
        }
    }

    public static void info (final String message, final Object... params) {
        if (sIsLoggable) {
            Log.i (TAG, moduleMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params))));
        }
    }

    public static void warn (final String message) {
        if (sIsLoggable) {
            Log.w (TAG, moduleMsg (specificLineMsg (message)));
        }
    }

    public static void warn (final Throwable error, final String message) {
        if (sIsLoggable) {
            Log.w (TAG, moduleMsg (specificLineMsg (message)), error);
        }
    }

    public static void warn (final String message, final Object... params) {
        if (sIsLoggable) {
            Log.w (TAG, moduleMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params))));
        }
    }

    public static void error (final String message) {
        if (sIsLoggable) {
            Log.e (TAG, moduleMsg (specificLineMsg (message)));
        }
    }

    public static void error (final Throwable error, final String message) {
        if (sIsLoggable) {
            Log.e (TAG, moduleMsg (specificLineMsg (message)), error);
        }
    }

    public static void error (final String message, final Object... params) {
        if (sIsLoggable) {
            Log.e (TAG, moduleMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params))));
        }
    }

    public static void verb (final PooLogScope scope, final String message) {
        if (sIsLoggable) {
            Log.v (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (message))));
        }
    }

    public static void verb (final PooLogScope scope, final String message, final Object... params) {
        if (sIsLoggable) {
            Log.v (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params)))));
        }
    }

    public static void debug (final PooLogScope scope, final String message) {
        if (sIsLoggable) {
            Log.d (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (message))));
        }
    }

    public static void debug (final PooLogScope scope, final String message, final Object... params) {
        if (sIsLoggable) {
            Log.d (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params)))));
        }
    }

    public static void info (final PooLogScope scope, final String message) {
        if (sIsLoggable) {
            Log.i (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (message))));
        }
    }

    public static void info (final PooLogScope scope, final String message, final Object... params) {
        if (sIsLoggable) {
            Log.i (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params)))));
        }
    }

    public static void warn (final PooLogScope scope, final String message) {
        if (sIsLoggable) {
            Log.w (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (message))));
        }
    }

    public static void warn (final PooLogScope scope, final String message, final Object... params) {
        if (sIsLoggable) {
            Log.w (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params)))));
        }
    }

    public static void error (final PooLogScope scope, final String message) {
        if (sIsLoggable) {
            Log.e (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (message))));
        }
    }

    public static void error (final PooLogScope scope, final String message, final Object... params) {
        if (sIsLoggable) {
            Log.e (TAG, moduleMsg (scope.scopeMsg (specificLineMsg (ToolStrings.formatArgs (US, message, params)))));
        }
    }

}

