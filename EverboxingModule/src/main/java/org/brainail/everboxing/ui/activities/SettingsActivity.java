package org.brainail.Everboxing.ui.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.auth.AuthUserInfo;
import org.brainail.Everboxing.auth.AuthorizationFlow;
import org.brainail.Everboxing.utils.SettingsManager;
import org.brainail.Everboxing.utils.ToolStrings;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public class SettingsActivity
        extends BaseActivity
        implements AuthorizationFlow.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSettingsBox();
    }

    @Override
    protected Integer getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    private void initSettingsBox() {
        // Display the fragment as the main content
        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
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
        topPaddingWorkaround();
    }

    private void topPaddingWorkaround() {
        try {
            final ListView allPreferences = (ListView) findViewById(android.R.id.list);
            final ViewGroup parent = (ViewGroup) allPreferences.getParent();
            parent.setPadding(parent.getPaddingLeft(), 0, parent.getPaddingRight(), parent.getPaddingBottom());
        } catch (Exception exception) {
            // Do nothing
        }
    }

    // A preference value change listener that updates the preference's summary to reflect its new value.
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
                    final Context context = preference.getContext();
                    final Ringtone ringManager = RingtoneManager.getRingtone(context, Uri.parse(value.toString()));
                    preference.setSummary(null == ringManager ? null : ringManager.getTitle(preference.getContext()));
                }
            } else {
                preference.setSummary(value.toString());
            }

            return true;
        }

    };

     // Binds a preference's summary to its value. More specifically, when the
     // preference's value is changed, its summary (line of text below the
     // preference title) is updated to reflect the value. The summary is also
     // immediately updated upon calling this method. The exact display format is
     // dependent on the type of preference.
    private static void bindPreferenceSummary(final Preference preference, final String defSummary) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(SUMMARY_BINDER);

        final SharedPreferences preferences = SettingsManager.getInstance().defaultPreferences();
        final String value = preferences.getString(preference.getKey(), defSummary);

        // Trigger the listener immediately with the preference's current value.
        SUMMARY_BINDER.onPreferenceChange(preference, value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (findSettingsFragment().canHandleOnActivityResult(requestCode, resultCode, data)) return;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthSucceed(final AuthUserInfo userInfo) {
        SettingsManager.getInstance().saveAccountDetails(userInfo);

        // To ensure that it will happen on the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findSettingsFragment().onChangeAccount(userInfo.email);
            }
        });
    }

    private SettingsFragment findSettingsFragment() {
        return (SettingsFragment) getFragmentManager().findFragmentById(R.id.container);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        private AuthorizationFlow mAuthorizationFlow;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mAuthorizationFlow = new AuthorizationFlow(getActivity());
            addPreferencesFromResource(R.xml.settings_main);

            // Bind the summaries of (EditText, List, Dialog, Ringtone) preferences to
            // their values. When their values change, their summaries are updated
            // to reflect the new value, per the Android Design guidelines.
            final String defAddAccountSummary = getString(R.string.settings_add_account_summary);
            bindPreferenceSummary(findPreference(getString(R.string.settings_add_account_key)), defAddAccountSummary);
            bindPreferenceSummary(findPreference(getString(R.string.settings_sign_out_account_key)), ToolStrings.EMPTY);

            // Set click listeners
            setOnClickListener(getString(R.string.settings_add_account_key));
            setOnClickListener(getString(R.string.settings_sign_out_account_key));
        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            if (getString(R.string.settings_add_account_key).equals(preference.getKey())) {
                mAuthorizationFlow.authorize();
            } else if (getString(R.string.settings_sign_out_account_key).equals(preference.getKey())) {
                mAuthorizationFlow.unauthorize();
                onChangeAccount(getString(R.string.settings_add_account_summary));
            }

            return false;
        }

        private void onChangeAccount(final String accountDescription) {
            final Preference preference = findPreference(getString(R.string.settings_add_account_key));
            SUMMARY_BINDER.onPreferenceChange(preference, accountDescription);
        }

        private void setOnClickListener(final String preferenceKey) {
            final Preference preference = findPreference(preferenceKey);
            if (null != preference) {
                preference.setOnPreferenceClickListener(this);
            }
        }

        private boolean canHandleOnActivityResult(int requestCode, int resultCode, Intent data) {
            return mAuthorizationFlow.handleOnActivityResult(requestCode, resultCode, data);
        }

    }

}
