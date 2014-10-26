package org.brainail.Everboxing.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.ToolUI;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public class HomeActivity extends BaseActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mDrawerActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMenuDrawer();
    }

    @Override
    protected Integer getLayoutResourceId() {
        return R.layout.activity_home;
    }

    private void initMenuDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        mDrawerActions = findViewById(R.id.menu_drawer_primary);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, getPrimaryToolbar(), R.string.drawer_open, R.string.drawer_close) {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                ToolUI.toggleMenuDrawer(mDrawerLayout, true);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (ToolUI.toggleMenuDrawer(mDrawerLayout, false)) return;
        super.onBackPressed();
    }

}
