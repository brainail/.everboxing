package org.brainail.EverboxingSplashFlame.di.module;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingSplashFlame.BuildConfig;
import org.brainail.EverboxingSplashFlame.ComponentLifecycleCallbacks;
import org.brainail.EverboxingSplashFlame.JApplication;
import org.brainail.EverboxingSplashFlame.di.AppContext;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
@Module
public class AndroidModule {

    private final JApplication mApp;

    public AndroidModule (final JApplication app) {
        mApp = app;
    }

    @Provides
    @AppContext
    Context provideAppContext () {
        return mApp.getApplicationContext ();
    }

    @Provides
    @Singleton
    static ComponentLifecycleCallbacks provideComponentLifecycleCallbacks (EventBus eventBus) {
        return new ComponentLifecycleCallbacks (eventBus);
    }

    @Provides
    @Singleton
    RefWatcher provideRefWatcher () {
        return BuildConfig.USE_LEAKCANARY ? LeakCanary.install (mApp) : RefWatcher.DISABLED;
    }

}
