package org.brainail.EverboxingSplashFlame._app.features.history;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
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
public class FilesHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<File> mData;
    private final OnItemClickListener<File> mItemClickListener;

    public interface OnItemClickListener <T> {
        public void onItemClick (final View itemView, final T data);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView (R.id.preview_flame_item)
        protected ImageView mPreviewFlameItem;

        protected final OnItemClickListener<File> mItemClickListener;
        protected File mData;

        public ItemViewHolder (final View itemView, final OnItemClickListener<File> itemClickListener) {
            super (itemView);
            ButterKnife.bind (this, itemView);
            mItemClickListener = itemClickListener;
        }

        public void bindPreviewFlame (final File file) {
            Glide.with (mPreviewFlameItem.getContext ())
                    .load (file.getAbsolutePath ())
                    .diskCacheStrategy (DiskCacheStrategy.ALL)
                    .centerCrop ()
                    .crossFade ()
                    .into (mPreviewFlameItem);
        }

        public void setData (final File data) {
            mData = data;
        }

        @OnClick (R.id.preview_flame_frame)
        protected void openPreview () {
            if (null != mItemClickListener) {
                mItemClickListener.onItemClick (itemView, mData);
            }
        }
    }

    public static final class FilesListDiffCallback extends DiffUtil.Callback {
        private final @NonNull List<File> mOldFiles;
        private final @NonNull List<File> mNewFiles;

        public FilesListDiffCallback(final @NonNull List<File> oldFiles, final @NonNull List<File> newFiles) {
            mOldFiles = oldFiles;
            mNewFiles = newFiles;
        }

        @Override
        public int getOldListSize() {
            return mOldFiles.size();
        }

        @Override
        public int getNewListSize() {
            return mNewFiles.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mNewFiles.get(newItemPosition).getAbsolutePath ().equals (mOldFiles.get (oldItemPosition).getAbsolutePath ());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mNewFiles.get(newItemPosition).getAbsolutePath ().equals (mOldFiles.get (oldItemPosition).getAbsolutePath ());
        }

        @Nullable
        @Override
        public Object getChangePayload (int oldItemPosition, int newItemPosition) {
            return super.getChangePayload (oldItemPosition, newItemPosition);
        }
    }

    public FilesHistoryRecyclerViewAdapter (final Context context, final OnItemClickListener<File> itemClickListener) {
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
        return new ItemViewHolder (itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder (holder, position, payloads);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final File file = mData.get (position);
        itemViewHolder.bindPreviewFlame (file);
        itemViewHolder.setData (file);
    }

    @Override
    public int getItemCount () {
        return null != mData ? mData.size () : 0;
    }

    public List<File> getData () {
        return mData;
    }

    public void swapDataQuietly (final List<File> files) {
        mData.clear ();
        mData.addAll (files);
    }

    public void addTailData (final List<File> tailData) {
        final int currentSize = mData.size ();
        mData.addAll (tailData);
        notifyItemRangeInserted (currentSize, tailData.size ());
    }

}
