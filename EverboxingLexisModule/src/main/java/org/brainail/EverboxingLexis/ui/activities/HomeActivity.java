package org.brainail.EverboxingLexis.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSection;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSectionsOnSceneInitializer;

import itkach.aard2.Application;
import itkach.aard2.ui.fragments.LexisBookmarksFragment;
import itkach.aard2.ui.fragments.LexisHistoryFragment;

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
public class HomeActivity extends SectionedDrawerActivity {

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
        return R.id.drawer_layout_root;
    }

    @Override
    protected Integer getDrawerActionsLayoutResourceId() {
        return R.id.drawer_menu_primary;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerCanHandleMenuItem(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public DrawerSectionsOnSceneInitializer.IDrawerSectionInitializer sectionInitializer() {
        return DrawerSectionsOnSceneInitializer.HOME;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        updateDrawerNotifications ();
    }

    // Some ugly solution to update bookmarks size and other stuff ...
    // It should be reimplemented, anyway.
    public void updateDrawerNotifications() {
        // Bookmarks
        final DrawerSection bookmarksSection = section (new LexisBookmarksFragment ());
        if (null != bookmarksSection) {
            final int bookmarksSize = ((Application) getApplication ()).bookmarksSize ();
            if (bookmarksSize != bookmarksSection.getNumberNotifications()) {
                bookmarksSection.withNotifications (bookmarksSize);
            }
        }

        // History
        final DrawerSection historySection = section (new LexisHistoryFragment());
        if (null != historySection) {
            final int historySize = ((Application) getApplication ()).historySize();
            if (historySize != historySection.getNumberNotifications()) {
                historySection.withNotifications (historySize);
            }
        }
    }

}
