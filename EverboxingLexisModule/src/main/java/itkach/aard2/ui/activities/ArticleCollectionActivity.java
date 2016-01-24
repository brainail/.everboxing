package itkach.aard2.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.ui.activities.HomeActivity;
import org.brainail.EverboxingLexis.utils.gestures.OnGestureListener;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import itkach.aard2.Application;
import itkach.aard2.ui.adapters.ArticleCollectionPagerAdapter;
import itkach.aard2.ui.fragments.ArticleFragment;
import itkach.slob.Slob;

public class ArticleCollectionActivity extends BaseActivity {

    public static enum CreatorMode {
        INTENT, URI, BOOKMARKS_SCREEN, HISTORY_SCREEN, LAST_RESULT, RANDOM
    }

    public static final class ShowMode {
        public static final String RANDOM = "showRandom";
        public static final String BOOKMARKS = "showBookmarks";
        public static final String HISTORY = "showHistory";
    }

    public static interface BlobConverter {
        public Slob.Blob convert (Object item);
    }

    private boolean mIsDestroyed = false;
    public ArticleCollectionPagerAdapter mPagerAdapter;
    public ViewPager mViewPager;
    public TabLayout mTabLayout;

    @Override
    protected Integer getLayoutResourceId () {
        return R.layout.activity_article_collection;
    }

    @Override
    protected Integer getPrimaryToolbarLayoutResourceId () {
        return R.id.toolbar_primary;
    }

    @Override
    protected void onResumeFragments () {
        super.onResumeFragments ();

        // Restore title
        if (null != mViewPager && mViewPager.getCurrentItem () >= 0) {
            updateTitle (mViewPager.getCurrentItem ());
        }
    }

