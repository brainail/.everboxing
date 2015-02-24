package org.brainail.Everboxing.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolCollections;
import org.brainail.Everboxing.utils.tool.ToolUI;

import static android.support.v4.widget.DrawerLayout.DrawerListener;

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
public abstract class BaseDrawerActivity extends BaseActivity implements DrawerListener {

    // Drawer
    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    protected View mDrawerActions;
    protected boolean mIsDrawerPresented = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init main stuff
        mIsDrawerPresented = initMenuDrawer();
    }

    protected boolean initMenuDrawer() {
        Integer resourceId;

        // Try to find drawer layout
        if (null != (resourceId = getDrawerLayoutResourceId())) {
            mDrawerLayout = (DrawerLayout) findViewById(resourceId);
        } else {
            return false;
        }

        // Try to find drawer's actions
        if (null != (resourceId = getDrawerActionsLayoutResourceId())) {
            mDrawerActions = findViewById(resourceId);
        } else {
            return false;
        }

        // Check that each component is presented
        if (ToolCollections.isAnyNull(mDrawerLayout, mDrawerActions, getPrimaryToolbar())) {
            return false;
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, getPrimaryToolbar(), R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();

                // Give others
                ((DrawerListener) self()).onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();

                // Give others
                ((DrawerListener) self()).onDrawerOpened(view);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // Give others
                ((DrawerListener) self()).onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

                // Give others
                ((DrawerListener) self()).onDrawerStateChanged(newState);
            }

        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        return true;
    }

    public final DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected Integer getDrawerLayoutResourceId() {
        return null;
    }

    protected Integer getDrawerActionsLayoutResourceId() {
        return null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mIsDrawerPresented) mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsDrawerPresented) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mIsDrawerPresented && ToolUI.toggleMenuDrawer(mDrawerLayout, false)) return;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsDrawerPresented) ToolUI.toggleMenuDrawer(mDrawerLayout, true);
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean drawerCanHandleMenuItem(final MenuItem item) {
        return mIsDrawerPresented && mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // Do nothing
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // Do nothing
    }

}
