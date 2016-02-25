package org.brainail.Everboxing.ui.fragments;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.helper.chrome.CustomTabsSceneHelper;
import org.brainail.Everboxing.utils.tool.ToolResources;
import org.brainail.Everboxing.utils.tool.ToolUI;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

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
public class FragmentChromeCustomTabs extends BaseFragment implements CustomTabsSceneHelper.ConnectionCallback {

    public static enum CustomTabsAction {
        GOOGLE_PLUS ("Open Google+", "https://plus.google.com/+ЕгорМалышев"),
        MEDIUM ("Open Medium", "https://medium.com/@brainail"),
        ABOUT_ME ("Open About.Me", "https://about.me/brainail");

        private final String mActionDescription;
        private final String mActionUrl;

        private CustomTabsAction (
                final String actionDescription,
                final String actionUrl) {

            mActionDescription = actionDescription;
            mActionUrl = actionUrl;
        }

        public String actionDescription () {
            return mActionDescription;
        }

        public String actionUrl () {
            return mActionUrl;
        }
    }

    private CustomTabsSceneHelper mCustomTabsSceneHelper;
    private CustomTabsAction mCurrentAction;

    @Override public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final LinearLayout view = new LinearLayout (getActivity ());
        view.setOrientation (LinearLayout.VERTICAL);
        view.setLayoutParams (new ViewGroup.LayoutParams (MATCH_PARENT, MATCH_PARENT));
        view.setGravity (Gravity.CENTER);
        view.setBackgroundResource (R.color.md_deep_orange_400);

        addAction (view, CustomTabsAction.GOOGLE_PLUS);
        addAction (view, CustomTabsAction.MEDIUM);
        addAction (view, CustomTabsAction.ABOUT_ME);

        return view;
    }

    private void addAction (
            final ViewGroup view,
            final CustomTabsAction action) {

        final Button button = new Button (getActivity ());
        button.setText (action.actionDescription ());
        view.addView (button, new LinearLayout.LayoutParams (400, 200));

        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                open (action);
            }
        });
    }

    private void open (final CustomTabsAction action) {
        final Activity scene = getActivity ();
        mCurrentAction = action;

        CustomTabsSceneHelper.openCustomTab (
                scene,
                getCustomTabIntent (scene, action, mCustomTabsSceneHelper.occupySession ()).build (),
                Uri.parse (action.actionUrl ())
        );
    }

    public static CustomTabsIntent.Builder getCustomTabIntent(
            @NonNull Context context,
            @NonNull CustomTabsAction action,
            @Nullable CustomTabsSession session) {

        // Construct our intent via builder
        final CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder (session);
        // Toolbar color
        intentBuilder.setToolbarColor(ToolResources.retrievePrimaryColor (context));
        // Show title
        intentBuilder.setShowTitle (true);
        // Custom menu item > Share
        intentBuilder.addMenuItem("Share it!", createPendingShareIntent (context, action));
        // Custom menu item > Email
        intentBuilder.addMenuItem("Send it via email!", createPendingEmailIntent (context, action));
        // Specify close button icon
        final int mainCloseResId = android.support.design.R.drawable.abc_ic_ab_back_material;
        final Bitmap mainCloseBitmap = BitmapFactory.decodeResource (context.getResources (), mainCloseResId);
        intentBuilder.setCloseButtonIcon (mainCloseBitmap);
        // Specify main action icon and doings
        final int mainActionResId = android.support.design.R.drawable.abc_ic_commit_search_api_mtrl_alpha;
        final Bitmap mainActionBitmap = BitmapFactory.decodeResource (context.getResources (), mainActionResId);
        intentBuilder.setActionButton (mainActionBitmap, "Notify me!", createPendingMainActionNotifyIntent (context, action));
        // Custom animation (start + exit)
        // intentBuilder.setExitAnimations (context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setStartAnimations (
                context,
                android.support.design.R.anim.abc_slide_in_bottom,
                android.support.design.R.anim.abc_slide_out_bottom
        );
        // Allow hiding for toolbar
        intentBuilder.enableUrlBarHiding ();

        return intentBuilder;
    }

    @Override public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.setConnectionCallback (this);
    }

    @Override public void onCustomTabsConnected () {
        ToolUI.showSnack (getView (), "Custom Tabs > Connected");
        if (null != mCurrentAction) {
            mCustomTabsSceneHelper.mayLaunchUrl (Uri.parse (mCurrentAction.actionUrl ()), null, null);
        }
    }

    @Override public void onCustomTabsDisconnected () {
        ToolUI.showSnack (getView (), "Custom Tabs > Disconnected");
    }

    @Override public void onStart () {
        super.onStart ();
        mCustomTabsSceneHelper.bindCustomTabsService (getActivity ());
    }

    @Override public void onStop () {
        mCustomTabsSceneHelper.unbindCustomTabsService (getActivity ());
        super.onStop ();
    }

    @Override public void onDestroy () {
        mCustomTabsSceneHelper.setConnectionCallback (null);
        super.onDestroy ();
    }

    private static PendingIntent createPendingEmailIntent(
            @NonNull final Context context,
            @NonNull final CustomTabsAction action) {

        Intent actionIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "customtabs666@gmail.com", null));
        actionIntent.putExtra (Intent.EXTRA_SUBJECT, "Do this action! " + action.actionDescription ());
        actionIntent.putExtra (Intent.EXTRA_TEXT, "Check this out! " + action.actionUrl ());
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createPendingShareIntent(
            @NonNull final Context context,
            @NonNull final CustomTabsAction action) {

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType ("text/plain");
        actionIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Share this action! " + action.actionDescription () + " and go to the " + action.actionUrl ()
        );
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createPendingMainActionNotifyIntent(
            @NonNull final Context context,
            @NonNull final CustomTabsAction action) {

        Intent actionIntent = new Intent(context, CustomTabsMainActionService.class);
        actionIntent.putExtra ("Action", action);
        return PendingIntent.getService(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class CustomTabsMainActionService extends IntentService {

        public CustomTabsMainActionService () {
            super ("CustomTabsMainActionService");
        }

        @Override
        protected void onHandleIntent (Intent intent) {
            if (null != intent) {
                final CustomTabsAction action = (CustomTabsAction) intent.getSerializableExtra ("Action");
                final Notification notification = new NotificationCompat.Builder (getApplicationContext ())
                        .setSmallIcon(android.support.design.R.drawable.abc_ic_commit_search_api_mtrl_alpha)
                        .setContentText (action.actionUrl ())
                        .setContentTitle (action.actionDescription ())
                        .setContentIntent (
                                PendingIntent.getActivity (
                                        getApplicationContext (), 0,
                                        new Intent (Intent.ACTION_VIEW, Uri.parse (action.actionUrl ())),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                )
                        )
                        .setSound(RingtoneManager.getDefaultUri (RingtoneManager.TYPE_RINGTONE))
                        .build ();

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(-1, notification);
            }
        }

    }

}
