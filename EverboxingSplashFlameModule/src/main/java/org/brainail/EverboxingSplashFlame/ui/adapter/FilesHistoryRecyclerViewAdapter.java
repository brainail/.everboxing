package org.brainail.EverboxingSplashFlame.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.brainail.EverboxingSplashFlame.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class FilesHistoryRecyclerViewAdapter <T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<T> mData;
    private final OnItemClickListener<T> mItemClickListener;

    public interface OnItemClickListener <T> {
        public void onItemClick (final View itemView, final T data);
    }

    public static class ItemViewHolder <T> extends RecyclerView.ViewHolder {
        @BindView (R.id.preview_flame_item)
        protected ImageView mPreviewFlameItem;

        protected final OnItemClickListener<T> mItemClickListener;
        protected T mData;

        public ItemViewHolder (final View itemView, final OnItemClickListener<T> itemClickListener) {
            super (itemView);
            ButterKnife.bind (this, itemView);
            mItemClickListener = itemClickListener;
        }

        public void bindPreviewFlame (final T file) {
            Glide.with (mPreviewFlameItem.getContext ())
                    .load (((File) file).getAbsolutePath ())
                    .diskCacheStrategy (DiskCacheStrategy.ALL)
                    .centerCrop ()
                    .crossFade ()
                    .into (mPreviewFlameItem);
        }

        public void setData (final T data) {
            mData = data;
        }

        @OnClick (R.id.preview_flame_frame)
        protected void openPreview () {
            if (null != mItemClickListener) {
                mItemClickListener.onItemClick (itemView, mData);
            }
        }
    }

    public FilesHistoryRecyclerViewAdapter (final Context context, final OnItemClickListener<T> itemClickListener) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

        mData = new ArrayList<> ();
        mItemClickListener = itemClickListener;
    }

    private View inflate (final ViewGroup parent, @LayoutRes final int layoutId) {
        return mInflater.inflate (layoutId, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final View itemView = inflate (parent, R.layout.view_files_history_item);
        return new ItemViewHolder<T> (itemView, mItemClickListener);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final T file = mData.get (position);
        itemViewHolder.bindPreviewFlame (file);
        itemViewHolder.setData (file);
    }

    @Override
    public int getItemCount () {
        return null != mData ? mData.size () : 0;
    }

    public void addTailData (final List<T> tailData) {
        final int currentSize = mData.size ();
        mData.addAll (tailData);
        notifyItemRangeInserted (currentSize, tailData.size ());
    }

}
