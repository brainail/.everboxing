package org.brainail.Everboxing;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

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
