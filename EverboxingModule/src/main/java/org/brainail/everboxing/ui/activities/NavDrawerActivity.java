package org.brainail.Everboxing.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.fragments.NavDrawerFragment;
import org.brainail.Everboxing.ui.fragments.PlaceholderFragment;

public class NavDrawerActivity extends ActionBarActivity implements NavDrawerFragment.NavigationDrawerCallbacks {

    private NavDrawerFragment mNavDrawerFragment;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mLastScreenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        initializeComponents();
    }

    public void initializeComponents() {
        mLastScreenTitle = getTitle();
        mNavDrawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment placeholderFragment = PlaceholderFragment.newInstance(position + 1);
        fragmentManager.beginTransaction().replace(R.id.container, placeholderFragment).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {

            case 1:
                mLastScreenTitle = getString(R.string.title_section1);
                break;

            case 2:
                mLastScreenTitle = getString(R.string.title_section2);
                break;

            case 3:
                mLastScreenTitle = getString(R.string.title_section3);
                break;

        }
    }

    public void restoreActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mLastScreenTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_ever_boxing, menu);
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_settings == item.getItemId()) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
