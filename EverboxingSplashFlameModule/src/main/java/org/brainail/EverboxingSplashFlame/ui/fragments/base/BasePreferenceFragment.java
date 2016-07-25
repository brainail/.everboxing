package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingSplashFlame.BuildConfig;
import org.brainail.EverboxingSplashFlame.bus.GlobalEvents;
import org.brainail.EverboxingSplashFlame.di.ActivityContext;
import org.brainail.EverboxingSplashFlame.di.component.ActivityComponent;
import org.brainail.EverboxingSplashFlame.navigator.Navigator;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BaseFragmentDelegate;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolUI;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class BasePreferenceFragment
        extends PreferenceFragment
        implements BaseFragmentDelegate.BaseFragmentCallbacks {

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
    public void onAttach (Activity activity) {
        super.onAttach (activity);
        mBaseFragmentDelegate = new BaseFragmentDelegate (this);
        mBaseFragmentDelegate.onAttach (activity);
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
    public void onStart () {
        super.onStart ();

        mGlobalBus.register (this);
    }

    @Override
    public void onStop () {
        super.onStop ();

        mGlobalBus.unregister (this);
    }

    @Override
    public void onDestroy () {
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
        if (! isConnected && BuildConfig.DEBUG) {
            ToolUI.showToast (getActivity (), "No internet connection ...");
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Preference> T findPreference(final @StringRes int key) {
        return (T) findPreference(getString(key));
    }

}
