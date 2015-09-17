/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import itkach.aard2.Application;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.ui.adapters.BlobDescriptorListAdapter;
import itkach.aard2.ui.adapters.BlobListAdapter;
import itkach.aard2.ui.fragments.ArticleFragment;
import itkach.slob.Slob;
import itkach.slob.Slob.Blob;

public class ArticleCollectionActivity extends BaseActivity {

    public static enum CreatorMode {
        INTENT, URI, BOOKMARKS_SCREEN, HISTORY_SCREEN, LAST_RESULT, RANDOM
    }

    ArticleCollectionPagerAdapter articleCollectionPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    ToBlob blobToBlob = new ToBlob() {

        @Override
        public Blob convert(Object item) {
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

    private boolean onDestroyCalled = false;

    @SuppressLint ("MissingSuperCall")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Application app = (Application) getApplication();
        // requestWindowFeature(Window.FEATURE_PROGRESS);
        // setContentView(R.layout.activity_article_collection_loading);
        app.push(this);
        // final ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setSubtitle("...");
        final Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);

        AsyncTask<Void, Void, ArticleCollectionPagerAdapter> createAdapterTask = new AsyncTask<Void, Void, ArticleCollectionPagerAdapter>() {

            @Override
            protected ArticleCollectionPagerAdapter doInBackground(Void... params) {
                ArticleCollectionPagerAdapter result;
                Uri articleUrl = intent.getData();
                if (articleUrl != null) {
                    result = createFromUri(app, articleUrl);
                } else {
                    String action = intent.getAction();
                    if (action == null) {
                        result = createFromLastResult(app);
                    } else if (action.equals("showRandom")) {
                        result = createFromRandom(app);
                    } else if (action.equals("showBookmarks")) {
                        result = createFromBookmarks(app);
                    } else if (action.equals("showHistory")) {
                        result = createFromHistory(app);
                    } else {
                        result = createFromIntent(app, intent);
                    }
                }
                return result;
            }
            
            @Override
            protected void onPostExecute(ArticleCollectionPagerAdapter adapter) {
                if (isFinishing() || onDestroyCalled) {
                    return;
                }
                articleCollectionPagerAdapter = adapter;
                if (articleCollectionPagerAdapter == null || articleCollectionPagerAdapter.getCount() == 0) {
                    int messageId;
                    if (articleCollectionPagerAdapter == null) {
                        messageId = R.string.article_collection_invalid_link;
                    } else {
                        messageId = R.string.article_collection_nothing_found;
                    }
                    Toast.makeText(ArticleCollectionActivity.this, messageId,
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (position > articleCollectionPagerAdapter.getCount() - 1) {
                    Toast.makeText(ArticleCollectionActivity.this, R.string.article_collection_selected_not_available,
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // setContentView (R.layout.activity_article_collection);

                viewPager = (ViewPager) findViewById(R.id.articles_pager);
                viewPager.setAdapter (articleCollectionPagerAdapter);
                tabLayout = (TabLayout) findViewById (R.id.articles_tabs);
                viewPager.setCurrentItem (position);

                updateTabsState (true);

                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                viewPager.addOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                    }

                    @Override
                    public void onPageSelected(final int position) {
                        updateTabsState(false);
                        updateTitle(position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArticleFragment fragment =
                                        (ArticleFragment) articleCollectionPagerAdapter.getItem(position);
                                fragment.applyTextZoomPref();
                            }
                        });

                    }
                });

                updateTitle (position);
                articleCollectionPagerAdapter.registerDataSetObserver (new DataSetObserver () {
                    @Override
                    public void onChanged () {
                        if (articleCollectionPagerAdapter.getCount () == 0) {
                            finish ();
                            return;
                        }

                        updateTabsState (false);
                    }
                });
            }
        };