    @SuppressLint ("MissingSuperCall")
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        Application.app ().push (this);
        new ArticlesAdapterCreateTask (getIntent (), this).execute ();
    }

    public void unbookmarkCurrentTab () {
        if (CreatorMode.BOOKMARKS_SCREEN == mPagerAdapter.creatorMode () && mTabLayout.getTabCount () > 1) {
            mTabLayout.removeTabAt (mTabLayout.getSelectedTabPosition ());
        }
    }

    public void updateTabsState (final boolean isInitialization) {
        final int LIMIT = 30;
        final int lastVisiblePage = Math.min (mViewPager.getCurrentItem () + LIMIT, mPagerAdapter.getCount ());

        for (int tabIndex = mTabLayout.getTabCount (); tabIndex < lastVisiblePage; ++ tabIndex) {
            final TabLayout.Tab tab = mTabLayout.newTab ().setText (mPagerAdapter.getPageTitle (tabIndex));
            mTabLayout.addTab (tab, false);
            assignTabGestureListener (mTabLayout.getTabCount () - 1);
        }

        while (mTabLayout.getTabCount () > mPagerAdapter.getCount ()) {
            mTabLayout.removeTabAt (mTabLayout.getTabCount () - 1);
        }

        final TabLayout.Tab selectedTab = mTabLayout.getTabAt (mViewPager.getCurrentItem ());
        if (null != selectedTab) {
            if (isInitialization) {
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run () {
                        selectedTab.select ();
                    }
                }, 500);
            } else {
                selectedTab.select ();
            }
        }
    }

    public void updateTitle (int position) {
        final Slob.Blob blob = mPagerAdapter.get (position);
        final CharSequence pageTitle = mPagerAdapter.getPageTitle (position);

        final ActionBar actionBar = getSupportActionBar ();
        if (blob != null) {
            final String dictLabel = blob.owner.getTags ().get ("label");
            if (null != actionBar) {
                actionBar.setTitle (dictLabel);
            }

            Application.app ().history.add (Application.app ().getUrl (blob));
        } else {
            if (null != actionBar) {
                actionBar.setTitle (ToolResources.string (R.string.wtf_emo));
            }
        }

        if (null != actionBar) {
            actionBar.setSubtitle (pageTitle);
        }
    }

    public void injectAdapter (final ArticleCollectionPagerAdapter adapter) {
        mPagerAdapter = adapter;
    }

    public boolean assignTabGestureListener (final int tabIndex) {
        final View tabView = ToolUI.tabView (mTabLayout, tabIndex);

        if (null == tabView) {
            // No tab, or something wrong or internal impl has changed ...
            return false;
        }

        final boolean shouldScrollToTop
                = SettingsManager.getInstance ().retrieveShouldScrollToTopWhenDoubleTapOnTab ();
        final boolean shouldScrollToBottom
                = SettingsManager.getInstance ().retrieveShouldScrollToBottomWhenLongTapOnTab ();

        if (! shouldScrollToBottom && ! shouldScrollToTop) {
            // Don't scroll in any case
            return false;
        }

        tabView.setOnTouchListener (new OnGestureListener (self (), tabIndex) {
            @Override
            public void onDoubleTap (MotionEvent event, Object tag) {
                if (shouldScrollToTop && ((Integer) mViewPager.getCurrentItem ()).equals (tag)) {
                    ((ArticleFragment) mPagerAdapter.currentPage ()).scrollToTop ();
                }

                super.onDoubleTap (event, tag);
            }

            @Override
            public void onLongPress (MotionEvent event, Object tag) {
                if (shouldScrollToBottom && ((Integer) mViewPager.getCurrentItem ()).equals (tag)) {
                    ((ArticleFragment) mPagerAdapter.currentPage ()).scrollToBottom ();
                }

                super.onLongPress (event, tag);
            }
        });

        return true;
    }

    public void assignTabLayoutListener () {
        mTabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected (TabLayout.Tab tab) {
                mViewPager.setCurrentItem (tab.getPosition ());
            }

            @Override
            public void onTabUnselected (TabLayout.Tab tab) {}

            @Override
            public void onTabReselected (TabLayout.Tab tab) {}
        });
    }

    public void assignViewPagerListener () {
        mViewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
            @Override
            public void onPageScrollStateChanged (int arg0) {}

            @Override
            public void onPageScrolled (int arg0, float arg1, int arg2) {}

            @Override
            public void onPageSelected (final int position) {
                updateTabsState (false);
                updateTitle (position);
                runOnUiThread (new Runnable () {
                    @Override
                    public void run () {
                        final ArticleFragment fragment = (ArticleFragment) mPagerAdapter.getItem (position);
                        fragment.applyTextZoomPref ();
                    }
                });
            }
        });
    }

    public void observePagerAdapterDataSet () {
        mPagerAdapter.registerDataSetObserver (new DataSetObserver () {
            @Override
            public void onChanged () {
                if (mPagerAdapter.getCount () == 0) {
                    finish ();
                    return;
                }

                updateTabsState (false);
            }
        });
    }

    public void findViewPager () {
        mViewPager = (ViewPager) findViewById (R.id.articles_pager);
        mViewPager.setAdapter (mPagerAdapter);
    }

    public void findTabLayout () {
        mTabLayout = (TabLayout) findViewById (R.id.articles_tabs);
    }

    public void positionViewPagerCurrentItem (final int position) {
        mViewPager.setCurrentItem (position);
    }

    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    @SuppressLint ("MissingSuperCall")
    @Override
    protected void onDestroy () {
        mIsDestroyed = true;

        if (mViewPager != null) {
            mViewPager.setAdapter (null);
        }

        if (mPagerAdapter != null) {
            mPagerAdapter.destroy ();
        }

        Application.app ().pop (this);
        super.onDestroy ();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                Intent upIntent = new Intent (this, HomeActivity.class);
                if (NavUtils.shouldUpRecreateTask (this, upIntent)) {
                    TaskStackBuilder.create (this).addNextIntent (upIntent).startActivities ();
                    finish ();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo (this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    protected void onStart () {
        super.onStart ();
        if (null != mPagerAdapter) {
            if (CreatorMode.BOOKMARKS_SCREEN == mPagerAdapter.creatorMode ()
                    || CreatorMode.HISTORY_SCREEN == mPagerAdapter.creatorMode ()) {
                if (null != mTabLayout) {
                    mTabLayout.removeAllTabs ();
                    updateTabsState (true);
                }
            }
        }
    }

    @Override
    public void onBackPressed () {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage && articlePage.onBackPressed ()) {
                return;
            }
        }

        super.onBackPressed ();
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage && articlePage.onKeyUp (keyCode, event)) {
                return true;
            }
        }

        return super.onKeyUp (keyCode, event);
    }

    @Override
    public void onActionModeFinished (ActionMode mode) {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage && articlePage.onActionModeFinished (mode)) {
                // ...
            }
        }

        super.onActionModeFinished (mode);
    }

    @Override
    public void onActionModeStarted (ActionMode mode) {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage && articlePage.onActionModeStarted (mode)) {
                // ...
            }
        }

        super.onActionModeStarted (mode);
    }

}
