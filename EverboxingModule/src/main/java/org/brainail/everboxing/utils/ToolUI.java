package org.brainail.Everboxing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class ToolUI {

    @SuppressLint("RtlHardcoded")
    public final static int GRAVITY_START = Gravity.START | Gravity.LEFT;

    public static boolean toggleMenuDrawer(final DrawerLayout drawerLayout, final boolean twoDirections) {
        if (drawerLayout.isDrawerOpen(GRAVITY_START)) {
            drawerLayout.closeDrawer(GRAVITY_START);
            return true;
        } else if (twoDirections) {
            drawerLayout.openDrawer(GRAVITY_START);
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

    public static void fixSettingsTopPaddingWorkaround(final Activity activity) {
        try {
            final ListView allPreferences = (ListView) activity.findViewById(android.R.id.list);
            allPreferences.setDivider(null);
            allPreferences.setDividerHeight(0);
            final ViewGroup parent = (ViewGroup) allPreferences.getParent();
            parent.setPadding(parent.getPaddingLeft(), 0, parent.getPaddingRight(), parent.getPaddingBottom());
        } catch (Exception exception) {
            // Do nothing
        }
    }


}
