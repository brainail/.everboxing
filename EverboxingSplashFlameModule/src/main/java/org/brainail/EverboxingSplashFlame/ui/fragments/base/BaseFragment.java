package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingSplashFlame.BuildConfig;
import org.brainail.EverboxingSplashFlame.bus.GlobalEvents;
import org.brainail.EverboxingSplashFlame.di.ActivityContext;
import org.brainail.EverboxingSplashFlame.di.component.ActivityComponent;
import org.brainail.EverboxingSplashFlame.navigator.Navigator;
import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BaseFragmentDelegate.BaseFragmentCallbacks;
import org.brainail.EverboxingSplashFlame.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolUI;
import org.brainail.EverboxingTools.utils.callable.Tagable;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

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
public class BaseFragment
        extends Fragment
        implements ActionMode.Callback, Tagable, BaseFragmentCallbacks {

    // Chrome tabs stuff
    private CustomTabsSceneHelper mCustomTabsSceneHelper;

    @Inject
    protected EventBus mGlobalBus;
    @Inject
    @ActivityContext
    protected Navigator mNavigator;
    @Inject
    protected RefWatcher mRefWatcher;

    private Unbinder mUnbinder;
    private BaseFragmentDelegate mBaseFragmentDelegate;

    @Override
    public void onAttach (Context context) {
        super.onAttach (context);
        mBaseFragmentDelegate = new BaseFragmentDelegate (this);
        mBaseFragmentDelegate.onAttach (context);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        mBaseFragmentDelegate.onActivityCreated (savedInstanceState);
    }

    @Override
    public void setupComponent (ActivityComponent activityComponent) {}

    @Override
    public void injectMembers (ActivityComponent activityComponent) {
        activityComponent.inject (this);
    }

    protected final View bind (final View source) {
        mUnbinder = ButterKnife.bind (this, source);
        return source;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // Chrome tabs stuff
        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.onCreateScene (getActivity ());
    }

    @Override
    public void onStart () {
        super.onStart ();

        mGlobalBus.register (this);

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStartScene (getActivity ());
    }

    @Override
    public void onStop () {
        super.onStop ();

        mGlobalBus.unregister (this);

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStopScene (getActivity ());
    }

    @Override
    public void onDestroy () {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.onDestroyScene (getActivity ());

        super.onDestroy ();

        if (mRefWatcher != null) {
            mRefWatcher.watch (this);
        }
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();

        if (mUnbinder != null) {
            mUnbinder.unbind ();
        }
    }

    @Subscribe
    public void onConnectivityChanged (GlobalEvents.ConnectivityChanged event) {
        onConnectivityChanged (event.isConnected);
    }

    protected void onConnectivityChanged (boolean isConnected) {
        if (!isConnected && BuildConfig.DEBUG) {
            ToolUI.showToast (getActivity (), "No internet connection ...");
        }
    }

    protected final AppCompatActivity getAppCompatActivity () {
        return (AppCompatActivity) getActivity ();
    }

    public void openUrl (final String url) {
        openUrl (url, null);
    }

    public void openUrl (final String url, final String title) {
        openUrl (url, title, null);
    }

    public void openUrl (final String url, final String title, final String description) {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.openCustomTab (getActivity (), url, title, description);
    }

    public boolean onKeyUp (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                final Activity scene = getActivity ();
                if (scene instanceof BaseActivity) {
                    final Toolbar toolbar = ((BaseActivity) scene).getPrimaryToolbar ();

                    if (null != toolbar && toolbar.isOverflowMenuShowing ()) {
                        // toolbar.hideOverflowMenu();
                        toolbar.dismissPopupMenus ();
                        return true;
                    } else if (null != toolbar) {
                        toolbar.showOverflowMenu ();
                        return true;
                    }
                }

                break;
        }

        return false;
    }

    public boolean onKeyDown (int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyLongPress (int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onCreateActionMode (ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode (ActionMode mode) {}

    @Override
    public boolean onPrepareActionMode (ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked (ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public String tag () {
        return ToolFragments.getTag (getClass ());
    }

    protected final Fragment self () {
        return this;
    }

}
