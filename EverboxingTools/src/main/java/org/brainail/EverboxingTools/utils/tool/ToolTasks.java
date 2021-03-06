package org.brainail.EverboxingTools.utils.tool;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.brainail.EverboxingTools.BuildConfig;
import org.brainail.EverboxingTools.utils.Sdk;

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
public final class ToolTasks {

    private static final String LOG_TAG = ToolTasks.class.getSimpleName ();

    // Safely executes async task.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings ("unchecked")
    public static void safeExecuteTask(final AsyncTask task) {
        try {
            if (!Sdk.isSdkSupported(Sdk.HONEYCOMB)) {
                task.execute();
            } else {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception exception) {
            if (BuildConfig.LOGGABLE) {
                Log.e(LOG_TAG, "Smth was wrong while executing a task.", exception);
            }
        }
    }

}
