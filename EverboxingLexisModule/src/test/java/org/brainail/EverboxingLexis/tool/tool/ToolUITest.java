package org.brainail.EverboxingLexis.tool.tool;

import android.annotation.SuppressLint;
import android.support.v4.widget.DrawerLayout;

import org.brainail.EverboxingLexis.utils.tool.ToolUI;
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
