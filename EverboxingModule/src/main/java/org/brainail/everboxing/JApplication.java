package org.brainail.Everboxing;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class JApplication extends Application {

    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // Store for long use
        mAppContext = getApplicationContext();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onTrimMemory(final int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static Context appContext() {
        return mAppContext;
    }

}
