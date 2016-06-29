package org.brainail.EverboxingSplashFlame.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.api.UserInfoApi;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolResources;

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

    private static volatile SettingsManager INSTANCE;

    private Context mContext;

    private SharedPreferences mDefaultPreferences;

    private String mPlayAccountPfKey;
    private String mSyncDataPfKey;

    private String mAppThemePfKey;
    private String mAppThemeNamePfKey;

    private String mShouldIntroducePfKey;

    private SettingsManager (final Context context) {
        mContext = context.getApplicationContext ();

        initializePreferences ();
        initializePreferencesKeys ();
    }

    private void initializePreferencesKeys () {
        mShouldIntroducePfKey = "settings_app_should_introduce";

        mPlayAccountPfKey = ToolResources.string (mContext, R.string.settings_add_play_account_key);
        mSyncDataPfKey = ToolResources.string (mContext, R.string.settings_sync_account_key);

        mAppThemeNamePfKey = ToolResources.string (mContext, R.string.settings_change_theme_key);
        mAppThemePfKey = "settings_app_theme";
    }

    private void initializePreferences () {
        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
    }

    public static SettingsManager getInstance () {
        return INSTANCE;
    }

    public static SettingsManager init (final Context context) {
        if (null == INSTANCE) {
            synchronized (SettingsManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new SettingsManager (context);
                }
            }
        }

        return INSTANCE;
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
        final String themeName = mContext.getString (theme.getNameResId ());

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
            return ToolResources.string (mContext, ThemeManager.AppTheme.valueOf (sTheme).getNameResId ());
        } catch (final Exception exception) {
            return ToolResources.string (mContext, ThemeManager.AppTheme.PINK.getNameResId ());
        }
    }

    public boolean retrieveAppShouldIntroduce (final boolean postMarker) {
        final boolean shouldIntroduce = mDefaultPreferences.getBoolean (mShouldIntroducePfKey, true);
        mDefaultPreferences.edit ().putBoolean (mShouldIntroducePfKey, postMarker).apply ();
        return shouldIntroduce;
    }

}
