package org.brainail.EverboxingLexis.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.brainail.EverboxingLexis.oauth.api.UserInfoApi;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSection;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSectionsControllerFactory;
import org.brainail.EverboxingLexis.ui.drawer.IDrawerSectionsController;
import org.brainail.EverboxingLexis.utils.tool.ToolFragments;
import org.brainail.EverboxingLexis.utils.tool.ToolToolbar;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSectionsOnSceneInitializer;

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
public abstract class SectionedDrawerActivity
        extends BaseDrawerActivity
        implements UserInfoApi.AuthCallback {

    private IDrawerSectionsController mDrawerSectionsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate section controller for drawer
        mDrawerSectionsController = DrawerSectionsControllerFactory.create(this);
        // Initialize drawer's sections
        sectionInitializer().initialize(this);
        // The first part: investigate by fragment manager due to AppCompatActivity creating
        mDrawerSectionsController.investigateFragmentsStack();
        // The second part: try to restore section by saved instance
        mDrawerSectionsController.restoreState(savedInstanceState);
        // Restore some data for toolbar
        updateToolbarColor();
        updateToolbarTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mDrawerSectionsController.saveState(state);
    }

    public void addDrawerSection(final DrawerSection section) {
        mDrawerSectionsController.addSection(section);
    }

    public void addDrawerDivider() {
        mDrawerSectionsController.addDivider();
    }

    public void addDrawerSubheader(final String title) {
        mDrawerSectionsController.addSubheader(title);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        mDrawerSectionsController.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        mDrawerSectionsController.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        mDrawerSectionsController.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        mDrawerSectionsController.onDrawerStateChanged(newState);
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();

        // Investigate section selection state by fragment manager (check the top)
        // due to back stack has changed
        mDrawerSectionsController.investigateFragmentsStack();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // Investigate section selection state by fragment manager (check the top)
        // due to fragments resuming
        mDrawerSectionsController.investigateFragmentsStack();
    }

    @Override
    protected void updateToolbarColor() {
        final DrawerSection section = mDrawerSectionsController.section(ToolFragments.topFragment(this));

        if (null == section) {
            super.updateToolbarColor();
        } else {
            ToolToolbar.updateToolbarColor(this, section.hasColor() ? section.getColor() : null);
        }
    }

    @Override
    protected void updateToolbarTitle() {
        final DrawerSection section = mDrawerSectionsController.section(ToolFragments.topFragment(this));

        if (null == section) {
            super.updateToolbarTitle();
        } else {
            ToolToolbar.updateToolbarTitle(this, section.getTitle());
        }
    }

    protected final DrawerSection section(final Fragment fragment) {
        return mDrawerSectionsController.section(fragment);
    }

    @Override
    public void onAuthSucceeded (UserInfoApi userInfo) {
        mDrawerSectionsController.onAuthSucceeded (userInfo);
    }

    @Override
    public void onUnauthSucceeded () {
        mDrawerSectionsController.onUnauthSucceeded ();
    }

    public abstract DrawerSectionsOnSceneInitializer.IDrawerSectionInitializer sectionInitializer();

}
