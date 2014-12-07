package org.brainail.Everboxing.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.views.GooglePsErrorDialog;
import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.manager.SettingsManager;
import org.brainail.Everboxing.utils.tool.ToolAuth;
import org.brainail.Everboxing.utils.tool.ToolDriveScope;
import org.brainail.Everboxing.utils.tool.ToolPhone;
import org.brainail.Everboxing.utils.tool.ToolTasks;
import org.brainail.Everboxing.utils.tool.ToolUI;

import java.lang.ref.WeakReference;

import static org.brainail.Everboxing.utils.Plogger.LogScope;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 *
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class AuthorizationFlow {

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
        SettingsManager.getInstance().removeAccountDetails();
        ToolTasks.safeExecuteAuthTask(formUnauthorizeTask());
        mEmail = null;
    }

    public void authorize() {
        final Activity activity = getActivity();
        final int statusCode = isGooglePlayServicesAvailable(activity);

        if (ConnectionResult.SUCCESS == statusCode) {
            Plogger.logI(LogScope.AUTH, "Success connection result to Gps");
            grabUser();
        } else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            Plogger.logW(LogScope.AUTH, "User recoverable error connection result to Gps, statusCode: %d", statusCode);
            GooglePsErrorDialog.show(activity, statusCode);
        } else if (ConnectionResult.DEVELOPER_ERROR != statusCode) {
            Plogger.logE(LogScope.AUTH, "Unrecoverable error: %d", statusCode);
            ToolUI.showToast(activity, R.string.auth_flow_device_suitability);
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
        if (!TextUtils.isEmpty(SettingsManager.getInstance().retrieveAccountEmail())) {
            Plogger.logI(LogScope.AUTH, "User is already authorized");
            ToolUI.showToast(getActivity(), R.string.auth_flow_sign_in_twice);
        } else if (TextUtils.isEmpty(mEmail)) {
            Plogger.logI(LogScope.AUTH, "No email address, try to pick user");
            pickUser();
        } else {
            if (ToolPhone.hasNetworkConnection(getActivity())) {
                ToolTasks.safeExecuteAuthTask(formAuthorizeTask());
            } else {
                Plogger.logW(LogScope.AUTH, "No network connection available");
                ToolUI.showToast(getActivity(), R.string.auth_flow_no_network);
            }
        }
    }

    private void pickUser() {
        Plogger.logI(LogScope.AUTH, "Picking user account ...");
        final Activity activity = getActivity();
        if (null != activity) {
            final Intent intent = ToolAuth.formChooseGoogleAccountsIntent();
            activity.startActivityForResult(intent, AuthRequestCode.PICK_ACCOUNT);
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
                    final int requestCode = AuthRequestCode.RECOVER_FROM_PLAY_SERVICES_ERROR;
                    GooglePsErrorDialog.show(activity, statusCode, requestCode);
                } else if (exception instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    final Intent intent = ((UserRecoverableAuthException) exception).getIntent();
                    activity.startActivityForResult(intent, AuthRequestCode.RECOVER_FROM_AUTH_ERROR);
                }
            }
        });
    }

    public boolean handleOnActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (AuthRequestCode.PICK_ACCOUNT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Plogger.logI(LogScope.AUTH, "RESULT_OK for PICK_ACCOUNT request code");
                withEmail(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)).grabUser();
            } else if (Activity.RESULT_CANCELED == resultCode) {
                Plogger.logW(LogScope.AUTH, "RESULT_CANCELED for PICK_ACCOUNT request code");
                ToolUI.showToast(getActivity(), R.string.auth_flow_pick_account);
            }

            return true;
        } else if (AuthRequestCode.isRecoverableError(requestCode)) {
            Plogger.logW(LogScope.AUTH, "RESULT_OK for error recover request code");
            handleAuthorizeErrorResultAfterRecoverableError(resultCode, data);

            return true;
        }

        return false;
    }

    private void handleAuthorizeErrorResultAfterRecoverableError(final int resultCode, final Intent data) {
        if (null == data) {
            Plogger.logW(LogScope.AUTH, "No data after error recover");
            ToolUI.showToast(getActivity(), R.string.auth_flow_unknown_error);
        } else if (Activity.RESULT_OK == resultCode) {
            Plogger.logI(LogScope.AUTH, "RESULT_OK after error recover");
            ToolTasks.safeExecuteAuthTask(formAuthorizeTask());
        } else if (Activity.RESULT_CANCELED == resultCode) {
            Plogger.logW(LogScope.AUTH, "RESULT_CANCELED after error recover. User rejected authorization");
            ToolUI.showToast(getActivity(), R.string.auth_flow_rejected);
        } else {
            Plogger.logW(LogScope.AUTH, "Unknown error after error recover");
            ToolUI.showToast(getActivity(), R.string.auth_flow_unknown_error);
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
