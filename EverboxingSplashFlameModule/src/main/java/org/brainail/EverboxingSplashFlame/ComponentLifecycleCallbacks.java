package org.brainail.EverboxingSplashFlame;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import org.brainail.EverboxingSplashFlame.bus.GlobalEvents;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
@Singleton
public class ComponentLifecycleCallbacks implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private EventBus mGlobalBus;
    private boolean mIsOnForeground;

    public ComponentLifecycleCallbacks (EventBus eventBus) {
        mGlobalBus = eventBus;
    }

    @Override
    public void onActivityCreated (Activity activity, Bundle savedInstanceState) {
        PooLogger.logI ("onActivityCreated: activity=?, savedInstanceState=?", activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted (Activity activity) {
        PooLogger.logI ("onActivityStarted: activity=?", activity);
    }

    @Override
    public void onActivityResumed (Activity activity) {
        PooLogger.logI ("onActivityResumed: activity=?", activity);
        setIsOnForeground (true);
    }

    @Override
    public void onActivityPaused (Activity activity) {
        PooLogger.logI ("onActivityPaused: activity=?", activity);
    }

    @Override
    public void onActivityStopped (Activity activity) {
        PooLogger.logI ("onActivityStopped: activity=?", activity);
    }

    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
        PooLogger.logI ("onActivitySaveInstanceState: activity=?, outState=?", activity, outState);
    }

    @Override
    public void onActivityDestroyed (Activity activity) {
        PooLogger.logI ("onActivityDestroyed: activity=?", activity);
    }

    @Override
    public void onTrimMemory (int level) {
        PooLogger.logI ("onTrimMemory: level=?", level);

        // http://stackoverflow.com/a/19920353/317928
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            setIsOnForeground (false);
        }
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        PooLogger.logI ("onConfigurationChanged: newConfig=?", newConfig);
    }

    @Override
    public void onLowMemory () {
        PooLogger.logI ("onLowMemory");
    }

    private void setIsOnForeground (boolean isOnForeground) {
        if (mIsOnForeground != isOnForeground) {
            mIsOnForeground = isOnForeground;

            // Notify
            mGlobalBus.post (new GlobalEvents.UiStateChanged (mIsOnForeground));

            PooLogger.logV ("setIsOnForeground: ?", mIsOnForeground);
        }
    }

}
