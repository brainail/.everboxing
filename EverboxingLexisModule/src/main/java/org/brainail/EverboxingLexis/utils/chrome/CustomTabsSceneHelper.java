package org.brainail.EverboxingLexis.utils.chrome;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;

import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import java.util.List;

/**
 * This is a helper class to manage the connection to the Custom Tabs Service and
 */
public class CustomTabsSceneHelper implements CustomTabsConnectionCallbacks {

    private CustomTabsClient mClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mConnection;
    private CustomTabsConnectionCallbacks mConnectionCallback;
    private ActionInfo mCurrentAction;

    public static class ActionInfo {
        public final String action;
        public final String title;
        public final String description;

        public ActionInfo (final String action) {
            this (action, null);
        }

        public ActionInfo (final String action, final String title) {
            this (action, title, null);
        }

        public ActionInfo (final String action, final String title, final String description) {
            this.action = action;
            this.title = title;
            this.description = TextUtils.isEmpty (description) ? action : description;
        }

        public String emailTitleExtra () {
            return TextUtils.isEmpty (title) ? "" : " (" + title + ")";
        }

        public String emailBody () {
            return description;
        }

        public String shareText () {
            return TextUtils.isEmpty (title) ? action : title;
        }
    }

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
            @Override
            public void onCustomTabsServiceConnected (ComponentName name, CustomTabsClient client) {
                (mClient = client).warmup (0L);

                if (null != mConnectionCallback) {
                    mConnectionCallback.onCustomTabsConnected ();
                }
                
                // Initialize a session as soon as possible.
                occupySession ();
            }

            @Override
            public void onServiceDisconnected (ComponentName name) {
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
    public void setConnectionCallback (CustomTabsConnectionCallbacks connectionCallback) {
        mConnectionCallback = connectionCallback;
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

    public void openCustomTab (final Activity scene, final String url, final String title, final String description) {
        mCurrentAction = new ActionInfo (url, title, description);

        CustomTabsSceneHelper.openCustomTab (
                scene,
                createCustomTabIntent (scene, mCurrentAction, occupySession ()).build (),
                Uri.parse (mCurrentAction.action)
        );
    }

    private static CustomTabsIntent.Builder createCustomTabIntent (
            @NonNull Context context,
            @NonNull ActionInfo action,
            @Nullable CustomTabsSession session) {

        // Construct our intent via builder
        final CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder (session);
        // Toolbar color
        intentBuilder.setToolbarColor (ToolResources.retrievePrimaryColor (context));
        // Show title
        intentBuilder.setShowTitle (true);
        // Custom menu item > Share
        intentBuilder.addMenuItem ("Share", createPendingShareIntent (context, action));
        // Custom menu item > Email
        intentBuilder.addMenuItem ("Email", createPendingEmailIntent (context, action));
        // Specify close button icon
        // final int mainCloseResId = android.support.design.R.drawable.abc_ic_ab_back_mtrl_am_alpha;
        // final Bitmap mainCloseBitmap = BitmapFactory.decodeResource (context.getResources (), mainCloseResId);
        // intentBuilder.setCloseButtonIcon (mainCloseBitmap);
        // Specify main action icon and doings
        // final int mainActionResId = android.support.design.R.drawable.abc_ic_commit_search_api_mtrl_alpha;
        // final Bitmap mainActionBitmap = BitmapFactory.decodeResource (context.getResources (), mainActionResId);
        // intentBuilder.setActionButton (mainActionBitmap, "Notify me!", createPendingMainActionNotifyIntent (context, action));
        // Custom animation (start + exit)
        // intentBuilder.setExitAnimations (context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // intentBuilder.setStartAnimations (
        // context,
        // android.support.design.R.anim.abc_slide_in_bottom,
        // android.support.design.R.anim.abc_slide_out_bottom
        // );
        // Allow hiding for toolbar
        intentBuilder.enableUrlBarHiding ();

        return intentBuilder;
    }

    private static PendingIntent createPendingEmailIntent (
            @NonNull final Context context,
            @NonNull final ActionInfo action) {

        Intent actionIntent = new Intent (Intent.ACTION_SENDTO, Uri.parse ("mailto:"));
        actionIntent.putExtra (Intent.EXTRA_SUBJECT, "Check this out" + action.emailTitleExtra ());
        actionIntent.putExtra (Intent.EXTRA_TEXT, action.emailBody ());
        return PendingIntent.getActivity (context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createPendingShareIntent (
            @NonNull final Context context,
            @NonNull final ActionInfo action) {

        Intent actionIntent = new Intent (Intent.ACTION_SEND);
        actionIntent.setType ("text/plain");
        actionIntent.putExtra (Intent.EXTRA_TEXT, action.shareText ());
        return PendingIntent.getActivity (context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onCustomTabsConnected () {
        // ToolUI.showSnack (getView (), "Custom Tabs > Connected");

        if (null != mCurrentAction) {
            mayLaunchUrl (Uri.parse (mCurrentAction.action), null, null);
        }
    }

    @Override
    public void onCustomTabsDisconnected () {
        // ToolUI.showSnack (getView (), "Custom Tabs > Disconnected");
    }

    public void onCreateScene (final Activity scene) {
        setConnectionCallback (this);
    }

    public void onStartScene (final Activity scene) {
        bindCustomTabsService (scene);
    }

    public void onStopScene (final Activity scene) {
        unbindCustomTabsService (scene);
    }

    public void onDestroyScene (final Activity scene) {
        setConnectionCallback (null);
    }

}

