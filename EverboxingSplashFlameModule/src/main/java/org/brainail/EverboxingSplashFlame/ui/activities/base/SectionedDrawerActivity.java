package org.brainail.EverboxingSplashFlame.ui.activities.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.brainail.EverboxingSplashFlame.api.UserInfoApi;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSection;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSectionsControllerFactory;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSectionsOnSceneInitializer;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerUser;
import org.brainail.EverboxingSplashFlame.ui.drawer.IDrawerSectionsController;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
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
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // Instantiate section controller for drawer
        mDrawerSectionsController = DrawerSectionsControllerFactory.create (this);
        // Initialize drawer's sections
        sectionInitializer ().initialize (this);
        // The first part: investigate by fragment manager due to AppCompatActivity creating
        mDrawerSectionsController.investigateFragmentsStack ();
        // The second part: try to restore section by saved instance
        mDrawerSectionsController.restoreState (savedInstanceState);
        // Restore some data for toolbar
        updateToolbarColor ();
        updateToolbarTitle ();
    }

    @Override
    protected void onSaveInstanceState (Bundle state) {
        super.onSaveInstanceState (state);
        mDrawerSectionsController.saveState (state);
    }

    public void addDrawerSection (final DrawerSection section) {
        mDrawerSectionsController.addSection (section);
    }

    public void addDrawerDivider () {
        mDrawerSectionsController.addDivider ();
    }

    public void addDrawerSubheader (final String title) {
        mDrawerSectionsController.addSubheader (title);
    }

    @Override
    public void onDrawerSlide (View drawerView, float slideOffset) {
        super.onDrawerSlide (drawerView, slideOffset);
        mDrawerSectionsController.onDrawerSlide (drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened (View drawerView) {
        super.onDrawerOpened (drawerView);
        mDrawerSectionsController.onDrawerOpened (drawerView);
    }

    @Override
    public void onDrawerClosed (View drawerView) {
        super.onDrawerClosed (drawerView);
        mDrawerSectionsController.onDrawerClosed (drawerView);
    }

    @Override
    public void onDrawerStateChanged (int newState) {
        super.onDrawerStateChanged (newState);
        mDrawerSectionsController.onDrawerStateChanged (newState);
    }

    @Override
    public void onBackStackChanged () {
        super.onBackStackChanged ();

        // Investigate section selection state by fragment manager (check the top)
        // due to back stack has changed
        mDrawerSectionsController.investigateFragmentsStack ();
    }

    protected final void updateUserInfo (final DrawerUser.UserProvider userProvider) {
        // For the first time get info about user from settings
        mDrawerSectionsController.updateUserInfo (userProvider);
    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments ();

        // Investigate section selection state by fragment manager (check the top)
        // due to fragments resuming
        mDrawerSectionsController.investigateFragmentsStack ();
    }

    @Override
    protected void updateToolbarColor () {
        final DrawerSection section = mDrawerSectionsController.section (ToolFragments.topFragment (this));

        if (null == section) {
            super.updateToolbarColor ();
        } else {
            mToolbarTuner.updateToolbarColor (section.hasDestinationColor () ? section.getDestinationColor () : null);
        }
    }

    @Override
    protected void updateToolbarTitle () {
        final DrawerSection section = mDrawerSectionsController.section (ToolFragments.topFragment (this));

        if (null == section) {
            super.updateToolbarTitle ();
        } else {
            mToolbarTuner.updateToolbarTitle (section.getTitle ());
        }
    }

    protected final DrawerSection section (final Fragment fragment) {
        return mDrawerSectionsController.section (fragment);
    }

    protected final DrawerSection section (final ToolFragments.FragmentCreator fragment) {
        return mDrawerSectionsController.section (fragment);
    }

    @Override
    public void onAuthSucceeded (final UserInfoApi userInfo) {
        mDrawerSectionsController.onAuthSucceeded (new DrawerUser.UserProvider () {
            @Override
            public String provideEmail () {
                return userInfo.email;
            }
        });
    }

    @Override
    public void onUnauthSucceeded () {
        mDrawerSectionsController.onUnauthSucceeded ();
    }

    public final void investigateFragmentsStack () {
        mDrawerSectionsController.investigateFragmentsStack ();
    }

    public abstract DrawerSectionsOnSceneInitializer.IDrawerSectionInitializer sectionInitializer ();

}
