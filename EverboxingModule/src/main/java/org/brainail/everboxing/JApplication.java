package org.brainail.Everboxing;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import org.brainail.Everboxing.utils.manager.ThemeManager;
import org.brainail.Everboxing.utils.tool.ToolFonts;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

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

    private static Context mAppContext;

    @Override
    public void onCreate () {
        super.onCreate ();

        // Store for long use
        mAppContext = getApplicationContext ();

        // Initialize crashlytics via Fabric
        if (BuildConfig.USE_CRASHLYTICS) initFabric ();

        // Initialize font
        initFont ();

        // Initialize theme
        ThemeManager.init ();
    }

    private void initFont () {
        final CalligraphyConfig.Builder fontBuilder = new CalligraphyConfig.Builder ();
        fontBuilder.setDefaultFontPath (ToolFonts.RobotoFonts.ASSETS_REGULAR);
        fontBuilder.setFontAttrId (R.attr.fontPath);

        CalligraphyConfig.initDefault (fontBuilder.build ());
    }

    @Override
    protected void attachBaseContext (final Context baseContext) {
        super.attachBaseContext (baseContext);
        // Enable multiDex
        MultiDex.install(this);
    }

    private void initFabric() {
        Fabric.with (this, new Crashlytics ());
        Crashlytics.setString (BuildConfig.BUILD_TIME_KEY, BuildConfig.BUILD_TIME);
        Crashlytics.setString (BuildConfig.GIT_SHA_KEY, BuildConfig.GIT_SHA);
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

    public static Context appContext () {
        return mAppContext;
    }

}
