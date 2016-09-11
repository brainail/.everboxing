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
import android.view.WindowManager;
import android.webkit.WebView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.ui.activities.HomeActivity;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;
import org.brainail.EverboxingTools.ui.views.RespectFullscreenInsetsFrameLayout;
import org.brainail.EverboxingTools.utils.Sdk;
import org.brainail.EverboxingTools.utils.gestures.OnGestureListener;
import org.brainail.EverboxingTools.utils.manager.ui.SystemUiHelper;

import itkach.aard2.Application;
import itkach.aard2.ui.adapters.ArticleCollectionPagerAdapter;
import itkach.aard2.ui.fragments.ArticleFragment;
import itkach.slob.Slob;

import static org.brainail.EverboxingTools.utils.manager.ui.SystemUiHelper.FLAG_IMMERSIVE_STICKY;
import static org.brainail.EverboxingTools.utils.manager.ui.SystemUiHelper.LEVEL_HIDE_STATUS_BAR;
import static org.brainail.EverboxingTools.utils.manager.ui.SystemUiHelper.LEVEL_IMMERSIVE;

public class ArticleCollectionActivity extends BaseActivity {

    public static enum CreatorMode {
        INTENT, URI, BOOKMARKS_SCREEN, HISTORY_SCREEN, LAST_RESULT, RANDOM
    }

    public static final class ShowMode {
        public static final String RANDOM = "showRandom";
        public static final String BOOKMARKS = "showBookmarks";
        public static final String HISTORY = "showHistory";
    }

    public static abstract class SavedStateArgs {
        public static final String IS_FULL_SCREEN = "saved.state.fullscreen";
    }

    public static interface BlobConverter {
        public Slob.Blob convert (Object item);
    }

    private boolean mIsDestroyed = false;
    public ArticleCollectionPagerAdapter mPagerAdapter;
    public ViewPager mViewPager;
    public TabLayout mTabLayout;

    public RespectFullscreenInsetsFrameLayout mContent;

    protected static volatile Boolean sIsFullscreenMode;
    protected SystemUiHelper mUiHelper;

    protected SystemUiHelper uiHelper () {
        if (null != mUiHelper) {
            return mUiHelper;
        }

        mUiHelper = new SystemUiHelper (
                this,
                Sdk.isSdkSupported (Sdk.KITKAT) ? LEVEL_IMMERSIVE : LEVEL_HIDE_STATUS_BAR,
                Sdk.isSdkSupported (Sdk.KITKAT) ? FLAG_IMMERSIVE_STICKY : 0,
                new SystemUiHelper.OnVisibilityChangeListener () {
                    @Override
                    public void onVisibilityChange (boolean visible) {
                        if (visible) {
                            fixFullscreenMode (1500);
                        }
                    }
                }
        );

        return mUiHelper;
    }

    protected void fixFullscreenMode (final int delayMillis) {
        if (sIsFullscreenMode) {
            mContent.respectFullscreenInsets (true);

            getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow ().addFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (delayMillis > 0) {
                uiHelper ().delayHide (delayMillis);
            } else {
                uiHelper ().hide ();
            }
        } else {
            mContent.respectFullscreenInsets (false);

            getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow ().addFlags (WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            uiHelper ().show ();
        }
    }

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

        mContent = bindView (R.id.app_content);

        // Tune fullscreen mode
        if (null != savedInstanceState && savedInstanceState.containsKey (SavedStateArgs.IS_FULL_SCREEN)) {
            updateFullscreenModeWithUi (savedInstanceState.getBoolean (SavedStateArgs.IS_FULL_SCREEN));
        } else {
            if (null == sIsFullscreenMode) {
                updateFullscreenModeWithUi (SettingsManager.getInstance ().retrieveShouldShowArticleInFullscreen ());
            }
        }

        // Add to stack
        Application.app ().push (this);

        // Create and start initialization for adapter
        new ArticlesAdapterCreateTask (getIntent (), this).execute ();
    }

    @Override
    protected void onResume () {
        super.onResume ();
        updateFullscreenModeWithUi (sIsFullscreenMode);
    }

    public static void updateFullscreenMode (final boolean isFullscreenMode) {
        sIsFullscreenMode = isFullscreenMode;
    }

    public void updateFullscreenModeWithUi (final boolean isFullscreenMode) {
        sIsFullscreenMode = isFullscreenMode;
        fixFullscreenMode (0);
    }

    public boolean isInFullscreen () {
        return sIsFullscreenMode;
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged (hasFocus);
        if (hasFocus) {
            fixFullscreenMode (1500);
        }
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

    private void stopCurrentTts (final int newPagePosition) {
        if (null != mPagerAdapter) {
            if (null != mViewPager && mViewPager.getCurrentItem () == newPagePosition) {
                // Don't need to stop if we resume to the same page
                return;
            }

            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage) {
                articlePage.finishTts (false);
            }
        }
    }

    public void assignTabLayoutListener () {
        mTabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected (TabLayout.Tab tab) {
                // Stop ASAP
                stopCurrentTts (tab.getPosition ());

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

    public boolean isDestroyed () {
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
            if (null != articlePage) {
                if (articlePage.onKeyUp (keyCode, event)) {
                    return true;
                }

                // Navigation via volume buttons
                if (SettingsManager.getInstance ().retrieveShouldUseVolumeNavigation ()) {
                    if (event.isCanceled ()) {
                        return super.onKeyUp (keyCode, event);
                    }

                    final WebView articleWebView = articlePage.webView ();
                    if (null == articleWebView) {
                        return super.onKeyUp (keyCode, event);
                    }

                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                        if (! articleWebView.pageUp (false)) {
                            final int currentItem = null != mViewPager ? mViewPager.getCurrentItem () : - 1;
                            if (currentItem > 0) {
                                mViewPager.setCurrentItem (currentItem - 1);
                            }
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        if (! articleWebView.pageDown (false)) {
                            final int currentItem = null != mViewPager ? mViewPager.getCurrentItem () : - 1;
                            if (currentItem < mPagerAdapter.getCount () - 1) {
                                mViewPager.setCurrentItem (currentItem + 1);
                            }
                        }
                        return true;
                    }
                }
            }
        }

        return super.onKeyUp (keyCode, event);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage) {
                if (articlePage.onKeyDown (keyCode, event)) {
                    return true;
                }

                // Navigation via volume buttons
                if (SettingsManager.getInstance ().retrieveShouldUseVolumeNavigation ()) {
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        event.startTracking ();
                        return true;
                    }
                }
            }
        }

        return super.onKeyDown (keyCode, event);
    }

    @Override
    public boolean onKeyLongPress (int keyCode, KeyEvent event) {
        if (null != mPagerAdapter) {
            final ArticleFragment articlePage = (ArticleFragment) mPagerAdapter.currentPage ();
            if (null != articlePage) {
                if (articlePage.onKeyLongPress (keyCode, event)) {
                    return true;
                }

                final WebView articleWebView = articlePage.webView ();
                if (null == articleWebView) {
                    return super.onKeyLongPress (keyCode, event);
                }

                // Navigation via volume buttons
                if (SettingsManager.getInstance ().retrieveShouldUseVolumeNavigation ()) {
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                        articleWebView.pageUp (true);
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        articleWebView.pageDown (true);
                        return true;
                    }
                }
            }
        }

        return super.onKeyLongPress (keyCode, event);
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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putBoolean (SavedStateArgs.IS_FULL_SCREEN, sIsFullscreenMode);
        super.onSaveInstanceState (outState);
    }

}
