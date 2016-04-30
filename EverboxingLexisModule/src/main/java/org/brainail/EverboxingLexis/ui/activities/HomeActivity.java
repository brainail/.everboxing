package org.brainail.EverboxingLexis.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSection;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSectionCallback;
import org.brainail.EverboxingLexis.ui.drawer.DrawerSectionsOnSceneInitializer;
import org.brainail.EverboxingLexis.ui.fragments.BaseFragment;
import org.brainail.EverboxingLexis.ui.fragments.BaseListFragment;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.fragments.LexisBookmarksFragment;
import itkach.aard2.ui.fragments.LexisDictionariesFragment;
import itkach.aard2.ui.fragments.LexisHistoryFragment;
import itkach.slob.Slob;

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
public class HomeActivity extends SectionedDrawerActivity implements DrawerSectionCallback {

    private boolean mShouldUpdateDrawerNotifications = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ...

        handleIntent (getIntent (), false);
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent (intent);

        // ...

        handleIntent (intent, true);
    }

    @Override
    public void onStart () {
        super.onStart ();

        // ...
    }

    @Override
    protected void onStop () {
        super.onStop ();

        // ...
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();

        // ...
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
        return super.onCreateOptionsMenu (menu);
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
        super.onResumeFragments ();
        updateDrawerNotifications ();
    }

    @Override
    public void onDrawerOpened (View drawerView) {
        super.onDrawerOpened (drawerView);

        final Fragment target = ToolFragments.topFragment (self ());
        if (target instanceof BaseListFragment) {
            ((BaseListFragment) target).finishCurrentActionMode ();
        }
    }

    @Override
    public void onDrawerClosed (View drawerView) {
        super.onDrawerClosed (drawerView);
        mShouldUpdateDrawerNotifications = true;
    }

    @Override
    public void onDrawerSlide (View drawerView, float slideOffset) {
        super.onDrawerSlide (drawerView, slideOffset);
        if (slideOffset > 0 && mShouldUpdateDrawerNotifications) {
            mShouldUpdateDrawerNotifications = false;
            updateDrawerNotifications ();
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
    }

    // Lexis stuff
    private static final ToolFragments.FragmentCreator LEXIS_BOOKMARKS_SECTION_TEMPLATE
            = ToolFragments.FragmentCreator.from (LexisBookmarksFragment.class);
    private static final ToolFragments.FragmentCreator LEXIS_HISTORY_SECTION_TEMPLATE
            = ToolFragments.FragmentCreator.from (LexisHistoryFragment.class);
    private static final ToolFragments.FragmentCreator LEXIS_DICTIONARIES_SECTION_TEMPLATE
            = ToolFragments.FragmentCreator.from (LexisDictionariesFragment.class);

    private void handleIntent (final Intent intent, final boolean isNew) {
        setIntent (intent);

        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (! isTaskRoot()) {
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Plogger.logW(Plogger.LogScope.WTF, "Home scene is not the root. Finishing .. instead of launching.");
                finish();
                return;
            }
        }

        if (SettingsManager.getInstance ().retrieveAppShouldIntroduce (false)) {
            AppIntro.introduce (this);
        }
    }

    // Some ugly solution to update bookmarks size and other stuff ...
    // It should be reimplemented, anyway.
    public void updateDrawerNotifications() {
        Plogger.logV (Plogger.LogScope.DRAWER, "Update drawer notifications numbers ...");

        // Filtered bookmarks
        final DrawerSection bookmarksSection = section (LEXIS_BOOKMARKS_SECTION_TEMPLATE);
        if (null != bookmarksSection) {
            final int bookmarksSize = ((Application) getApplication ()).bookmarksSize ();
            if (bookmarksSize != bookmarksSection.getNumberNotifications()) {
                bookmarksSection.withNotifications (bookmarksSize);
            }
        }

        // Filtered history
        final DrawerSection historySection = section (LEXIS_HISTORY_SECTION_TEMPLATE);
        if (null != historySection) {
            final int historySize = ((Application) getApplication ()).historySize ();
            if (historySize != historySection.getNumberNotifications()) {
                historySection.withNotifications (historySize);
            }
        }

        // Active dictionaries
        final DrawerSection dictionarySection = section (LEXIS_DICTIONARIES_SECTION_TEMPLATE);
        if (null != dictionarySection) {
            final int dictionariesSize = ((Application) getApplication ()).activeDictionariesSize ();
            if (dictionariesSize != dictionarySection.getNumberNotifications()) {
                dictionarySection.withNotifications (dictionariesSize);
            }
        }
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        final Fragment target = ToolFragments.topFragment (self ());

        if (target instanceof BaseListFragment) {
            if (((BaseListFragment) target).onKeyUp (keyCode, event)) {
                return true;
            }
        } else {
            if (target instanceof BaseFragment) {
                if (((BaseFragment) target).onKeyUp (keyCode, event)) {
                    return true;
                }
            }
        }

        return super.onKeyUp (keyCode, event);
    }

    @Override
    public void onClick (DrawerSection section) {}

    @Override
    public void onTargetClick (DrawerSection section) {
        if (DrawerSectionsOnSceneInitializer.LUCKY_SECTION_POSITION == section.getPosition ()) {
            final Slob.Blob randomBlob = Application.app ().random ();
            if (null != randomBlob) {
                final Intent intent = new Intent (this, ArticleCollectionActivity.class);
                intent.setData (Uri.parse (Application.app ().getUrl (randomBlob)));
                startActivity (intent);
            } else {
                ToolUI.showToast (this, R.string.article_collection_nothing_found);
                investigateFragmentsStack ();
            }
        }
    }

}
