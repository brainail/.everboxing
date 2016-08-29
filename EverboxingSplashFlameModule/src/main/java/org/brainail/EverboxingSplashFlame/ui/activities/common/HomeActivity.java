package org.brainail.EverboxingSplashFlame.ui.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.OnDialogListActionCallback;
import org.brainail.EverboxingSplashFlame.BuildConfig;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.api.UserInfoApi;
import org.brainail.EverboxingSplashFlame.api.google.PlayServices;
import org.brainail.EverboxingSplashFlame.ui.activities.base.SectionedDrawerActivity;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSection;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSectionCallback;
import org.brainail.EverboxingSplashFlame._app.drawer.DrawerSectionsOnSceneInitializer;
import org.brainail.EverboxingSplashFlame.ui.drawer.IDrawerSectionInitializer;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BaseFragment;
import org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs;
import org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs.AppHardyDialogsCode;
import org.brainail.EverboxingSplashFlame.utils.LogScope;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;

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
public class HomeActivity
        extends SectionedDrawerActivity
        implements DrawerSectionCallback, OnDialogListActionCallback {

    private boolean mShouldUpdateDrawerNotifications = true;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // ...

        handleIntent (getIntent (), false);
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent (intent);

        // ...

        handleIntent (intent, true);
    }

    @Override
    public void onStart () {
        super.onStart ();

        // ...
    }

    @Override
    protected void onStop () {
        super.onStop ();

        // ...
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();

        // ...
    }

    @Override
    protected Integer getLayoutResourceId () {
        return R.layout.activity_home;
    }

    @Override
    protected Integer getPrimaryToolbarLayoutResourceId () {
        return R.id.toolbar_primary;
    }

    @Override
    protected Integer getDrawerLayoutResourceId () {
        return R.id.drawer_layout_root;
    }

    @Override
    protected Integer getDrawerActionsLayoutResourceId () {
        return R.id.drawer_menu_primary;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (drawerCanHandleMenuItem (item)) return true;
        return super.onOptionsItemSelected (item);
    }

    @Override
    public IDrawerSectionInitializer sectionInitializer () {
        return DrawerSectionsOnSceneInitializer.HOME;
    }

    @Override
    protected void onResume () {
        super.onResume ();

        // For the first time get info about user from settings
        updateUserInfo (() -> {
            final UserInfoApi userInfo = PlayServices.formSettingsUserInfo ();
            return userInfo.email;
        });
    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments ();
        updateDrawerNotifications ();
    }

    @Override
    public void onDrawerOpened (View drawerView) {
        super.onDrawerOpened (drawerView);

        // ...
    }

    @Override
    public void onDrawerClosed (View drawerView) {
        super.onDrawerClosed (drawerView);
        mShouldUpdateDrawerNotifications = true;
    }

    @Override
    public void onDrawerSlide (View drawerView, float slideOffset) {
        super.onDrawerSlide (drawerView, slideOffset);
        if (slideOffset > 0 && mShouldUpdateDrawerNotifications) {
            mShouldUpdateDrawerNotifications = false;
            updateDrawerNotifications ();
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
    }

    private void handleIntent (final Intent intent, final boolean isNew) {
        setIntent (intent);

        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (! isTaskRoot ()) {
            if (intent.hasCategory (Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals (intent.getAction ())) {
                PooLogger.warn (LogScope.WTF, "Home scene is not the root. Finishing .. instead of launching.");
                finish ();
                return;
            }
        }

        if (SettingsManager.getInstance ().retrieveAppShouldIntroduce (false)) {
            AppIntro.introduce (this);
        }
    }

    // Some ugly solution to update bookmarks size and other stuff ...
    public void updateDrawerNotifications () {
        PooLogger.verb (LogScope.DRAWER, "Update drawer notifications numbers ...");

        // ...
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        final Fragment target = ToolFragments.topFragment (self ());

        if (target instanceof BaseFragment) {
            if (((BaseFragment) target).onKeyUp (keyCode, event)) {
                return true;
            }
        }

        return super.onKeyUp (keyCode, event);
    }

    @Override
    public void onClick (DrawerSection section) {
        // ...
    }

    @Override
    public void onTargetClick (DrawerSection section) {
         if (DrawerSectionsOnSceneInitializer.SectionTag.FEEDBACK_RATING.equals (section.tag ())) {
             investigateFragmentsStack ();
             AppHardyDialogs.helpUs ().setCallbacks (this).show (this);
         }
    }

    @Override
    public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String item, String itemTag) {
        if (dialog.isDialogWithCode (AppHardyDialogsCode.D_HELP_US)) {
            switch (whichItem) {
                case 0:
                    mNavigator.openAppInMarketAction (BuildConfig.APPLICATION_ID).start ();
                    break;
                case 1:
                    mNavigator.shareApp (BuildConfig.APPLICATION_ID).start ();
                    break;
                case 2:
                    mNavigator.sendFeedbackOrSuggestion ().start ();
                    break;
            }
        }
    }

}
