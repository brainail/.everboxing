package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingSplashFlame.JApplication;
import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingSplashFlame.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingTools.utils.callable.Tagable;
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
public class BaseFragment extends Fragment implements ActionMode.Callback, Tagable {

    // Chrome tabs stuff
    private CustomTabsSceneHelper mCustomTabsSceneHelper;

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

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStartScene (getActivity ());
    }

    @Override
    public void onStop () {
        super.onStop ();

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStopScene (getActivity ());

        final RefWatcher refWatcher = JApplication.refWatcher (getActivity ());
        if (null != refWatcher) refWatcher.watch (this);
    }

    @Override
    public void onDestroy () {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.onDestroyScene (getActivity ());

        super.onDestroy ();
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

    protected final Fragment self() {
        return this;
    }

}
