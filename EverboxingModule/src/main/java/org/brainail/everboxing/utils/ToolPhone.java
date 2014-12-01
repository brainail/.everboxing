package org.brainail.Everboxing.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public final class ToolPhone {

    public static boolean isXLTablet(final Context context) {
        final Configuration configuration = context.getResources().getConfiguration();
        return (configuration.screenLayout & SCREENLAYOUT_SIZE_MASK) >= SCREENLAYOUT_SIZE_XLARGE;
    }

    public static boolean hasNetworkConnection(final Context context) {
        try {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return null != networkInfo && networkInfo.isConnected();
        } catch (Exception exception) {
            return true;
        }
    }

}
