package org.brainail.EverboxingLexis.utils.chrome;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsService;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for Custom Tabs.
 */
public class CustomTabsHelper {

    public static abstract class ChromePackages {
        public static final String STABLE_PACKAGE = "com.android.chrome";
        public static final String BETA_PACKAGE = "com.chrome.beta";
        public static final String DEV_PACKAGE = "com.chrome.dev";
        public static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    }

    public static final String EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";

    public static String sPackageNameToUse;

    private CustomTabsHelper () {}

    public static void addKeepAliveExtra (Context context, Intent intent) {
        final Intent keepAliveIntent = new Intent ().setClassName (
                context.getPackageName (),
                CustomTabsKeepAliveService.class.getCanonicalName ()
        );

        intent.putExtra (EXTRA_CUSTOM_TABS_KEEP_ALIVE, keepAliveIntent);
    }

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     * <p/>
     * This is <strong>not</strong> threadsafe.
     *
     * @param context {@link Context} to use for accessing {@link PackageManager}.
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    public static String getPackageNameToUse (final Context context) {
        if (null != sPackageNameToUse) return sPackageNameToUse;

        final PackageManager packageManager = context.getPackageManager ();

        // Get default VIEW intent handler.
        final Intent activityIntent = new Intent (Intent.ACTION_VIEW, Uri.parse ("http://www.example.com"));
        final ResolveInfo defaultViewHandlerInfo = packageManager.resolveActivity (activityIntent, 0);
        String defaultViewHandlerPackageName = null;

        if (null != defaultViewHandlerInfo) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Get all apps that can handle VIEW intents and had warmup service
        final List<ResolveInfo> resolvedActivityList = packageManager.queryIntentActivities (activityIntent, 0);
        final List<String> packagesSupportingCustomTabs = new ArrayList<> ();
        for (final ResolveInfo resolveInfo : resolvedActivityList) {
            final Intent serviceIntent = new Intent ();
            serviceIntent.setAction (CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage (resolveInfo.activityInfo.packageName);

            if (null != packageManager.resolveService (serviceIntent, 0)) {
                packagesSupportingCustomTabs.add (resolveInfo.activityInfo.packageName);
            }
        }

        // No handlers
        if (packagesSupportingCustomTabs.isEmpty ()) {
            sPackageNameToUse = null;
        } else

        // Only one
        if (1 == packagesSupportingCustomTabs.size ()) {
            sPackageNameToUse = packagesSupportingCustomTabs.get (0);
        } else

        // Try to use the default
        if (! TextUtils.isEmpty (defaultViewHandlerPackageName)
                && ! hasSpecializedHandlerIntents (context, activityIntent)
                && packagesSupportingCustomTabs.contains (defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else

        // Stable
        if (packagesSupportingCustomTabs.contains (ChromePackages.STABLE_PACKAGE)) {
            sPackageNameToUse = ChromePackages.STABLE_PACKAGE;
        } else

        // Beta
        if (packagesSupportingCustomTabs.contains (ChromePackages.BETA_PACKAGE)) {
            sPackageNameToUse = ChromePackages.BETA_PACKAGE;
        } else

        // Dev
        if (packagesSupportingCustomTabs.contains (ChromePackages.DEV_PACKAGE)) {
            sPackageNameToUse = ChromePackages.DEV_PACKAGE;
        } else

        // Local
        if (packagesSupportingCustomTabs.contains (ChromePackages.LOCAL_PACKAGE)) {
            sPackageNameToUse = ChromePackages.LOCAL_PACKAGE;
        }

        return sPackageNameToUse;
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     *
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private static boolean hasSpecializedHandlerIntents (Context context, Intent intent) {
        try {
            final PackageManager pm = context.getPackageManager ();
            final List<ResolveInfo> handlers = pm.queryIntentActivities (intent, PackageManager.GET_RESOLVED_FILTER);
            if (! (null != handlers && ! handlers.isEmpty ())) return false;

            for (final ResolveInfo resolveInfo : handlers) {
                final IntentFilter filter = resolveInfo.filter;
                if (null == filter || filter.countDataAuthorities () == 0 || filter.countDataPaths () == 0) continue;
                if (null == resolveInfo.activityInfo) continue;
                return true;
            }
        } catch (final Exception exception) {
            Log.e ("[CustomTabsHelper]", "Runtime exception while getting specialized handlers");
        }
        return false;
    }

}

