package org.brainail.EverboxingSplashFlame.api.google;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.api.ClientApi;
import org.brainail.EverboxingSplashFlame.api.RequestCodeApi;
import org.brainail.EverboxingSplashFlame.api.UserInfoApi;
import org.brainail.EverboxingSplashFlame.ui.views.dialogs.GooglePsErrorDialog;
import org.brainail.EverboxingSplashFlame.utils.LogScope;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolUI;
import org.brainail.EverboxingTools.utils.PooLogger;

import java.lang.ref.WeakReference;

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
public class PlayServices
        extends ClientApi<GoogleApiClient>
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private WeakReference<AppCompatActivity> mScene;

    // Main API
    private GoogleApiClient mApi;

    // Key for saved instance to store error flag
    private static final String EXTRA_RESOLVING_ERROR_FLAG
            = "org.brainail.Everboxing.extra#resolving.error.flag";

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    // User info
    private String mEmail;

    private PlayServices () {
        mScene = new WeakReference<> (null);
    }

    public PlayServices (final AppCompatActivity scene, final Bundle savedState) {
        this (scene);

        // Restore from state
        if (null != savedState) {
            mResolvingError = savedState.getBoolean (EXTRA_RESOLVING_ERROR_FLAG, false);
        }
    }

    public PlayServices (final AppCompatActivity scene) {
        this ();
        withScene (scene);
        withEmail (SettingsManager.getInstance().retrievePlayAccountEmail ());
        initAPI ();
    }

    private PlayServices withEmail (final String email) {
        PooLogger.debug (LogScope.PLAY_SERVICES_AUTH, "An email has set: %s", email);
        mEmail = email;
        return this;
    }

    public PlayServices withScene (final AppCompatActivity scene) {
        mScene.clear ();
        mScene = new WeakReference<AppCompatActivity> (scene);
        return this;
    }

    private AppCompatActivity scene () {
        return mScene.get ();
    }

    @Override
    public UserInfoApi.AuthCallback authorizer () {
        final AppCompatActivity activity = scene ();
        return !(activity instanceof UserInfoApi.AuthCallback) ? null : (UserInfoApi.AuthCallback) activity;
    }

    @Override
    public void revoke () {
        super.revoke ();
        PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Revoke was called");

        Plus.AccountApi.clearDefaultAccount (mApi);
        Plus.AccountApi.revokeAccessAndDisconnect (mApi).setResultCallback (new ResultCallback<Status> () {
            @Override
            public void onResult (Status status) {
                PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Access has been revoked, status: " + status);
                withEmail (null);
                disconnect ();
                onUnauthSucceeded ();
            }
        });
    }

    @Override
    public void connect () {
        super.connect ();
        PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Connect was called");
        if (!mResolvingError && null != mApi && !isConnected ()) {
            mApi.connect ();
        }
    }

    @Override
    public void disconnect () {
        super.disconnect ();
        PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Disconnect was called");
        if (null != mApi) {
            mApi.disconnect ();
        }
    }

    @Override
    protected GoogleApiClient initAPI () {
        super.initAPI ();

        if (null == mApi) {
            mApi = new GoogleApiClient.Builder (scene ())
                    .addApi (Plus.API)
                    .addApi (Drive.API)
                    .addScope (Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks (this)
                    .addOnConnectionFailedListener (this)
                    .build ();
        }

        return mApi;
    }

    @Override
    public boolean isConnected () {
        return null != mApi && mApi.isConnected ();
    }

    private boolean isConnectedOrConnecting () {
        return null != mApi && (mApi.isConnected () || mApi.isConnecting ());
    }

    @Override
    public boolean handleOnResult (final int requestCode, final int resultCode, final Intent data) {
        PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Handling result code ...");
        if (super.handleOnResult (requestCode, resultCode, data)) return true;

        if (RequestCodeApi.REQUEST_RESOLVE_PLAY_SERVICES_ERROR == requestCode) {
            mResolvingError = false;
            if (AppCompatActivity.RESULT_OK == resultCode) {
                // Make sure the app is not already connected or attempting to connect
                if (null != mApi && !isConnectedOrConnecting ()) {
                    mApi.connect();
                }

                return true;
            } else {
                PooLogger.warn (LogScope.PLAY_SERVICES_AUTH, "No resolution for play services error");
            }
        }

        return false;
    }

    @Override
    public UserInfoApi formUserInfo () {
        return new UserInfoApi (mEmail);
    }

    public static UserInfoApi formSettingsUserInfo () {
        return new UserInfoApi (SettingsManager.getInstance ().retrievePlayAccountEmail ());
    }

    @Override
    public void onConnected (Bundle bundle) {
        PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Successfully connected to play services");

        // Fallback
        final AppCompatActivity scene = scene ();
        if (scene instanceof GoogleApiClient.ConnectionCallbacks) {
            ((GoogleApiClient.ConnectionCallbacks) scene).onConnected (bundle);
        }

        final String accountName = Plus.AccountApi.getAccountName (mApi);
        if (!TextUtils.isEmpty (accountName)) {
            withEmail (accountName);
            onAuthSucceeded (formUserInfo ());
        }
    }

    @Override
    public void onConnectionSuspended (int cause) {
        PooLogger.warn (LogScope.PLAY_SERVICES_AUTH, "Connection suspended, cause: %d", cause);

        // Fallback
        final AppCompatActivity scene = scene ();
        if (scene instanceof GoogleApiClient.ConnectionCallbacks) {
            ((GoogleApiClient.ConnectionCallbacks) scene).onConnectionSuspended (cause);
        }
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {
        PooLogger.warn (LogScope.PLAY_SERVICES_AUTH, "Connection is failed: %s", connectionResult);

        // Fallback
        final AppCompatActivity scene = scene ();
        if (scene instanceof GoogleApiClient.OnConnectionFailedListener) {
            ((GoogleApiClient.OnConnectionFailedListener) scene).onConnectionFailed (connectionResult);
        }

        if (mResolvingError) {
            // Already attempting to resolve an error ...
            PooLogger.info (LogScope.PLAY_SERVICES_AUTH, "Skip resolution ...");
        } else if (connectionResult.hasResolution ()) {
            try {
                mResolvingError = true;
                if (null != scene) {
                    connectionResult.startResolutionForResult (scene, RequestCodeApi.REQUEST_RESOLVE_PLAY_SERVICES_ERROR);
                }
            } catch (final IntentSender.SendIntentException exception) {
                PooLogger.error (LogScope.PLAY_SERVICES_AUTH, "Error while starting resolution ...");
                ToolUI.showToast(scene, R.string.auth_flow_unknown_error);
            }
        } else {
            GooglePsErrorDialog.show(scene, connectionResult.getErrorCode(), 0);
            mResolvingError = true;
        }
    }

    @Override
    public GoogleApiClient api () {
        return isConnected () ? mApi : null;
    }

    @Override
    public void onStart () {
        super.onStart ();
        connect ();
    }

    @Override
    public void onStop () {
        super.onStop ();
        disconnect ();
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
        disconnect ();
        withScene (null);
    }

    public void onErrorDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onSave (final Bundle outState) {
        super.onSave (outState);
        outState.putBoolean (EXTRA_RESOLVING_ERROR_FLAG, mResolvingError);
    }

    @Override
    public void onAuthSucceeded (UserInfoApi userInfo) {
        super.onAuthSucceeded (userInfo);
        SettingsManager.getInstance ().savePlayAccountDetails (userInfo);
    }

    @Override
    public void onUnauthSucceeded () {
        super.onUnauthSucceeded ();
        SettingsManager.getInstance ().removePlayAccountDetails ();
    }

    @Override
    public boolean useOn (AppCompatActivity scene) {
        return (scene instanceof Supportable) && ((Supportable) scene).usePlayServices ();
    }

    public static int isGooglePlayServicesAvailable (final AppCompatActivity activity) {
        if (null != activity) {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable (activity);
        } else {
            return ConnectionResult.DEVELOPER_ERROR;
        }
    }

}
