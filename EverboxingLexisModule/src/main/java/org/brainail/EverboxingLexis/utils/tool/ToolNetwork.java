package org.brainail.EverboxingLexis.utils.tool;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
public final class ToolNetwork {

    public static boolean hasNetworkConnection(final Context context) {
        try {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return null != networkInfo && networkInfo.isConnected();
        } catch (Exception exception) {
            return true;
        }
    }

    public static boolean isNetworkAvailable(final Context context) {
        int accessEnabled = context.checkCallingPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        if (PackageManager.PERMISSION_DENIED == accessEnabled) return true;

        final ConnectivityManager coManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo wifiNetwork = coManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiNetwork && wifiNetwork.isConnected()) {
            return true;
        }

        final NetworkInfo mobileNetwork = coManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != mobileNetwork && mobileNetwork.isConnected()) {
            return true;
        }

        final NetworkInfo activeNetwork = coManager.getActiveNetworkInfo();
        if (null != activeNetwork  && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

}
