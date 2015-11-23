package org.brainail.Everboxing.utils.helper.chrome;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import java.util.List;

/**
 * This is a helper class to manage the connection to the Custom Tabs Service and
 */
public class CustomTabsSceneHelper {

    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mConnection;
    private ConnectionCallback mConnectionCallback;

    /**
     * Opens the URL on a Custom Tab if possible; otherwise falls back to opening it via
     * {@code Intent.ACTION_VIEW}
     *
     * @param activity         The host activity
     * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available
     * @param uri              the Uri to be opened
     */
    public static void openCustomTab (
            final Activity activity,
            final CustomTabsIntent customTabsIntent,
            final Uri uri) {

        final String packageName = CustomTabsHelper.getPackageNameToUse (activity);

        // if we cant find a package name, it means there's no browser that supports
        // Custom Tabs installed. So, we fallback to a view intent
        if (null != packageName) {
            customTabsIntent.intent.setPackage (packageName);
            customTabsIntent.launchUrl (activity, uri);
        } else {
            activity.startActivity (new Intent (Intent.ACTION_VIEW, uri));
        }
    }

    /**
     * Binds the Activity to the Custom Tabs Service
     *
     * @param activity the activity to be bound to the service
     */
    public void bindCustomTabsService (final Activity activity) {
        final String packageName = CustomTabsHelper.getPackageNameToUse (activity);
        if (null == packageName || null == mClient) return;
        
        mConnection = new CustomTabsServiceConnection () {
            @Override public void onCustomTabsServiceConnected (ComponentName name, CustomTabsClient client) {
                (mClient = client).warmup (0L);

                if (null != mConnectionCallback) {
                    mConnectionCallback.onCustomTabsConnected ();
                }
                
                // Initialize a session as soon as possible.
                occupySession ();
            }

            @Override public void onServiceDisconnected (ComponentName name) {
                mClient = null;
                if (null != mConnectionCallback) {
                    mConnectionCallback.onCustomTabsDisconnected ();
                }
            }
        };

        CustomTabsClient.bindCustomTabsService (activity, packageName, mConnection);
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service
     *
     * @param activity the activity that is bound to the service
     */
    public void unbindCustomTabsService (final Activity activity) {
        if (null == mConnection) return;
        activity.unbindService (mConnection);
        mClient = null;
        mCustomTabsSession = null;
    }

    /**
     * Creates or retrieves an exiting CustomTabsSession
     *
     * @return a CustomTabsSession
     */
    public CustomTabsSession occupySession () {
        if (null == mClient) {
            // No session without client
            mCustomTabsSession = null;
        } else if (null == mCustomTabsSession) {
            // Create a new one
            mCustomTabsSession = mClient.newSession (null);
        }

        return mCustomTabsSession;
    }

    /**
     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service
     *
     * @param connectionCallback
     */
    public void setConnectionCallback (ConnectionCallback connectionCallback) {
        this.mConnectionCallback = connectionCallback;
    }

    /**
     * @return true if call to mayLaunchUrl was accepted
     * @see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}
     */
    public boolean mayLaunchUrl (Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (null == mClient) return false;

        final CustomTabsSession session = occupySession ();
        if (null == session) return false;

        return session.mayLaunchUrl (uri, extras, otherLikelyBundles);
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected
     */
    public static interface ConnectionCallback {
        /**
         * Called when the service is connected
         */
        public void onCustomTabsConnected ();

        /**
         * Called when the service is disconnected
         */
        public void onCustomTabsDisconnected ();
    }

}

