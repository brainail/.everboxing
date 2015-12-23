package itkach.aard2.ui.adapters;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import itkach.aard2.Application;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.ui.activities.ArticleCollectionActivity.CreatorMode;
import itkach.aard2.ui.activities.ArticleCollectionActivity.BlobConverter;
import itkach.aard2.ui.fragments.ArticleFragment;
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
public class ArticleCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private DataSetObserver mDataObserver;
    private BaseAdapter mDataAdapter;
    private BlobConverter mBlobConverter;
    private CreatorMode mCreatorMode;
    private int mCount;

    private Fragment mCurrentPage;

    public ArticleCollectionPagerAdapter (
            final CreatorMode creatorMode,
            final BaseAdapter dataAdapter,
            final BlobConverter blobConverter,
            final FragmentManager fragmentManager) {

        super (fragmentManager);

        mDataAdapter = dataAdapter;
        mCount = dataAdapter.getCount ();
        mCreatorMode = creatorMode;

        mDataObserver = new DataSetObserver () {
            @Override
            public void onChanged () {
                mCount = selfDataAdapter ().getCount ();
                notifyDataSetChanged ();
            }
        };

        mDataAdapter.registerDataSetObserver (mDataObserver);
        mBlobConverter = blobConverter;
    }

    public BaseAdapter selfDataAdapter () {
        return mDataAdapter;
    }

    public CreatorMode creatorMode () {
        return mCreatorMode;
    }

    public void destroy () {
        mDataAdapter.unregisterDataSetObserver (mDataObserver);

        mDataAdapter = null;
        mCurrentPage = null;
    }

    @Override
    public Fragment getItem (int i) {
        final Fragment fragment = new ArticleFragment ();
        final Slob.Blob blob = get (i);

        if (blob != null) {
            final Bundle args = new Bundle ();
            args.putString (ArticleFragment.Args.ARTICLE_URL, Application.app ().getUrl (blob));
            args.putString (ArticleFragment.Args.ARTICLE_TITLE, getPageTitle (i).toString ());
            fragment.setArguments (args);
        } else {
            final Bundle args = new Bundle ();
            args.putString (ArticleFragment.Args.ARTICLE_TITLE, getPageTitle (i).toString ());
            fragment.setArguments (args);
        }

        return fragment;
    }

    public Fragment currentPage () {
        return mCurrentPage;
    }

    @Override
    public void setPrimaryItem (ViewGroup container, int position, Object page) {
        if (mCurrentPage != page) {
            mCurrentPage = (Fragment) page;
        }

        super.setPrimaryItem (container, position, page);
    }

    @Override
    public int getCount () {
        return mCount;
    }

    public Slob.Blob get (int position) {
        return mBlobConverter.convert (mDataAdapter.getItem (position));
    }

    @Override
    public CharSequence getPageTitle (int position) {
        if (position < mDataAdapter.getCount ()) {
            Object item = mDataAdapter.getItem (position);

            if (item instanceof BlobDescriptor) {
                return ((BlobDescriptor) item).key;
            } else if (item instanceof Slob.Blob) {
                return ((Slob.Blob) item).key;
            }
        }

        return ToolResources.string (R.string.wtf_emo);
    }

    //this is needed so that fragment is properly updated
    //if underlying data changes (such as on unbookmark)
    //https://code.google.com/p/android/issues/detail?id=19001
    @Override
    public int getItemPosition (Object object) {
        return POSITION_NONE;
    }

}
