package org.brainail.EverboxingSplashFlame.ui.fragments.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.api.ClientApi;
import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.common.SettingsActivity;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BasePreferenceFragment;
import org.brainail.EverboxingSplashFlame.ui.views.BaseIcon;
import org.brainail.EverboxingSplashFlame.ui.views.dialogs.ThemeChooser;
import org.brainail.EverboxingSplashFlame.ui.views.preference.SwitchPreferenceCompat;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;

import static com.malinskiy.materialicons.Iconify.IconValue.zmdi_email;
import static com.malinskiy.materialicons.Iconify.IconValue.zmdi_info_outline;
import static com.malinskiy.materialicons.Iconify.IconValue.zmdi_palette;
import static com.malinskiy.materialicons.Iconify.IconValue.zmdi_refresh_sync;
import static com.malinskiy.materialicons.Iconify.IconValue.zmdi_shield_security;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
@TargetApi (Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment
        extends BasePreferenceFragment
        implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String MANAGER_TAG = "org.brainail.Everboxing.tag#settings.fragment";

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        addPreferencesFromResource (R.xml.settings_main);

        // Bind the summaries of (EditText, List, Dialog, Ringtone) preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        // ...

        // Sync data
        final Preference syncDataPf = findPreference (getString (R.string.settings_sync_account_key));
        syncDataPf.setIcon (BaseIcon.controlIcon (getActivity (), zmdi_refresh_sync));
        if (!syncDataPf.isEnabled ()) {
            final PreferenceScreen rootScreen
                    = (PreferenceScreen) findPreference (getString (R.string.settings_preference_screen_root));
            final PreferenceCategory dataTransferCategory
                    = (PreferenceCategory) findPreference (getString (R.string.settings_category_data_transfer));
            rootScreen.removePreference (dataTransferCategory);
        }

        // Change theme
        final String defChangeThemeSummary = SettingsManager.getInstance ().retrieveAppThemeSummary ();
        final Preference changeThemePf = findPreference (getString (R.string.settings_change_theme_key));
        changeThemePf.setIcon (BaseIcon.controlIcon (getActivity (), zmdi_palette));
        SettingsActivity.bindPreferenceSummary (changeThemePf, defChangeThemeSummary, true);

        // Feedback
        final Preference feedbackPf = findPreference (getString (R.string.settings_feedback_key));
        feedbackPf.setIcon (BaseIcon.controlIcon (getActivity (), zmdi_email));

        // Privacy Policy
        final Preference privacyPolicyPf = findPreference (getString (R.string.settings_open_privacy_policy_key));
        privacyPolicyPf.setIcon (BaseIcon.controlIcon (getActivity (), zmdi_shield_security));

        // About
        final Preference aboutPf = findPreference (getString (R.string.settings_open_about_key));
        aboutPf.setIcon (BaseIcon.controlIcon (getActivity (), zmdi_info_outline));

        // Set click listeners
        setOnClickListener (getString (R.string.settings_sync_account_key));
        setOnClickListener (getString (R.string.settings_change_theme_key));
        setOnClickListener (getString (R.string.settings_feedback_key));
        setOnClickListener (getString (R.string.settings_open_privacy_policy_key));
        setOnClickListener (getString (R.string.settings_open_about_key));
    }

    @Override
    public boolean onPreferenceClick (final Preference preference) {
        if (getString (R.string.settings_sync_account_key).equals (preference.getKey ())) {
            // Sync
            final Activity scene = getActivity ();
            if ((scene instanceof ClientApi.Supportable) && ((SwitchPreferenceCompat) preference).isChecked ()) {
                final ClientApi api = ((ClientApi.Supportable) scene).getPlayServices ();
                if (null != api) {
                    api.connect ();
                }
            }
        } else if (getString (R.string.settings_change_theme_key).equals (preference.getKey ())) {
            // Theme
            new ThemeChooser ().show (getActivity ().getFragmentManager (), ThemeChooser.MANAGER_TAG);
        } else if (getString (R.string.settings_feedback_key).equals (preference.getKey ())) {
            // Feedback
            mNavigator.sendFeedbackOrSuggestion ().start ();
        }  else if (getString (R.string.settings_open_privacy_policy_key).equals (preference.getKey ())) {
            // Privacy Policy
            ((BaseActivity) getActivity ()).openUrl (getString (R.string.privacy_policy_url));
        } else if (getString (R.string.settings_open_about_key).equals (preference.getKey ())) {
            // About
            mNavigator.aboutScreen ().start ();
        }

        return false;
    }

    private void setOnClickListener (final String preferenceKey) {
        final Preference preference = findPreference (preferenceKey);
        if (null != preference) {
            preference.setOnPreferenceClickListener (this);
        }
    }

    @Override
    public void onResume () {
        super.onResume ();
        getPreferenceManager ().getSharedPreferences ().registerOnSharedPreferenceChangeListener (this);
    }

    @Override
    public void onPause () {
        super.onPause ();
        getPreferenceScreen ().getSharedPreferences ().unregisterOnSharedPreferenceChangeListener (this);
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        // No-impl
    }

}
