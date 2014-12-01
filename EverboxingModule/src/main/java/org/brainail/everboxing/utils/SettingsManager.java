package org.brainail.Everboxing.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;
import org.brainail.Everboxing.auth.AuthUserInfo;

import static org.brainail.Everboxing.utils.ThemeManager.AppTheme;

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

    public void saveAppTheme(final AppTheme theme) {
        final String themeName = JApplication.appContext().getString(theme.getNameResId());
        mDefaultPreferences.edit()
                .putString(mAppThemeNamePfKey, themeName)
                .putString(mAppThemePfKey, theme.name()).apply();
    }

    public AppTheme retrieveAppTheme() {
        final String sTheme = mDefaultPreferences.getString(mAppThemePfKey, AppTheme.PINK.name());
        return AppTheme.valueOf(sTheme);
    }

}
