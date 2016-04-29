package org.brainail.EverboxingTools.utils;

import android.util.Log;

import org.brainail.EverboxingTools.BuildConfig;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author emalyshev
 */
public final class Stopwatch implements Serializable {

    private long mStartTime;
    private long mElapsedTime;
    private boolean mIsRunning;

    public static Stopwatch start() {
        return new Stopwatch();
    }

    private Stopwatch() {
        reset();
    }

    public void reset() {
        mStartTime = System.nanoTime();
        mElapsedTime = 0;
        mIsRunning = true;
    }

    public void pause() {
        if (mIsRunning) {
            mElapsedTime += System.nanoTime() - mStartTime;
            mIsRunning = false;
        }
    }

    public void resume() {
        if (! mIsRunning) {
            mStartTime = System.nanoTime();
            mIsRunning = true;
        }
    }

    public long elapsed() {
        if (mIsRunning) {
            return mElapsedTime + System.nanoTime() - mStartTime;
        }

        return mElapsedTime;
    }

    public long elapsedMillis() {
        return elapsed(TimeUnit.MILLISECONDS);
    }

    public long elapsed(final TimeUnit timeUnit) {
        return timeUnit.convert(elapsed(), TimeUnit.NANOSECONDS);
    }

    public void logElapsedMillis(final String logTag) {
        if (BuildConfig.LOGGABLE) {
            Log.v(logTag, TraceHelper.previousMethod() + " -> elapsed time millis: " + elapsedMillis());
        }
    }

}
