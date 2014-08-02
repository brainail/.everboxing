package org.brainail.Everboxing.utils;

import android.app.Activity;
import android.widget.Toast;

import net.simonvt.menudrawer.MenuDrawer;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public final class UIUtils {

    public static boolean isMenuDrawerOpened(final int drawerState) {
        return drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING;
    }

    public static void showToast(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != activity) {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
