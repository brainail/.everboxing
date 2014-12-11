package org.brainail.Everboxing.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kenny.snackbar.SnackBar;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.manager.ThemeManager;

import static org.brainail.Everboxing.utils.manager.ThemeManager.AppTheme;

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
public class BaseActivity extends ActionBarActivity {

    // Primary Toolbar
    private Toolbar mPrimaryToolbar;

    // Theme. I use null to define that this is full recreating
    private AppTheme mTheme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTheme = ThemeManager.checkOnCreate(this, mTheme);

        // Init main stuff
        initContent();
        initToolbar();
    }

    private void initContent() {
        final Integer resourceId = getLayoutResourceId();
        if (null != resourceId) {
            setContentView(resourceId);
        }
    }

    private void initToolbar() {
        final Integer resourceId = getPrimaryToolbarLayoutResourceId();
        if (null != resourceId) {
            mPrimaryToolbar = (Toolbar) findViewById(resourceId);
        }

        if (null != mPrimaryToolbar) {
            setSupportActionBar(mPrimaryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    protected Integer getLayoutResourceId() {
        return null;
    }

    protected Toolbar getPrimaryToolbar() {
        return mPrimaryToolbar;
    }

    protected Integer getPrimaryToolbarLayoutResourceId() {
        return null;
    }

    @Override
    protected void onStop() {
        SnackBar.cancelSnackBars(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        ThemeManager.checkOnResume(this, mTheme);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
