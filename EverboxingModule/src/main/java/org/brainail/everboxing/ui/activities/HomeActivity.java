package org.brainail.Everboxing.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import org.brainail.Everboxing.R;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public class HomeActivity extends BaseActivity {

    // private MenuDrawer mMenuDrawer;
    DrawerArrowDrawable mDrawerArrow;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    View mDrawerActions;

    // http://www.google.com/design/spec/style/color.html#color-ui-color-palette
    // // Read: http://antonioleiva.com/material-design-everywhere/
    // http://stackoverflow.com/questions/26434504/how-to-implement-drawerarrowtoggle-from-android-appcompat-v7-21-library/26447144#26447144
    // http://stackoverflow.com/questions/26440879/how-do-i-use-drawerlayout-to-display-over-the-actionbar-toolbar-and-under-the-st
    // http://stackoverflow.com/questions/26430974/drawer-indicator-in-lollipop-play-store
    // // http://stackoverflow.com/questions/26476837/android-5-0-material-design-style-navigation-drawer-for-kitkat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initActionbar();
        initMenuDrawer2();
    }

//    private void initMenuDrawer() {
//        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.START);
//        mMenuDrawer.setMenuSize(getResources().getDimensionPixelSize(R.dimen.menu_drawer_home_width));
//        mMenuDrawer.setMenuView(R.layout.menu_drawer_home);
//        mMenuDrawer.setContentView(R.layout.activity_home);
//        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
//        mMenuDrawer.setDrawerIndicatorEnabled(true);
//        // mMenuDrawer.peekDrawer(1000, 0);
//    }

    private void initActionbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initMenuDrawer2() {
        // mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.START);
        // mMenuDrawer.setMenuSize(getResources().getDimensionPixelSize(R.dimen.menu_drawer_home_width));
        // mMenuDrawer.setMenuView(R.layout.menu_drawer_home);
        // mMenuDrawer.setContentView(R.layout.activity_home);
        // mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        // mMenuDrawer.setDrawerIndicatorEnabled(true);
        // mMenuDrawer.peekDrawer(1000, 0);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        mDrawerActions = findViewById(R.id.nav_drawer);

        mDrawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mDrawerArrow,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

        };

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
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(mDrawerActions);
                } else {
                    mDrawerLayout.openDrawer(mDrawerActions);
                }
                // mMenuDrawer.toggleMenu();
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
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

}
