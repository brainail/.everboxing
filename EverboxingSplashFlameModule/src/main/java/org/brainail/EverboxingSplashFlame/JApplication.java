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
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingHardyDialogs.HardyDialogsContext;
import org.brainail.EverboxingSplashFlame.bus.BusEventsLogger;
import org.brainail.EverboxingSplashFlame.di.HasComponent;
import org.brainail.EverboxingSplashFlame.di.component.AppComponent;
import org.brainail.EverboxingSplashFlame.di.component.DaggerAppComponent;
import org.brainail.EverboxingSplashFlame.di.module.AndroidModule;
import org.brainail.EverboxingSplashFlame.ui.activities.common.HomeActivity;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;
import org.brainail.EverboxingSplashFlame.utils.manager.ThemeManager;
import org.brainail.EverboxingTools.ToolsContext;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.brainail.EverboxingTools.utils.detector.StrictModeInitializer;
import org.brainail.EverboxingTools.utils.tool.ToolFonts;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

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
public class JApplication extends Application implements HasComponent<AppComponent> {

    private AppComponent mAppComponent;

    @Override
    public void onCreate () {
        super.onCreate ();

        initLoggers ();
        initPrefs ();
        initDialogs ();
        initTools ();
        initAnalyzers ();
        initFonts ();
        initThemes ();
        initAppComponent ();
    }

    private void initAppComponent () {
        mAppComponent = DaggerAppComponent.builder ()
                .androidModule (new AndroidModule (this))
                .build ();
        mAppComponent.inject (this);
    }

    // Method injections are called after AppComponent.inject(...App)
    // call in order methods are defined.
    // Move dependencies that can be used by other dependencies to top.
    // ...
    @Inject
    void registerLifecycleCallbacks (ComponentLifecycleCallbacks callbacks) {
        PooLogger.info ("registerLifecycleCallbacks");
        registerActivityLifecycleCallbacks (callbacks);
        registerComponentCallbacks (callbacks);
        // Dagger keeps the injected object instance via ScopedProvider in DaggerAppComponent
    }

    @Inject
    void registerStrictMode (final StrictModeInitializer strictModeInitializer) {
        PooLogger.info ("registerStrictMode");
        // Dagger keeps the injected object instance via ScopedProvider in DaggerAppComponent
    }

    @Inject
    void registerRefWatcher (final RefWatcher refWatcher) {
        PooLogger.info ("registerRefWatcher");
        // Dagger keeps the injected object instance via ScopedProvider in DaggerAppComponent
    }

    @Inject
    void registerEventBusLogger (final BusEventsLogger busEventsLogger) {
        PooLogger.info ("registerEventBusLogger");
        // Dagger keeps the injected object instance via ScopedProvider in DaggerAppComponent
        busEventsLogger.init ();
    }

    @Inject
    void registerConnectionManager (final ConnectionManager connectionManager) {
        PooLogger.info ("registerConnectionManager");
        // Dagger keeps the injected object instance via ScopedProvider in DaggerAppComponent
    }

    @Override
    public AppComponent getComponent () {
        return mAppComponent;
    }

    private void initPrefs () {
        // Provide context for prefs
        SettingsManager.init (getApplicationContext ());
    }

    private void initLoggers () {
        PooLogger.init (BuildConfig.LOGGABLE, BuildConfig.MODULE_NAME);

        // Logging settings of other libraries
        // Icepick.setDebug(BuildConfig.DEBUG);
        // ButterKnife.setDebug (BuildConfig.DEBUG);
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
        // Initialize crashlytics via Fabric
        if (BuildConfig.USE_CRASHLYTICS) {
            initFabric ();
        }
    }

    private void initThemes () {
        // Initialization from preferences
        ThemeManager.init ();
    }

    private void initFabric () {
        Fabric.with (this, new Crashlytics (), new CrashlyticsNdk ());
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
        PooLogger.warn ("onLowMemory");
    }

    @Override
    @TargetApi (Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onTrimMemory (final int level) {
        super.onTrimMemory (level);
        PooLogger.warn ("onTrimMemory: ?", level);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged (newConfig);
        PooLogger.verb ("onConfigurationChanged: ?", newConfig);
    }

    /**
     * Right way to get application context.
     */
    public static JApplication get (final @NonNull Context context) {
        return (JApplication) context.getApplicationContext ();
    }

    public void restartApp () {
        PooLogger.verb ("restartApp");

        final long APP_RESTART_DELAY_MILLIS = TimeUnit.SECONDS.toMillis (3);
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
