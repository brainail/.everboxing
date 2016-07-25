package org.brainail.EverboxingSplashFlame.ui.activities.common;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.api.UserInfoApi;
import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingSplashFlame.ui.fragments.common.SettingsFragment;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolUI;

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
public class SettingsActivity
        extends BaseActivity
        implements UserInfoApi.AuthCallback {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // Display the fragment as the main content
        initSettingsBox ();
    }

    @Override
    protected Integer getLayoutResourceId () {
        return R.layout.activity_settings;
    }

    @Override
    protected Integer getPrimaryToolbarLayoutResourceId () {
        return R.id.toolbar_primary;
    }

    private void initSettingsBox () {
        if (null == getFragmentManager ().findFragmentByTag (SettingsFragment.MANAGER_TAG)) {
            final FragmentTransaction fragmentTransaction = getFragmentManager ().beginTransaction ();

            fragmentTransaction.replace (
                    R.id.base_fragment_container,
                    new SettingsFragment (),
                    SettingsFragment.MANAGER_TAG
            ).commit ();
        }
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate (savedInstanceState);
        ToolUI.fixSettingsPaddingWorkaround (this);
        ToolUI.fixSettingsSelectorWorkaround (this);
    }

    // A preference value change listener that updates the preference's summary to reflect its new value.
    private static OnPreferenceChangeListener SUMMARY_BINDER = new OnPreferenceChangeListener () {
        @Override
        public boolean onPreferenceChange (final Preference preference, final Object value) {
            if (preference instanceof ListPreference) {
                final ListPreference listPreference = (ListPreference) preference;
                final int valueIndex = listPreference.findIndexOfValue (value.toString ());
                preference.setSummary (valueIndex >= 0 ? listPreference.getEntries ()[valueIndex] : null);
            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty (value.toString ())) {
                    preference.setSummary (R.string.settings_ringtone_silent_summary);
                } else {
                    final Context context = preference.getContext ();
                    final Ringtone ringManager = RingtoneManager.getRingtone (context, Uri.parse (value.toString ()));
                    preference.setSummary (null == ringManager ? null : ringManager.getTitle (preference.getContext ()));
                }
            } else {
                preference.setSummary (value.toString ());
            }
            
            return true;
        }
    };

    // Binds a preference's summary to its value. More specifically, when the
    // preference's value is changed, its summary (line of text below the
    // preference title) is updated to reflect the value. The summary is also
    // immediately updated upon calling this method. The exact display format is
    // dependent on the type of preference.
    public static void bindPreferenceSummary (
            final Preference preference,
            final String defSummary,
            final boolean useDef) {

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener (SUMMARY_BINDER);

        // Trigger the listener immediately with the preference's current value.
        if (! useDef) {
            final SharedPreferences preferences = SettingsManager.getInstance ().defaultPreferences ();
            final String value = preferences.getString (preference.getKey (), defSummary);

            SUMMARY_BINDER.onPreferenceChange (preference, value);
        } else {
            SUMMARY_BINDER.onPreferenceChange (preference, defSummary);
        }
    }

    public static void bindPreferenceSummary (final Preference preference, final String defSummary) {
        bindPreferenceSummary (preference, defSummary, false);
    }

    @Override
    public void onAuthSucceeded (UserInfoApi userInfo) {
        // ...
    }

    @Override
    public void onUnauthSucceeded () {
        // ...
    }

}
