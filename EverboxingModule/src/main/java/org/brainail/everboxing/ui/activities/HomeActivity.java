package org.brainail.Everboxing.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.brainail.Everboxing.R;

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
public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected Integer getPrimaryToolbarLayoutResourceId() {
        return R.id.toolbar_primary;
    }

    @Override
    protected Integer getDrawerLayoutResourceId() {
        return R.id.home_drawer_layout;
    }

    @Override
    protected Integer getDrawerActionsLayoutResourceId() {
        return R.id.menu_drawer_primary;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isDrawerPresented && mDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

}
