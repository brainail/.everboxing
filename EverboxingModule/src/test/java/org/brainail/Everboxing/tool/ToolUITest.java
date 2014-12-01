package org.brainail.Everboxing.tool;

import android.annotation.SuppressLint;
import android.support.v4.widget.DrawerLayout;

import org.brainail.Everboxing.utils.ToolUI;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
@Config(emulateSdk = 18, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
@SuppressLint("RtlHardcoded")
public class ToolUITest {

    @Test
    public void toggleMenuDrawerWhenOpenedTest() {
        // Prepare mock
        final DrawerLayout drawerLayout = mock(DrawerLayout.class);
        when(drawerLayout.isDrawerOpen(ToolUI.GRAVITY_START)).thenReturn(true);

        // Call method
        final boolean result = ToolUI.toggleMenuDrawer(drawerLayout, anyBoolean());

        // Verify some stuff
        verify(drawerLayout, times(1)).closeDrawer(ToolUI.GRAVITY_START);
        verify(drawerLayout, never()).openDrawer(ToolUI.GRAVITY_START);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void toggleMenuDrawerWhenClosedTest() {
        // Prepare mock
        final DrawerLayout drawerLayout = mock(DrawerLayout.class);
        when(drawerLayout.isDrawerOpen(ToolUI.GRAVITY_START)).thenReturn(false);

        // Call method
        final boolean result = ToolUI.toggleMenuDrawer(drawerLayout, true);

        // Verify some stuff
        verify(drawerLayout, never()).closeDrawer(ToolUI.GRAVITY_START);
        verify(drawerLayout, times(1)).openDrawer(ToolUI.GRAVITY_START);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void toggleMenuDrawerWhenClosedAndOneDirectionTest() {
        // Prepare mock
        final DrawerLayout drawerLayout = mock(DrawerLayout.class);
        when(drawerLayout.isDrawerOpen(ToolUI.GRAVITY_START)).thenReturn(false);

        // Call method
        final boolean result = ToolUI.toggleMenuDrawer(drawerLayout, false);

        // Verify some stuff
        verify(drawerLayout, never()).closeDrawer(ToolUI.GRAVITY_START);
        verify(drawerLayout, never()).openDrawer(ToolUI.GRAVITY_START);
        Assertions.assertThat(result).isFalse();
    }

}
