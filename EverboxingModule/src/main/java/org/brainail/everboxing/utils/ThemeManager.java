package org.brainail.Everboxing.utils;

import android.app.Activity;
import android.content.Context;

import org.brainail.Everboxing.R;

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
public class ThemeManager {

    private static volatile AppTheme APP_THEME = AppTheme.PINK;

    public enum AppTheme {

        PINK(R.style.AppTheme_Default, R.string.theme_name_default),
        INDIGO(R.style.AppTheme_Default_Indigo, R.string.theme_name_indigo),
        BLUE(R.style.AppTheme_Default_Blue, R.string.theme_name_blue),
        CYAN(R.style.AppTheme_Default_Cyan, R.string.theme_name_cyan),
        TEAL(R.style.AppTheme_Default_Teal, R.string.theme_name_teal),
        GREEN(R.style.AppTheme_Default_Green, R.string.theme_name_green),
        LGREEN(R.style.AppTheme_Default_LightGreen, R.string.theme_name_lgreen);

        private final int mThemeResId;
        private final int mNameResId;

        private AppTheme(final int themeResId, final int nameResId) {
            mThemeResId = themeResId;
            mNameResId = nameResId;
        }

        public int getThemeResId() {
            return mThemeResId;
        }

        public int getNameResId() {
            return mNameResId;
        }

        public static String [] themes(final Context context) {
            final String [] themes = new String [values().length];

            for (int themeIndex = 0; themeIndex < values().length; ++ themeIndex) {
                themes [themeIndex] = context.getString(values() [themeIndex].getNameResId());
            }

            return themes;
        }

    }

    public static void init() {
        APP_THEME = SettingsManager.getInstance().retrieveAppTheme();
    }

    public static AppTheme updateThemeOnCreate(final Activity activity, final AppTheme currentTheme) {
        if (APP_THEME != currentTheme && null != activity) {
            activity.setTheme(APP_THEME.getThemeResId());

            if (null != currentTheme) {
                activity.recreate();
            }
        }

        return APP_THEME;
    }

    public static void changeTheme(final Activity activity, final AppTheme theme) {
        if (APP_THEME != theme) {
            SettingsManager.getInstance().saveAppTheme(theme);

            APP_THEME = theme;
            if (null != activity) {
                activity.recreate();
            }
        }
    }

}
