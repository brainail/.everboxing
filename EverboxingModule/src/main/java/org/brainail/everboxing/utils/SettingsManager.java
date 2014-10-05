package org.brainail.Everboxing.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;
import org.brainail.Everboxing.auth.AuthorizationFlow;

/**
 * User: brainail<br/>
 * Date: 10.08.14<br/>
 * Time: 16:11<br/>
 */
public final class SettingsManager {

    private SharedPreferences mDefaultPreferences;
    private String mAccountPfKey;

    private SettingsManager() {
        initializePreferences();
        initializePreferencesKeys();
    }

    private void initializePreferencesKeys() {
        mAccountPfKey = JApplication.appContext().getString(R.string.settings_add_account_key);
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

    public void saveAccountDetails(final AuthorizationFlow.UserAuthInfo userInfo) {
        mDefaultPreferences.edit().putString(mAccountPfKey, userInfo.email).apply();
    }

}
