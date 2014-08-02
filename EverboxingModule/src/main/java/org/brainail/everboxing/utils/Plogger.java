package org.brainail.Everboxing.utils;

import android.util.Log;

import org.brainail.Everboxing.BuildConfig;

import java.util.Locale;

/**
 * User: brainail<br/>
 * Date: 27.07.14<br/>
 * Time: 11:51<br/>
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

