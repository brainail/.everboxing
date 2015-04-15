package org.brainail.Everboxing.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.notice.NoticeBar;
import org.brainail.Everboxing.ui.notice.NoticeController;
import org.brainail.Everboxing.utils.manager.ThemeManager;
import org.brainail.Everboxing.utils.tool.ToolFonts;
import org.brainail.Everboxing.utils.tool.ToolFragments;
import org.brainail.Everboxing.utils.tool.ToolToolbar;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.app.FragmentManager.OnBackStackChangedListener;
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
public abstract class BaseActivity
        extends ActionBarActivity
        implements OnBackStackChangedListener, NoticeBar.OnActionCallback, NoticeBar.OnVisibilityCallback {

    // Primary Toolbar
    private Toolbar mPrimaryToolbar;

    // Theme. I use null to define that this is full recreating
    private AppTheme mTheme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Window background causes an useless overdraw.
        // Nullifying the background removes that overdraw.
        getWindow().setBackgroundDrawable(null);

        // Monitor fragments
        getFragmentManager().addOnBackStackChangedListener(this);

        // Init & check theme
        mTheme = ThemeManager.checkOnCreate(this, mTheme);

        // Create notice controller
        NoticeController.from(this).onCreate(savedInstanceState);

        // Init content view
        initContent();

        // Init toolbar aka action bar
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
            mPrimaryToolbar = ButterKnife.findById(this, resourceId);
        }

        if (null != mPrimaryToolbar) {
            setSupportActionBar(mPrimaryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void attachBaseContext(final Context baseContext) {
        // Attach the Calligraphy
        super.attachBaseContext(CalligraphyContextWrapper.wrap(baseContext));
    }

    protected String getDefaultFont() {
        return ToolFonts.RobotoFonts.ASSETS_REGULAR;
    }

    protected Integer getLayoutResourceId() {
        return null;
    }

    public Toolbar getPrimaryToolbar() {
        return mPrimaryToolbar;
    }

    protected Integer getPrimaryToolbarLayoutResourceId() {
        return null;
    }

    public final ActionBarActivity self() {
        return this;
    }

    @Override
    protected void onStart() {
        NoticeController.from(this).showScene();
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        NoticeController.from(this).onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        NoticeController.from(this).hideScene();
        super.onStop();
    }

    @Override
    protected void onResume() {
        ThemeManager.checkOnResume(this, mTheme);
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        if (!ToolFragments.navigateBack(this)) {
            super.onBackPressed();
        }
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

    @Override
    public void onBackStackChanged() {
        // Restore some data for toolbar
        updateToolbarColor();
        updateToolbarTitle();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // Restore some data for toolbar
        updateToolbarColor();
        updateToolbarTitle();
    }

    protected void updateToolbarColor() {
        ToolToolbar.updateToolbarColor(this, null);
    }

    protected void updateToolbarTitle() {
        ToolToolbar.updateToolbarTitle(this, null);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // We don't want to monitor fragments after
        getFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onShow(final String token, final int activeSize) {
        // Check token to define your future actions
    }

    @Override
    public void onMute(final String token, final int activeSize) {
        // Check token to define your future actions
    }

    @Override
    public void onAction(final String token) {
        // Check token to define your future actions
    }

}
