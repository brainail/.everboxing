package org.brainail.Everboxing.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.brainail.Everboxing.utils.DriveScope;
import org.brainail.Everboxing.utils.PhoneUtils;
import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.UIUtils;

import static org.brainail.Everboxing.utils.Plogger.LogScope;

/**
 * User: brainail<br/>
 * Date: 26.07.14<br/>
 * Time: 18:25<br/>
 */
public class AuthorizationFlow {

    private final static String [] ACCOUNT_TYPES = {"com.google"};

    public static final class RequestCodes {

        private static final int BASE = 1000;
        public static final int PICK_ACCOUNT = BASE;
        public static final int RECOVER_FROM_AUTH_ERROR = BASE + 1;
        public static final int RECOVER_FROM_PLAY_SERVICES_ERROR = BASE + 2;

        public static boolean isErrorRecover(final int code) {
            boolean result = RECOVER_FROM_AUTH_ERROR == code;
            result |= RECOVER_FROM_PLAY_SERVICES_ERROR == code;
            return result;
        }

    }

    private Activity mActivity;
    private String mEmail;

    public AuthorizationFlow(final Activity activity) {
        mActivity = activity;
        mEmail = null;
    }

    public AuthorizationFlow withActivity(final Activity activity) {
        mActivity = activity;
        return this;
    }

    public AuthorizationFlow withEmail(final String email) {
        Plogger.logD(LogScope.AUTH, "An email has set: %s", email);
        mEmail = email;
        return this;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public String getEmail() {
        return mEmail;
    }

    public void authorize() {
        final int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (ConnectionResult.SUCCESS == statusCode) {
            Plogger.logI(LogScope.AUTH, "Success connection result to Gps");
            grabUser();
        } else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            Plogger.logW(LogScope.AUTH, "User recoverable error connection result to Gps, statusCode: %d", statusCode);
            GooglePlayServicesUtil.getErrorDialog(statusCode, mActivity, 0).show();
        } else {
            Plogger.logE(LogScope.AUTH, "Unrecoverable error");
            // FIXME: Get string from the resources
            UIUtils.showToast(mActivity, "Unrecoverable Error");
        }
    }

    private void grabUser() {
        Plogger.logI(LogScope.AUTH, "Grabbing user account ...");
        if (TextUtils.isEmpty(mEmail)) {
            Plogger.logI(LogScope.AUTH, "No email address, try to pick user");
            pickUser();
        } else {
            if (PhoneUtils.hasNetworkConnection(mActivity)) {
                formAuthTask().execute();
            } else {
                Plogger.logW(LogScope.AUTH, "No network connection available");
                // FIXME: Get string from the resources
                UIUtils.showToast(mActivity, "No network connection available");
            }
        }
    }

    private void pickUser() {
        Plogger.logI(LogScope.AUTH, "Picking user account ...");
        final Intent intent = AccountPicker.newChooseAccountIntent(null, null, ACCOUNT_TYPES, false, null, null, null, null);
        mActivity.startActivityForResult(intent, RequestCodes.PICK_ACCOUNT);
    }

    // Provides the user a response UI when an exception occurs.
    public void handleError(final Exception exception) {
        Plogger.logW(LogScope.AUTH, "An exception has occurred: %s", exception);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (exception instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows the user to update the APK
                    final int statusCode = ((GooglePlayServicesAvailabilityException) exception).getConnectionStatusCode();
                    final int requestCode = RequestCodes.RECOVER_FROM_PLAY_SERVICES_ERROR;
                    GooglePlayServicesUtil.getErrorDialog(statusCode, mActivity, requestCode).show();
                } else if (exception instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    final Intent intent = ((UserRecoverableAuthException) exception).getIntent();
                    mActivity.startActivityForResult(intent, RequestCodes.RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    public boolean handleOnActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (RequestCodes.PICK_ACCOUNT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Plogger.logI(LogScope.AUTH, "RESULT_OK for PICK_ACCOUNT request code");
                withEmail(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)).grabUser();
            } else if (Activity.RESULT_CANCELED == resultCode) {
                Plogger.logW(LogScope.AUTH, "RESULT_CANCELED for PICK_ACCOUNT request code");
                // FIXME: Get string from the resources
                UIUtils.showToast(mActivity, "You must pick an account");
            }
        } else if (RequestCodes.isErrorRecover(requestCode) && Activity.RESULT_OK == resultCode) {
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
            UIUtils.showToast(mActivity, "Unknown error, click the button again");
        } else if (Activity.RESULT_OK == resultCode) {
            Plogger.logI(LogScope.AUTH, "RESULT_OK after error recover");
            formAuthTask().execute();
        } else if (Activity.RESULT_CANCELED == resultCode) {
            Plogger.logW(LogScope.AUTH, "RESULT_CANCELED after error recover. User rejected authorization");
            // FIXME: Get string from the resources
            UIUtils.showToast(mActivity, "User rejected authorization");
        } else {
            Plogger.logW(LogScope.AUTH, "Unknown error after error recover");
            // FIXME: Get string from the resources
            UIUtils.showToast(mActivity, "Unknown error, click the button again");
        }
    }

    private AbstractFetchTokenTask formAuthTask() {
        return new FetchTokenTask(this, mEmail, DriveScope.formFullScope());
    }

}
