package org.brainail.Everboxing.utils;

import android.util.Log;

import org.brainail.Everboxing.BuildConfig;

import java.util.Locale;

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
public final class Plogger {

    public enum LogScope {

        AUTH("Authorization flow"),
        AUTH_TOKEN("Auth token");

        final String mScopeName;

        private LogScope(final String scopeName) {
            mScopeName = scopeName;
        }

        public String scope() {
            return mScopeName;
        }

        public String scopeMsg(final String message) {
            return "[" + mScopeName + "] " + message;
        }

    }

    public static final boolean LOGGABLE = BuildConfig.LOGGABLE;
    public static final String TAG = String.format(Locale.US, "[Plogger]:[%s]", BuildConfig.MODULE_NAME);

    public static void logV(final String message) {
        if (LOGGABLE) {
            Log.v(TAG, message);
        }
    }

    public static void logV(final String message, Object ... params) {
        if (LOGGABLE) {
            Log.v(TAG, String.format(Locale.US, message, params));
        }
    }

    public static void logD(final String message) {
        if (LOGGABLE) {
            Log.d(TAG, message);
        }
    }

    public static void logD(final String message, Object ... params) {
        if (LOGGABLE) {
            Log.d(TAG, String.format(Locale.US, message, params));
        }
    }

    public static void logI(final String message) {
        if (LOGGABLE) {
            Log.i(TAG, message);
        }
    }

    public static void logI(final String message, Object ... params) {
        if (LOGGABLE) {
            Log.i(TAG, String.format(Locale.US, message, params));
        }
    }

    public static void logW(final String message) {
        if (LOGGABLE) {
            Log.w(TAG, message);
        }
    }

    public static void logW(final String message, Object ... params) {
        if (LOGGABLE) {
            Log.w(TAG, String.format(Locale.US, message, params));
        }
    }

    public static void logE(final String message) {
        if (LOGGABLE) {
            Log.e(TAG, message);
        }
    }

    public static void logE(final String message, Object ... params) {
        if (LOGGABLE) {
            Log.e(TAG, String.format(Locale.US, message, params));
        }
    }

    public static void logV(final LogScope scope, final String message) {
        if (LOGGABLE) {
            Log.v(TAG, scope.scopeMsg(message));
        }
    }

    public static void logV(final LogScope scope, final String message, Object ... params) {
        if (LOGGABLE) {
            Log.v(TAG, scope.scopeMsg(String.format(Locale.US, message, params)));
        }
    }

    public static void logD(final LogScope scope, final String message) {
        if (LOGGABLE) {
            Log.d(TAG, scope.scopeMsg(message));
        }
    }

    public static void logD(final LogScope scope, final String message, Object ... params) {
        if (LOGGABLE) {
            Log.d(TAG, scope.scopeMsg(String.format(Locale.US, message, params)));
        }
    }

    public static void logI(final LogScope scope, final String message) {
        if (LOGGABLE) {
            Log.i(TAG, scope.scopeMsg(message));
        }
    }

    public static void logI(final LogScope scope, final String message, Object ... params) {
        if (LOGGABLE) {
            Log.i(TAG, scope.scopeMsg(String.format(Locale.US, message, params)));
        }
    }

    public static void logW(final LogScope scope, final String message) {
        if (LOGGABLE) {
            Log.w(TAG, scope.scopeMsg(message));
        }
    }

    public static void logW(final LogScope scope, final String message, Object ... params) {
        if (LOGGABLE) {
            Log.w(TAG, scope.scopeMsg(String.format(Locale.US, message, params)));
        }
    }

    public static void logE(final LogScope scope, final String message) {
        if (LOGGABLE) {
            Log.e(TAG, scope.scopeMsg(message));
        }
    }

    public static void logE(final LogScope scope, final String message, Object ... params) {
        if (LOGGABLE) {
            Log.e(TAG, scope.scopeMsg(String.format(Locale.US, message, params)));
        }
    }

}

