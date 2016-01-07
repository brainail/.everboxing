package itkach.aard2.ui.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.ui.activities.HomeActivity;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import itkach.aard2.Application;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.ui.adapters.ArticleCollectionPagerAdapter;
import itkach.aard2.ui.adapters.BlobDescriptorListAdapter;
import itkach.aard2.ui.adapters.BlobListAdapter;
import itkach.aard2.ui.fragments.ArticleFragment;
import itkach.slob.Slob;
import itkach.slob.Slob.Blob;

public class ArticleCollectionActivity extends BaseActivity {

    public static enum CreatorMode {
        INTENT, URI, BOOKMARKS_SCREEN, HISTORY_SCREEN, LAST_RESULT, RANDOM
    }

    public static interface BlobConverter {
        public Slob.Blob convert (Object item);
    }

    public ArticleCollectionPagerAdapter mPagerAdapter;
    public ViewPager mViewPager;
    public TabLayout mTabLayout;

    public final BlobConverter mBlobConverter = new BlobConverter () {
        @Override
        public Blob convert (Object item) {
            return (Blob) item;
        }
    };

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

    private boolean mIsDestroyed = false;

    @SuppressLint ("MissingSuperCall")
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        Application.app ().push (this);

        final Intent intent = getIntent ();
        final int position = intent.getIntExtra ("position", 0);

