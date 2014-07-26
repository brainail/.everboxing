package org.brainail.Everboxing.utils;

import android.content.Context;
import android.content.res.Configuration;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

/**
 * User: brainail<br/>
 * Date: 13.07.14<br/>
 * Time: 14:45<br/>
 */
public final class DeviceUtils {

    public static boolean isXLTablet(final Context context) {
        final Configuration configuration = context.getResources().getConfiguration();
        return (configuration.screenLayout & SCREENLAYOUT_SIZE_MASK) >= SCREENLAYOUT_SIZE_XLARGE;
    }

}
