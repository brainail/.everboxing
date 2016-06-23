package org.brainail.EverboxingSplashFlame.di.module;

import android.content.Context;

import org.brainail.EverboxingSplashFlame.ConnectionManager;
import org.brainail.EverboxingSplashFlame.bus.BusEventsLogger;
import org.brainail.EverboxingSplashFlame.config.DeviceConfig;
import org.brainail.EverboxingSplashFlame.di.AppContext;
import org.brainail.EverboxingSplashFlame.navigate.navigator.Navigator;
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
public class AppModule {

    @Provides
    @Singleton
    static EventBus provideGlobalBus () {
        return EventBus.getDefault ();
    }

    @Provides
    @Singleton
    static BusEventsLogger provideEventsLogger (final EventBus globalBus) {
        return new BusEventsLogger (globalBus);
    }

    @Provides
    @Singleton
    @AppContext
    static Navigator provideNavigator (@AppContext Context context) {
        return new Navigator (context);
    }

    @Provides
    @Singleton
    static DeviceConfig provideDeviceConfig (@AppContext Context context) {
        return new DeviceConfig (context);
    }

    @Provides
    @Singleton
    static ConnectionManager provideConnectionManager (@AppContext Context context, EventBus globalBus) {
        return new ConnectionManager (context, globalBus);
    }

}
