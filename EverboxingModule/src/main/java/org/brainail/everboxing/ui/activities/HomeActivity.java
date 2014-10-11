package org.brainail.Everboxing.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.ToolUI;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public class HomeActivity extends BaseActivity {

    private MenuDrawer mMenuDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenuDrawer();
    }

    private void initMenuDrawer() {
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.START);
        mMenuDrawer.setMenuSize(getResources().getDimensionPixelSize(R.dimen.menu_drawer_home_width));
        mMenuDrawer.setMenuView(R.layout.menu_drawer_home);
        mMenuDrawer.setContentView(R.layout.activity_home);
        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        mMenuDrawer.setDrawerIndicatorEnabled(true);
        mMenuDrawer.peekDrawer(1000, 0);
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
                mMenuDrawer.toggleMenu();
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
    public void onBackPressed() {
        if (ToolUI.isMenuDrawerOpened(mMenuDrawer.getDrawerState())) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }

}
