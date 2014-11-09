package org.brainail.Everboxing.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.ToolCollections;
import org.brainail.Everboxing.utils.ToolUI;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * &copy; 2014 brainail <br/><br/>
 *
 * This program is free software: you can redistribute it and/or modify <br/>
 * it under the terms of the GNU General Public License as published by <br/>
 * the Free Software Foundation, either version 3 of the License, or <br/>
 * (at your option) any later version. <br/><br/>
 *
 * This program is distributed in the hope that it will be useful, <br/>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of <br/>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the <br/>
 * GNU General Public License for more details. <br/>
 *
 * You should have received a copy of the GNU General Public License <br/>
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class BaseActivity extends ActionBarActivity {

    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    protected View mDrawerActions;
    protected Toolbar mPrimaryToolbar;
    protected boolean isDrawerPresented = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init main stuff
        initContent();
        initToolbar();
        isDrawerPresented = initMenuDrawer();
    }

    private boolean initMenuDrawer() {
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
        if (ToolCollections.isAnyNull(mDrawerLayout, mDrawerActions, mPrimaryToolbar)) {
            return false;
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mPrimaryToolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }

        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        return true;
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

    protected Integer getPrimaryToolbarLayoutResourceId() {
        return null;
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
        if (isDrawerPresented) mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isDrawerPresented) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerPresented && ToolUI.toggleMenuDrawer(mDrawerLayout, false)) return;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isDrawerPresented) ToolUI.toggleMenuDrawer(mDrawerLayout, true);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
