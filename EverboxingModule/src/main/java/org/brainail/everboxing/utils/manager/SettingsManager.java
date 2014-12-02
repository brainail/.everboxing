package org.brainail.Everboxing.utils.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;
import org.brainail.Everboxing.auth.AuthUserInfo;

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
public final class SettingsManager {

    private SharedPreferences mDefaultPreferences;
    private String mAccountPfKey;
    private String mAppThemePfKey;
    private String mAppThemeNamePfKey;

    private SettingsManager() {
        initializePreferences();
        initializePreferencesKeys();
    }

    private void initializePreferencesKeys() {
        mAccountPfKey = JApplication.appContext().getString(R.string.settings_add_account_key);
        mAppThemePfKey = "app_theme";
        mAppThemeNamePfKey = JApplication.appContext().getString(R.string.settings_change_theme_key);
    }

    private void initializePreferences() {
        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(JApplication.appContext());
    }

    private static class LazyHolder {
        private static final SettingsManager INSTANCE = new SettingsManager();
    }

    public static SettingsManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public SharedPreferences defaultPreferences() {
        return mDefaultPreferences;
    }

    public void saveAccountDetails(final AuthUserInfo userInfo) {
        mDefaultPreferences.edit().putString(mAccountPfKey, userInfo.email).apply();
    }

    public void removeAccountDetails() {
        mDefaultPreferences.edit().remove(mAccountPfKey).apply();
    }

    public String retrieveAccountEmail() {
        return mDefaultPreferences.getString(mAccountPfKey, null);
    }

    public void saveAppTheme(final ThemeManager.AppTheme theme) {
        final String themeName = JApplication.appContext().getString(theme.getNameResId());
        mDefaultPreferences.edit()
                .putString(mAppThemeNamePfKey, themeName)
                .putString(mAppThemePfKey, theme.name()).apply();
    }

    public ThemeManager.AppTheme retrieveAppTheme() {
        final String sTheme = mDefaultPreferences.getString(mAppThemePfKey, ThemeManager.AppTheme.PINK.name());
        return ThemeManager.AppTheme.valueOf(sTheme);
    }

}