        createAdapterTask.execute();

    }

    public void unbookmarkCurrentTab() {
        if (CreatorMode.BOOKMARKS_SCREEN == articleCollectionPagerAdapter.creatorMode() && tabLayout.getTabCount () > 1) {
            tabLayout.removeTabAt (tabLayout.getSelectedTabPosition ());
        }
    }

    private void updateTabsState (final boolean isInitialization) {
        final int LIMIT = 30;
        final int lastVisiblePage = Math.min (viewPager.getCurrentItem () + LIMIT, articleCollectionPagerAdapter.getCount ());
        for (int tabIndex = tabLayout.getTabCount (); tabIndex < lastVisiblePage; ++ tabIndex) {
            tabLayout.addTab (tabLayout.newTab ().setText (articleCollectionPagerAdapter.getPageTitle (tabIndex)), false);
        }

        while (tabLayout.getTabCount () > articleCollectionPagerAdapter.getCount ()) {
            tabLayout.removeTabAt (tabLayout.getTabCount () - 1);
        }

        final TabLayout.Tab selectedTab = tabLayout.getTabAt (viewPager.getCurrentItem ());
        if (null != selectedTab) {
            if (isInitialization) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectedTab.select();
                    }
                }, 500);
            } else {
                selectedTab.select();
            }
        }
    }

    private ArticleCollectionPagerAdapter createFromUri(Application app, Uri articleUrl) {
        BlobDescriptor bd = BlobDescriptor.fromUri(articleUrl);
        if (bd == null) {
            return null;
        }
        Iterator<Slob.Blob> result = app.find(bd.key, bd.slobId);
        BlobListAdapter data = new BlobListAdapter(this, 21, 1);
        data.setData(result);
        return new ArticleCollectionPagerAdapter(
                CreatorMode.URI, app, data, blobToBlob, getSupportFragmentManager()
        );
    }

    ;

    private ArticleCollectionPagerAdapter createFromLastResult(Application app) {
        return new ArticleCollectionPagerAdapter(
                CreatorMode.LAST_RESULT, app, app.lastResult, blobToBlob, getSupportFragmentManager()
        );
    }

    private ArticleCollectionPagerAdapter createFromBookmarks(final Application app) {
        return new ArticleCollectionPagerAdapter (
                CreatorMode.BOOKMARKS_SCREEN, app, new BlobDescriptorListAdapter (app.bookmarks),
                new ToBlob () {
                    @Override
                    public Blob convert (Object item) {
                        return app.bookmarks.resolve ((BlobDescriptor) item);
                    }
                }, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromHistory(final Application app) {
        return new ArticleCollectionPagerAdapter (
                CreatorMode.HISTORY_SCREEN, app, new BlobDescriptorListAdapter (app.history),
                new ToBlob () {
                    @Override
                    public Blob convert (Object item) {
                        return app.history.resolve ((BlobDescriptor) item);
                    }
                }, getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromRandom(Application app) {
        BlobListAdapter data = new BlobListAdapter(this);
        List<Blob> result = new ArrayList<Blob>();
        Blob blob = app.random();
        if (blob != null) {
            result.add(blob);
        }
        data.setData(result);
        return new ArticleCollectionPagerAdapter(
                CreatorMode.RANDOM, app, data, blobToBlob, getSupportFragmentManager()
        );
    }

    private ArticleCollectionPagerAdapter createFromIntent(Application app, Intent intent) {
        String lookupKey = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (lookupKey == null) {
            lookupKey = intent.getStringExtra(SearchManager.QUERY);
        }
        BlobListAdapter data = new BlobListAdapter(this, 21, 1);
        if (lookupKey == null || lookupKey.length() == 0) {
            Toast.makeText(this, R.string.article_collection_nothing_to_lookup, Toast.LENGTH_SHORT).show();
        } else {
            Iterator<Blob> result = stemLookup(app, lookupKey);
            data.setData(result);
        }
        return new ArticleCollectionPagerAdapter(
                CreatorMode.INTENT, app, data, blobToBlob, getSupportFragmentManager()
        );
    }

    private Iterator<Blob> stemLookup(Application app, String lookupKey) {
        Slob.PeekableIterator<Blob> result;
        final int length = lookupKey.length();
        String currentLookupKey = lookupKey;
        int currentLength = currentLookupKey.length();
        do {
            result = app.find(currentLookupKey, null, true);
            if (result.hasNext()) {
                Blob b = result.peek();
                if (b.key.length() - length > 3) {
                    //we don't like this result
                } else {
                    break;
                }
            }
            currentLookupKey = currentLookupKey.substring(0, currentLength - 1);
            currentLength = currentLookupKey.length();
        } while (length - currentLength < 5 && currentLength > 0);
        return result;
    }

    private void updateTitle(int position) {
        Log.d("updateTitle", "" + position + " count: " + articleCollectionPagerAdapter.getCount());
        Slob.Blob blob = articleCollectionPagerAdapter.get(position);
        CharSequence pageTitle = articleCollectionPagerAdapter.getPageTitle(position);
        Log.d("updateTitle", "" + blob);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (blob != null) {
            String dictLabel = blob.owner.getTags().get("label");
            if (null != actionBar) actionBar.setTitle(dictLabel);
            Application app = (Application) getApplication();
            app.history.add(app.getUrl(blob));
        } else {
            if (null != actionBar) actionBar.setTitle("???");
        }
        if (null != actionBar) actionBar.setSubtitle(pageTitle);
    }

    @SuppressLint ("MissingSuperCall")
    @Override
    protected void onDestroy() {
        onDestroyCalled = true;
        if (viewPager != null) {
            viewPager.setAdapter(null);
        }
        if (articleCollectionPagerAdapter != null) {
            articleCollectionPagerAdapter.destroy();
        }
        Application app = (Application) getApplication();
        app.pop(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = new Intent(this, HomeActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(upIntent).startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart () {
        super.onStart ();
        if (null != articleCollectionPagerAdapter) {
            if (CreatorMode.BOOKMARKS_SCREEN == articleCollectionPagerAdapter.creatorMode ()
                    || CreatorMode.HISTORY_SCREEN == articleCollectionPagerAdapter.creatorMode ()) {
                if (null != tabLayout) {
                    tabLayout.removeAllTabs ();
                    updateTabsState (true);
                }
            }
        }
    }

    @Override
    protected void onStop () {
        super.onStop ();
    }

    static interface ToBlob {
        Slob.Blob convert(Object item);
    }

    public static class ArticleCollectionPagerAdapter extends FragmentStatePagerAdapter {

        private Application app;
        private DataSetObserver observer;
        private BaseAdapter data;
        private ToBlob toBlob;
        private int count;
        private CreatorMode creatorMode;

        public ArticleCollectionPagerAdapter(
                CreatorMode creatorMode,
                Application app,
                BaseAdapter data,
                ToBlob toBlob,
                FragmentManager fm) {

            super(fm);

            this.app = app;
            this.data = data;
            this.count = data.getCount();
            this.creatorMode = creatorMode;

            this.observer = new DataSetObserver() {
                @Override
                public void onChanged() {
                    count = ArticleCollectionPagerAdapter.this.data.getCount();
                    notifyDataSetChanged ();
                }
            };
            data.registerDataSetObserver(observer);
            this.toBlob = toBlob;
        }

        public CreatorMode creatorMode() {
            return creatorMode;
        }

        void destroy() {
            data.unregisterDataSetObserver(observer);
            data = null;
            app = null;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new ArticleFragment();
            Slob.Blob blob = get(i);
            if (blob != null) {
                String articleUrl = app.getUrl(blob);
                Bundle args = new Bundle();
                args.putString(ArticleFragment.ARG_URL, articleUrl);
                fragment.setArguments(args);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return count;
        }

        Slob.Blob get(int position) {
            return toBlob.convert(data.getItem(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < data.getCount()) {
                Object item = data.getItem(position);
                if (item instanceof BlobDescriptor) {
                    return ((BlobDescriptor) item).key;
                }
                if (item instanceof Slob.Blob) {
                    return ((Blob) item).key;
                }
            }
            return "???";
        }

        //this is needed so that fragment is properly updated
        //if underlying data changes (such as on unbookmark)
        //https://code.google.com/p/android/issues/detail?id=19001
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
