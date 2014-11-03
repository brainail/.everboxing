package org.brainail.Everboxing.utils;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.Toast;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public final class ToolUI {

    public static boolean toggleMenuDrawer(final DrawerLayout drawerLayout, final boolean twoDirections) {
        if (drawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.START | Gravity.LEFT);
            return true;
        } else if (twoDirections) {
            drawerLayout.openDrawer(Gravity.START | Gravity.LEFT);
            return true;
        }

        return false;
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
