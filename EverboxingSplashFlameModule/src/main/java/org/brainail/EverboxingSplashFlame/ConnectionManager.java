package org.brainail.EverboxingSplashFlame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.brainail.EverboxingSplashFlame.bus.GlobalEvents;
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
public class ConnectionManager {
    
    private final ConnectivityManager mConnectivityManager;
    private final EventBus mGlobalBus;
    private boolean mIsConnected;

    public ConnectionManager (final Context context, final EventBus globalBus) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        mGlobalBus = globalBus;
        mIsConnected = isConnectedInternal ();
        context.registerReceiver (
                mConnectivityChangeReceiver,
                new IntentFilter (android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private final BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context, Intent intent) {
            updateConnectivityState ();
        }
    };

    private boolean isConnectedInternal () {
        final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo ();
        return (networkInfo != null && networkInfo.isConnected ());
    }

    private void updateConnectivityState () {
        final boolean isConnected = isConnectedInternal ();
        if (mIsConnected != isConnected) {
            mIsConnected = isConnected;
            mGlobalBus.post (new GlobalEvents.ConnectivityChanged (mIsConnected));
        }
    }

    public boolean isConnected () {
        return mIsConnected;
    }

}
