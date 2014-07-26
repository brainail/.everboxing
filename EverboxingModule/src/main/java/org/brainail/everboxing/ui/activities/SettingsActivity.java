package org.brainail.Everboxing.ui.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.DeviceUtils;
import org.brainail.Everboxing.utils.Sdk;
import org.brainail.Everboxing.utils.StringUtils;

import java.util.List;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public class SettingsActivity extends PreferenceActivity {

    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean SINGLE_PANE_MODE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Sdk.isSdkSupported(Sdk.HONEYCOMB)) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (android.R.id.home == menuItemId) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) return;

        // Add 'account' settings.
        addPreferencesFromResource(R.xml.settings_account);

        // Bind the summaries of (EditText, List, Dialog, Ringtone) preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        // bindPreferenceSummaryToValue(findPreference(<KEY>));

        fixTopPadding();
    }

    private void fixTopPadding() {
        try {
            final ListView allPreferences = (ListView) findViewById(android.R.id.list);
            ViewGroup parent = (ViewGroup) allPreferences.getParent();
            parent.setPadding(parent.getPaddingLeft(), 0, parent.getPaddingRight(), parent.getPaddingBottom());
        } catch (Exception exception) {
            // Do nothing
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return DeviceUtils.isXLTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #SINGLE_PANE_MODE}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return SINGLE_PANE_MODE || !Sdk.isSdkSupported(Sdk.HONEYCOMB) || !DeviceUtils.isXLTablet(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.settings_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener SUMMARY_BINDER = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object value) {
            if (preference instanceof ListPreference) {
                final ListPreference listPreference = (ListPreference) preference;
                final int valueIndex = listPreference.findIndexOfValue(value.toString());
                preference.setSummary(valueIndex >= 0 ? listPreference.getEntries() [valueIndex] : null);
            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(value.toString())) {
                    preference.setSummary(R.string.settings_ringtone_silent_summary);
                } else {
                    final Ringtone ringtoneManager
                            = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(value.toString()));

                    if (null == ringtoneManager) {
                        preference.setSummary(null);
                    } else {
                        final String summary = ringtoneManager.getTitle(preference.getContext());
                        preference.setSummary(summary);
                    }
                }
            } else {
                preference.setSummary(value.toString());
            }

            return true;
        }

    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #SUMMARY_BINDER
     */
    private static void bindPreferenceSummaryToValue(final Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(SUMMARY_BINDER);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        final String value = preferences.getString(preference.getKey(), StringUtils.EMPTY_STRING);

        // Trigger the listener immediately with the preference's current value.
        SUMMARY_BINDER.onPreferenceChange(preference, value);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_account);

            // Bind the summaries of (EditText, List, Dialog, Ringtone) preferences to
            // their values. When their values change, their summaries are updated
            // to reflect the new value, per the Android Design guidelines.
            // bindPreferenceSummaryToValue(findPreference(<KEY>));
        }

    }

}
