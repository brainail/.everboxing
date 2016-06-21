package org.brainail.EverboxingSplashFlame;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingHardyDialogs.HardyDialogsContext;
import org.brainail.EverboxingSplashFlame.ui.activities.common.HomeActivity;
import org.brainail.EverboxingSplashFlame.utils.manager.ThemeManager;
import org.brainail.EverboxingTools.ToolsContext;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.brainail.EverboxingTools.utils.tool.ToolFonts;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
public class JApplication extends Application {

    private static final long APP_RESTART_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(3);

    @Deprecated
    protected static JApplication mApp;

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate () {
        super.onCreate ();

        // Ridiculous way to store for long use
        mApp = this;

        initLoggers ();
        initDialogs ();
        initTools ();
        initAnalyzers ();
        initFonts ();
        initThemes ();
    }

    private void initLoggers () {
        PooLogger.init (BuildConfig.LOGGABLE, BuildConfig.MODULE_NAME);

        // Logging settings of other libraries
        // Icepick.setDebug(BuildConfig.DEBUG);
        ButterKnife.setDebug (BuildConfig.DEBUG);
    }

    private void initDialogs () {
        // Provide context for dialogs
        HardyDialogsContext.init (getApplicationContext ());
    }

    private void initTools () {
        // Provide context for tools
        ToolsContext.init (getApplicationContext ());
    }

    private void initAnalyzers () {
        // Leaks!
        if (BuildConfig.USE_LEAKCANARY) {
            mRefWatcher = LeakCanary.install (this);
        }

        // Initialize crashlytics via Fabric
        if (BuildConfig.USE_CRASHLYTICS) {
            initFabric ();
        }
    }

    private void initThemes () {
        // Initialization from preferences
        ThemeManager.init ();
    }

    public static RefWatcher refWatcher (final Context context) {
        final JApplication application = (JApplication) context.getApplicationContext ();
        return application.mRefWatcher;
    }

    private void initFabric () {
        Fabric.with (this, new Crashlytics ());
        Crashlytics.setString (BuildConfig.BUILD_TIME_KEY, BuildConfig.BUILD_TIME);
        Crashlytics.setString (BuildConfig.GIT_SHA_KEY, BuildConfig.GIT_SHA);
    }

    private void initFonts () {
        final CalligraphyConfig.Builder fontBuilder = new CalligraphyConfig.Builder ();
        fontBuilder.setDefaultFontPath (ToolFonts.RobotoFonts.ASSETS_LIGHT);
        fontBuilder.setFontAttrId (R.attr.fontPath);

        CalligraphyConfig.initDefault (fontBuilder.build ());
    }

    @Override
    protected void attachBaseContext (final Context baseContext) {
        // Attach the Calligraphy
        super.attachBaseContext (CalligraphyContextWrapper.wrap (baseContext));
    }

    @Override
    public void onLowMemory () {
        super.onLowMemory ();
    }

    @Override
    @TargetApi (Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onTrimMemory (final int level) {
        super.onTrimMemory (level);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged (newConfig);
    }

    @Deprecated
    public static Context appContext () {
        return mApp.getApplicationContext ();
    }

    /**
     * Right way to get application context.
     */
    public static JApplication get (final @NonNull Context context) {
        return (JApplication) context.getApplicationContext ();
    }

    public void restartApp () {
        final AlarmManager alarmManager = (AlarmManager) getSystemService (Context.ALARM_SERVICE);

        final Intent intent = new Intent ();
        intent.setAction (Intent.ACTION_MAIN);
        intent.addCategory (Intent.CATEGORY_LAUNCHER);
        intent.setClass (this, HomeActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity (this, 0, intent, 0);
        final long triggerAtMillis = System.currentTimeMillis () + APP_RESTART_DELAY_MILLIS;
        alarmManager.set (AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

        final int myPId = android.os.Process.myPid ();
        android.os.Process.killProcess (myPId);
    }

}
