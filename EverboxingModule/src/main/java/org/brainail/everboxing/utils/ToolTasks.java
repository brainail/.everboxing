package org.brainail.Everboxing.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import org.brainail.Everboxing.auth.AbstractAuthTask;

/**
 * User: brainail<br/>
 * Date: 11.10.14<br/>
 * Time: 15:40<br/>
 */
public final class ToolTasks {

    // Safely executes async task.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void safeExecuteAuthTask(final AbstractAuthTask authTask) {
        try {
            if (!Sdk.isSdkSupported(Sdk.HONEYCOMB)) {
                authTask.execute();
            } else {
                authTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception exception) {
            Plogger.logE("Smth was wrong while executing ad task.\nException: %s", exception.getLocalizedMessage());
        }
    }


}
