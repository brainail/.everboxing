package org.brainail.EverboxingLexis.utils.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brainail.EverboxingLexis.JApplication;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.oauth.api.UserInfoApi;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import itkach.aard2.utils.RemoteContentMode;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
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
    private String mPlayAccountPfKey;
    private String mSyncDataPfKey;
    private String mAppThemePfKey;
    private String mAppThemeNamePfKey;
    private String mLoadRemoteContentModePfKey;
    private String mLoadRemoteContentModeNamePfKey;

    private SettingsManager () {
        initializePreferences ();
        initializePreferencesKeys ();
    }

    private void initializePreferencesKeys () {
        mPlayAccountPfKey = ToolResources.string (R.string.settings_add_play_account_key);
        mSyncDataPfKey = ToolResources.string (R.string.settings_sync_account_key);
        mAppThemeNamePfKey = ToolResources.string (R.string.settings_change_theme_key);
        mAppThemePfKey = "settings_app_theme";
        mLoadRemoteContentModeNamePfKey = ToolResources.string (R.string.settings_load_remote_content_key);
        mLoadRemoteContentModePfKey = "settings_load_remote_content";
    }

    private void initializePreferences () {
        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences (JApplication.appContext ());
    }

    private static class LazyHolder {
        private static final SettingsManager INSTANCE = new SettingsManager ();
    }

    public static SettingsManager getInstance () {
        return LazyHolder.INSTANCE;
    }

    public SharedPreferences defaultPreferences () {
        return mDefaultPreferences;
    }

    public void savePlayAccountDetails (final UserInfoApi userInfo) {
        mDefaultPreferences.edit ().putString (mPlayAccountPfKey, userInfo.email).apply ();
    }

    public void removePlayAccountDetails () {
        mDefaultPreferences.edit ().remove (mPlayAccountPfKey).apply ();
    }

    public String retrievePlayAccountEmail () {
        return mDefaultPreferences.getString (mPlayAccountPfKey, null);
    }

    public boolean retrieveSyncDataFlag () {
        return mDefaultPreferences.getBoolean (mSyncDataPfKey, false);
    }

    public void saveAppTheme (final ThemeManager.AppTheme theme) {
        final String themeName = JApplication.appContext ().getString (theme.getNameResId ());

        mDefaultPreferences.edit ()
                .putString (mAppThemeNamePfKey, themeName)
                .putString (mAppThemePfKey, theme.name ())
                .apply ();
    }

    public ThemeManager.AppTheme retrieveAppTheme () {
        final String sTheme = mDefaultPreferences.getString (mAppThemePfKey, ThemeManager.AppTheme.PINK.name ());

        try {
            return ThemeManager.AppTheme.valueOf (sTheme);
        } catch (final Exception exception) {
            return ThemeManager.AppTheme.PINK;
        }
    }

    public String retrieveAppThemeSummary () {
        final String sTheme = mDefaultPreferences.getString (mAppThemePfKey, ThemeManager.AppTheme.PINK.name ());

        try {
            return ToolResources.string (ThemeManager.AppTheme.valueOf (sTheme).getNameResId ());
        } catch (final Exception exception) {
            return ToolResources.string (ThemeManager.AppTheme.PINK.getNameResId ());
        }
    }

    public void saveLoadRemoteContentMode (final RemoteContentMode mode) {
        final String modeName = JApplication.appContext ().getString (mode.getNameResId ());

        mDefaultPreferences.edit ()
                .putString (mLoadRemoteContentModeNamePfKey, modeName)
                .putString (mLoadRemoteContentModePfKey, mode.name ())
                .apply ();
    }

    public RemoteContentMode retrieveLoadRemoteContentMode () {
        final String sMode = mDefaultPreferences.getString (mLoadRemoteContentModePfKey, RemoteContentMode.WIFI.name ());

        try {
            return RemoteContentMode.valueOf (sMode);
        } catch (final Exception exception) {
            return RemoteContentMode.WIFI;
        }
    }

    public String retrieveLoadRemoteContentModeSummary () {
        final String sMode = mDefaultPreferences.getString (mLoadRemoteContentModePfKey, RemoteContentMode.WIFI.name ());

        try {
            return ToolResources.string (RemoteContentMode.valueOf (sMode).getNameResId ());
        } catch (final Exception exception) {
            return ToolResources.string (RemoteContentMode.WIFI.getNameResId ());
        }
    }

}
