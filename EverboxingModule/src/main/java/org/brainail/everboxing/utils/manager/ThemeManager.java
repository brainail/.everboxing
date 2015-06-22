package org.brainail.Everboxing.utils.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.brainail.Everboxing.R;

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
public class ThemeManager {

    private static AppTheme APP_THEME = AppTheme.PINK;

    public enum AppTheme {

        PINK(R.style.AppTheme_Default, R.string.theme_name_default),
        INDIGO(R.style.AppTheme_Default_Indigo, R.string.theme_name_indigo),
        BLUE(R.style.AppTheme_Default_Blue, R.string.theme_name_blue),
        CYAN(R.style.AppTheme_Default_Cyan, R.string.theme_name_cyan),
        TEAL(R.style.AppTheme_Default_Teal, R.string.theme_name_teal),
        GREEN(R.style.AppTheme_Default_Green, R.string.theme_name_green),
        LGREEN(R.style.AppTheme_Default_LightGreen, R.string.theme_name_lgreen),
        PURPLE(R.style.AppTheme_Default_Purple, R.string.theme_name_purple),
        BROWN(R.style.AppTheme_Default_Brown, R.string.theme_name_brown),
        ORANGE(R.style.AppTheme_Default_Orange, R.string.theme_name_orange);

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

    public synchronized static void init() {
        APP_THEME = SettingsManager.getInstance().retrieveAppTheme();
    }

    public synchronized static AppTheme checkOnCreate(final AppCompatActivity activity, final AppTheme currentTheme) {
        if (APP_THEME != currentTheme && null != activity) {
            activity.setTheme(APP_THEME.getThemeResId());

            if (null != currentTheme) {
                activity.recreate();
            }
        }

        return APP_THEME;
    }

    public synchronized static void checkOnResume(final AppCompatActivity activity, final AppTheme currentTheme) {
        if (APP_THEME != currentTheme && null != activity && null != currentTheme) {
            activity.recreate();
        }
    }

    public synchronized static void checkOnChange(final Activity activity, final AppTheme theme) {
        if (APP_THEME != theme) {
            SettingsManager.getInstance().saveAppTheme(theme);

            APP_THEME = theme;
            if (null != activity) {
                activity.recreate();
            }
        }
    }

    public synchronized static AppTheme appTheme() {
        return APP_THEME;
    }

}
