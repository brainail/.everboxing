package org.brainail.Everboxing.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.SettingsManager;
import org.brainail.Everboxing.utils.ToolAuth;
import org.brainail.Everboxing.utils.ToolDriveScope;
import org.brainail.Everboxing.utils.ToolPhone;
import org.brainail.Everboxing.utils.ToolTasks;
import org.brainail.Everboxing.utils.ToolUI;

import java.lang.ref.WeakReference;

import static org.brainail.Everboxing.utils.Plogger.LogScope;

/**
 * User: brainail<br/>
 * Date: 26.07.14<br/>
 * Time: 18:25<br/>
 */
public class AuthorizationFlow {

    public final static String [] ACCOUNT_TYPES = {"com.google"};

    public interface Callback {
        public void onAuthSucceed(final AuthUserInfo userInfo);
    }

    private WeakReference<Activity> mActivityRef;
    private String mEmail;

    private AuthorizationFlow() {}

    public AuthorizationFlow(final Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        mEmail = SettingsManager.getInstance().retrieveAccountEmail();
    }

    public AuthorizationFlow withActivity(final Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        return this;
    }

    public AuthorizationFlow withEmail(final String email) {
        Plogger.logD(LogScope.AUTH, "An email has set: %s", email);
        mEmail = email;
        return this;
    }

    public Activity getActivity() {
        return mActivityRef.get();
    }

    public String getEmail() {
        return mEmail;
    }

    public void unauthorize() {
        mEmail = null;
        SettingsManager.getInstance().removeAccountDetails();
        ToolTasks.safeExecuteAuthTask(formUnauthorizeTask());
    }

    public void authorize() {
        final Activity activity = getActivity();
        final int statusCode = isGooglePlayServicesAvailable(activity);

        if (ConnectionResult.SUCCESS == statusCode) {
            Plogger.logI(LogScope.AUTH, "Success connection result to Gps");
            grabUser();
        } else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            Plogger.logW(LogScope.AUTH, "User recoverable error connection result to Gps, statusCode: %d", statusCode);
            GooglePlayServicesUtil.getErrorDialog(statusCode, activity, 0).show();
        } else {
            Plogger.logE(LogScope.AUTH, "Unrecoverable error");
            // FIXME: Get string from the resources
            ToolUI.showToast(activity, "Unrecoverable Error");
        }
    }

    private static int isGooglePlayServicesAvailable(final Activity activity) {
        if (null != activity) {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        } else {
            return ConnectionResult.DEVELOPER_ERROR;
        }
    }

    private void grabUser() {
        Plogger.logI(LogScope.AUTH, "Grabbing user account ...");
        if (TextUtils.isEmpty(mEmail)) {
            Plogger.logI(LogScope.AUTH, "No email address, try to pick user");
            pickUser();
        } else {
            if (ToolPhone.hasNetworkConnection(getActivity())) {
                ToolTasks.safeExecuteAuthTask(formAuthorizeTask());
            } else {
                Plogger.logW(LogScope.AUTH, "No network connection available");
                // FIXME: Get string from the resources
                ToolUI.showToast(getActivity(), "No network connection available");
            }
        }
    }

    private void pickUser() {
        Plogger.logI(LogScope.AUTH, "Picking user account ...");
        final Activity activity = getActivity();
        if (null != activity) {
            final Intent intent = ToolAuth.formChooseGoogleAccountsIntent();
            activity.startActivityForResult(intent, AuthRequestCodes.PICK_ACCOUNT);
        }
    }

    // Provides the user a response UI when an exception occurs.
    public void handleError(final Exception exception) {
        Plogger.logW(LogScope.AUTH, "An exception has occurred: %s", exception);

        final Activity activity = getActivity();
        if (null == activity) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (exception instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows the user to update the APK
                    final int statusCode = ((GooglePlayServicesAvailabilityException) exception).getConnectionStatusCode();
                    final int requestCode = AuthRequestCodes.RECOVER_FROM_PLAY_SERVICES_ERROR;
                    GooglePlayServicesUtil.getErrorDialog(statusCode, activity, requestCode).show();
                } else if (exception instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    final Intent intent = ((UserRecoverableAuthException) exception).getIntent();
                    activity.startActivityForResult(intent, AuthRequestCodes.RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    public boolean handleOnActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (AuthRequestCodes.PICK_ACCOUNT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Plogger.logI(LogScope.AUTH, "RESULT_OK for PICK_ACCOUNT request code");
                withEmail(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)).grabUser();
            } else if (Activity.RESULT_CANCELED == resultCode) {
                Plogger.logW(LogScope.AUTH, "RESULT_CANCELED for PICK_ACCOUNT request code");
                // FIXME: Get string from the resources
                ToolUI.showToast(getActivity(), "You must pick an account");
            }
        } else if (AuthRequestCodes.isErrorRecover(requestCode) && Activity.RESULT_OK == resultCode) {
            Plogger.logW(LogScope.AUTH, "RESULT_OK for error recover request code");
            handleAuthorizeResultAfterErrorRecover(resultCode, data);
            return true;
        }

        return false;
    }

    private void handleAuthorizeResultAfterErrorRecover(final int resultCode, final Intent data) {
        if (null == data) {
            Plogger.logW(LogScope.AUTH, "No data after error recover");
            // FIXME: Get string from the resources
            ToolUI.showToast(getActivity(), "Unknown error, click the button again");
        } else if (Activity.RESULT_OK == resultCode) {
            Plogger.logI(LogScope.AUTH, "RESULT_OK after error recover");
            ToolTasks.safeExecuteAuthTask(formAuthorizeTask());
        } else if (Activity.RESULT_CANCELED == resultCode) {
            Plogger.logW(LogScope.AUTH, "RESULT_CANCELED after error recover. User rejected authorization");
            // FIXME: Get string from the resources
            ToolUI.showToast(getActivity(), "User rejected authorization");
        } else {
            Plogger.logW(LogScope.AUTH, "Unknown error after error recover");
            // FIXME: Get string from the resources
            ToolUI.showToast(getActivity(), "Unknown error, click the button again");
        }
    }

    void onAuthTokenFetched(final String authToken) {
        final Activity activity = getActivity();
        if (activity instanceof Callback) {
            ((Callback) activity).onAuthSucceed(new AuthUserInfo(mEmail));
        }
    }

    private FetchTokenTask formAuthorizeTask() {
        return new FetchTokenTask(this, mEmail, ToolDriveScope.formFullScope());
    }

    private ClearTokenTask formUnauthorizeTask() {
        return new ClearTokenTask(this, mEmail);
    }

}