        final AsyncTask<Void, Void, ArticleCollectionPagerAdapter> createAdapterTask
                = new AsyncTask<Void, Void, ArticleCollectionPagerAdapter> () {

            @Override
            protected ArticleCollectionPagerAdapter doInBackground (Void... params) {
                ArticleCollectionPagerAdapter result;
                Uri articleUrl = intent.getData ();
                if (articleUrl != null) {
                    result = createFromUri (Application.app (), articleUrl);
                } else {
                    String action = intent.getAction ();
                    if (action == null) {
                        result = createFromLastResult (Application.app ());
                    } else if (action.equals ("showRandom")) {
                        result = createFromRandom (Application.app ());
                    } else if (action.equals ("showBookmarks")) {
                        result = createFromBookmarks (Application.app ());
                    } else if (action.equals ("showHistory")) {
                        result = createFromHistory (Application.app ());
                    } else {
                        result = createFromIntent (Application.app (), intent);
                    }
                }
                return result;
            }
            
            @Override
            protected void onPostExecute (ArticleCollectionPagerAdapter adapter) {
                if (isFinishing () || mIsDestroyed) {
                    return;
                }

                mPagerAdapter = adapter;
                if (mPagerAdapter == null || mPagerAdapter.getCount () == 0) {
                    int messageId;
                    if (mPagerAdapter == null) {
                        messageId = R.string.article_collection_invalid_link;
                    } else {
                        messageId = R.string.article_collection_nothing_found;
                    }

                    ToolUI.showToast (self (), messageId);
                    finish ();
                    return;
                }

                if (position > mPagerAdapter.getCount () - 1) {
                    ToolUI.showToast (self (), R.string.article_collection_selected_not_available);
                    finish ();
                    return;
                }

                mViewPager = (ViewPager) findViewById (R.id.articles_pager);
                mViewPager.setAdapter (mPagerAdapter);
                mTabLayout = (TabLayout) findViewById (R.id.articles_tabs);
                mViewPager.setCurrentItem (position);

                updateTabsState (true);

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

                mViewPager.addOnPageChangeListener (new OnPageChangeListener () {
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

                updateTitle (position);
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
        };

        createAdapterTask.execute ();
    }

    public void unbookmarkCurrentTab () {
        if (CreatorMode.BOOKMARKS_SCREEN == mPagerAdapter.creatorMode () && mTabLayout.getTabCount () > 1) {
            mTabLayout.removeTabAt (mTabLayout.getSelectedTabPosition ());
        }
    }

    private void updateTabsState (final boolean isInitialization) {
        final int LIMIT = 30;
        final int lastVisiblePage = Math.min (mViewPager.getCurrentItem () + LIMIT, mPagerAdapter.getCount ());

        for (int tabIndex = mTabLayout.getTabCount (); tabIndex < lastVisiblePage; ++tabIndex) {
            mTabLayout.addTab (mTabLayout.newTab ().setText (mPagerAdapter.getPageTitle (tabIndex)), false);
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

    private ArticleCollectionPagerAdapter createFromUri (Application app, Uri articleUrl) {
        final String host = articleUrl.getHost();

        // For instance ACTION_VIEW ...
        if (null != host && ! (host.equals ("localhost") || host.matches ("127.\\d{1,3}.\\d{1,3}.\\d{1,3}"))) {
            return createFromIntent (app, getIntent ());
        }

        final BlobDescriptor blobDescriptor = BlobDescriptor.fromUri (articleUrl);
        if (blobDescriptor == null) {
            return null;
        }

        Iterator<Slob.Blob> result = app.find (blobDescriptor.key, blobDescriptor.slobId);
        BlobListAdapter data = new BlobListAdapter (this, 21, 1);
        data.setData (result);

        return new ArticleCollectionPagerAdapter (
                CreatorMode.URI, data, mBlobConverter, getSupportFragmentManager ()
        );
    }

    ;

    private ArticleCollectionPagerAdapter createFromLastResult (Application app) {
        return new ArticleCollectionPagerAdapter (
                CreatorMode.LAST_RESULT, app.mLastResult, mBlobConverter, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromBookmarks (final Application app) {
        return new ArticleCollectionPagerAdapter (
                CreatorMode.BOOKMARKS_SCREEN, new BlobDescriptorListAdapter (app.bookmarks),
                new BlobConverter () {
                    @Override
                    public Blob convert (Object item) {
                        return app.bookmarks.resolve ((BlobDescriptor) item);
                    }
                }, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromHistory (final Application app) {
        return new ArticleCollectionPagerAdapter (
                CreatorMode.HISTORY_SCREEN, new BlobDescriptorListAdapter (app.history),
                new BlobConverter () {
                    @Override
                    public Blob convert (Object item) {
                        return app.history.resolve ((BlobDescriptor) item);
                    }
                }, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromRandom (Application app) {
        BlobListAdapter data = new BlobListAdapter (this);
        List<Blob> result = new ArrayList<Blob> ();
        Blob blob = app.random ();

        if (blob != null) result.add (blob);
        data.setData (result);

        return new ArticleCollectionPagerAdapter (
                CreatorMode.RANDOM, data, mBlobConverter, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromIntent (Application app, Intent intent) {
        // ACTION_SEND ...
        String lookupKey = intent.getStringExtra (Intent.EXTRA_TEXT);

        // ACTION_SEARCH ...
        if (lookupKey == null) {
            lookupKey = intent.getStringExtra (SearchManager.QUERY);
        }

        // ACTION_COLORDICT ...
        if (lookupKey == null) {
            lookupKey = intent.getStringExtra("EXTRA_QUERY");
        }

        // ACTION_VIEW?
        if (lookupKey == null) {
            if (null != intent.getData()) {
                final List<String> segments = intent.getData().getPathSegments ();
                if (null != segments && ! segments.isEmpty ()) {
                    // For instance http://.../wiki/wtf --> wtf
                    // It's from aard. getLastPathSegment ()? Why so serious?
                    lookupKey = segments.get (segments.size () - 1);
                }
            }
        }

        // Try to match wiki (everyone likes wiki) or something else (in the future ...)
        // It's for cases like ->
        //     ..TEXT = thirst - Wiktionary https://en.m.wiktionary.org/wiki/thirst (Share from CM Browser)
        if (null != lookupKey) {
            final String [] parts = lookupKey.split ("\\s+");

            // Wiki stuff ...
            String wikiLookup = null;

            for (final String part : parts) {
                try {
                    // If no exception then it's OK
                    final URL urlChecker = new URL (part);

                    final Uri possibleUri = Uri.parse (part);
                    final List<String> segments = possibleUri.getPathSegments ();

                    // Wiki stuff ...
                    if (segments.size () > 1 && segments.get (0).equalsIgnoreCase ("wiki")) {
                        wikiLookup = segments.get (segments.size () - 1);
                        break;
                    }
                } catch (final Exception exception) {
                    // Ooops ...
                }
            }

            // Wiki stuff ...
            if (null != wikiLookup) {
                lookupKey = wikiLookup;
            }
        }

        final BlobListAdapter data = new BlobListAdapter (this, 21, 1);
        if (TextUtils.isEmpty (lookupKey) || TextUtils.isEmpty (lookupKey.trim ())) {
            ToolUI.showToast (self (), R.string.article_collection_nothing_to_lookup);
        } else {
            final Iterator<Blob> result = stemLookup (app, lookupKey);
            data.setData (result);
        }

        return new ArticleCollectionPagerAdapter (
                CreatorMode.INTENT, data, mBlobConverter, getSupportFragmentManager ()
        );
    }

    private Iterator<Blob> stemLookup (Application app, String lookupKey) {
        Slob.PeekableIterator<Blob> result;
        final int length = lookupKey.length ();
        String currentLookupKey = lookupKey;
        int currentLength = currentLookupKey.length ();

        do {
            result = app.find (currentLookupKey, null, true);
            if (result.hasNext ()) {
                Blob b = result.peek ();
                if (b.key.length () - length > 3) {
                    //we don't like this result
                } else {
                    break;
                }
            }
            currentLookupKey = currentLookupKey.substring (0, currentLength - 1);
            currentLength = currentLookupKey.length ();
        } while (length - currentLength < 5 && currentLength > 0);

        return result;
    }

    private void updateTitle (int position) {
        Slob.Blob blob = mPagerAdapter.get (position);
        CharSequence pageTitle = mPagerAdapter.getPageTitle (position);

        ActionBar actionBar = getSupportActionBar ();
        if (blob != null) {
            String dictLabel = blob.owner.getTags ().get ("label");
            if (null != actionBar) actionBar.setTitle (dictLabel);
            Application.app ().history.add (Application.app ().getUrl (blob));
        } else {
            if (null != actionBar) actionBar.setTitle (ToolResources.string (R.string.wtf_emo));
        }

        if (null != actionBar) actionBar.setSubtitle (pageTitle);
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
