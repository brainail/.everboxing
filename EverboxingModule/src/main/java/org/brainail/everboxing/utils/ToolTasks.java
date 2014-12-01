package org.brainail.Everboxing.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import org.brainail.Everboxing.auth.AbstractAuthTask;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * &copy; 2014 brainail <br/><br/>
 *
 * This program is free software: you can redistribute it and/or modify <br/>
 * it under the terms of the GNU General Public License as published by <br/>
 * the Free Software Foundation, either version 3 of the License, or <br/>
 * (at your option) any later version. <br/><br/>
 *
 * This program is distributed in the hope that it will be useful, <br/>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of <br/>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the <br/>
 * GNU General Public License for more details. <br/>
 *
 * You should have received a copy of the GNU General Public License <br/>
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
