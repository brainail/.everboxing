package net.brainail.everboxing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.brainail.everboxing.windows.fragments.NavigationDrawerFragment;
import net.brainail.everboxing.windows.fragments.PlaceholderFragment;

public class MainEverBoxingActivity
        extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mLastScreenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_everboxing);
        initializeComponents();
    }

    public void initializeComponents() {
        mNavigationDrawerFragment = getNavigationDrawerFragment();
        mLastScreenTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // Update the main content by replacing fragments
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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        if (R.id.action_settings == itemId) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
