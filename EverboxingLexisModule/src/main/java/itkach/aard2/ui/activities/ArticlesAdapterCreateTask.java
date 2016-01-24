package itkach.aard2.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import itkach.aard2.Application;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.ui.activities.ArticleCollectionActivity.CreatorMode;
import itkach.aard2.ui.adapters.ArticleCollectionPagerAdapter;
import itkach.aard2.ui.adapters.BlobDescriptorListAdapter;
import itkach.aard2.ui.adapters.BlobListAdapter;
import itkach.slob.Slob;

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
public class ArticlesAdapterCreateTask extends AsyncTask<Void, Void, ArticleCollectionPagerAdapter> {

    public Intent mIntent;
    public Uri mData;
    public String mAction;
    public int mPosition;
    public WeakReference<ArticleCollectionActivity> mSceneRef;

    public ArticlesAdapterCreateTask (final Intent intent, final ArticleCollectionActivity scene) {
        mIntent = intent;
        mData = intent.getData ();
        mAction = intent.getAction ();
        mPosition = intent.getIntExtra ("position", 0);
        mSceneRef = new WeakReference<> (scene);
    }

    private ArticleCollectionActivity scene () {
        return mSceneRef.get ();
    }

    @Override
    protected ArticleCollectionPagerAdapter doInBackground (Void... params) {
        if (mData != null) {
            return createFromUri (mData);
        } else {
            if (mAction == null) {
                return createFromLastResult ();
            } else if (mAction.equals (ArticleCollectionActivity.ShowMode.RANDOM)) {
                return createFromRandom ();
            } else if (mAction.equals (ArticleCollectionActivity.ShowMode.BOOKMARKS)) {
                return createFromBookmarks ();
            } else if (mAction.equals (ArticleCollectionActivity.ShowMode.HISTORY)) {
                return createFromHistory ();
            } else {
                return createFromIntent (mIntent);
            }
        }
    }

    @Override
    protected void onPostExecute (ArticleCollectionPagerAdapter adapter) {
        final ArticleCollectionActivity scene = scene ();

        // Wow! The is no any scene
        if (null == scene || scene.isFinishing () || scene.isDestroyed ()) {
            return;
        }

        scene.injectAdapter (adapter);

        if (adapter == null || adapter.getCount () == 0) {
            int messageId;
            if (adapter == null) {
                messageId = R.string.article_collection_invalid_link;
            } else {
                messageId = R.string.article_collection_nothing_found;
            }

            ToolUI.showToast (scene, messageId);
            scene.finish ();
            return;
        }

        if (mPosition > adapter.getCount () - 1) {
            ToolUI.showToast (scene, R.string.article_collection_selected_not_available);
            scene.finish ();
            return;
        }

        scene.findViewPager ();
        scene.findTabLayout ();
        scene.positionViewPagerCurrentItem (mPosition);
        scene.updateTabsState (true);

        scene.assignTabLayoutListener ();
        scene.assignViewPagerListener ();
        scene.updateTitle (mPosition);
        scene.observePagerAdapterDataSet ();
    }

    private ArticleCollectionPagerAdapter createFromUri (Uri articleUrl) {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        final String host = articleUrl.getHost ();

        // For instance ACTION_VIEW ...
        if (null != host && !(host.equals ("localhost") || host.matches ("127.\\d{1,3}.\\d{1,3}.\\d{1,3}"))) {
            return createFromIntent (mIntent);
        }

        final BlobDescriptor blobDescriptor = BlobDescriptor.fromUri (articleUrl);
        if (blobDescriptor == null) {
            return null;
        }

        Iterator<Slob.Blob> result = Application.app ().find (blobDescriptor.key, blobDescriptor.slobId);
        BlobListAdapter data = new BlobListAdapter (scene, 21, 1);
        data.setData (result);

        return new ArticleCollectionPagerAdapter (
                CreatorMode.URI, data,
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return (Slob.Blob) item;
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromLastResult () {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        return new ArticleCollectionPagerAdapter (
                CreatorMode.LAST_RESULT, Application.app ().mLastResult,
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return (Slob.Blob) item;
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromBookmarks () {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        return new ArticleCollectionPagerAdapter (
                CreatorMode.BOOKMARKS_SCREEN, new BlobDescriptorListAdapter (Application.app ().bookmarks),
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return Application.app ().bookmarks.resolve ((BlobDescriptor) item);
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromHistory () {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        return new ArticleCollectionPagerAdapter (
                CreatorMode.HISTORY_SCREEN, new BlobDescriptorListAdapter (Application.app ().history),
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return Application.app ().history.resolve ((BlobDescriptor) item);
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromRandom () {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        BlobListAdapter data = new BlobListAdapter (scene);
        List<Slob.Blob> result = new ArrayList<Slob.Blob> ();
        Slob.Blob blob = Application.app ().random ();

        if (blob != null) result.add (blob);
        data.setData (result);

        return new ArticleCollectionPagerAdapter (
                CreatorMode.RANDOM, data,
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return (Slob.Blob) item;
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private ArticleCollectionPagerAdapter createFromIntent (Intent intent) {
        final ArticleCollectionActivity scene = scene ();
        if (null == scene) {
            return null;
        }

        // ACTION_SEND ...
        String lookupKey = intent.getStringExtra (Intent.EXTRA_TEXT);

        // ACTION_SEARCH ...
        if (lookupKey == null) {
            lookupKey = intent.getStringExtra (SearchManager.QUERY);
        }

        // ACTION_COLORDICT ...
        if (lookupKey == null) {
            lookupKey = intent.getStringExtra ("EXTRA_QUERY");
        }

        // ACTION_VIEW?
        if (lookupKey == null) {
            if (null != intent.getData ()) {
                final List<String> segments = intent.getData ().getPathSegments ();
                if (null != segments && !segments.isEmpty ()) {
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
            final String[] parts = lookupKey.split ("\\s+");

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

        final BlobListAdapter data = new BlobListAdapter (scene, 21, 1);
        if (TextUtils.isEmpty (lookupKey) || TextUtils.isEmpty (lookupKey.trim ())) {
            ToolUI.showToast (scene, R.string.article_collection_nothing_to_lookup);
        } else {
            final Iterator<Slob.Blob> result = stemLookup (lookupKey);
            data.setData (result);
        }

        return new ArticleCollectionPagerAdapter (
                CreatorMode.INTENT, data,
                new ArticleCollectionActivity.BlobConverter () {
                    @Override
                    public Slob.Blob convert (Object item) {
                        return (Slob.Blob) item;
                    }
                }, scene.getSupportFragmentManager ()
        );
    }

    private Iterator<Slob.Blob> stemLookup (String lookupKey) {
        Slob.PeekableIterator<Slob.Blob> result;
        final int length = lookupKey.length ();
        String currentLookupKey = lookupKey;
        int currentLength = currentLookupKey.length ();

        do {
            result = Application.app ().find (currentLookupKey, null, true);
            if (result.hasNext ()) {
                Slob.Blob b = result.peek ();
                if (b.key.length () - length > 3) {
                    // We don't like this result
                } else {
                    break;
                }
            }
            currentLookupKey = currentLookupKey.substring (0, currentLength - 1);
            currentLength = currentLookupKey.length ();
        } while (length - currentLength < 5 && currentLength > 0);

        return result;
    }

}
