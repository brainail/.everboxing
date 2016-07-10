package org.brainail.EverboxingHardyDialogs;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.brainail.EverboxingTools.utils.tool.ToolRecyclerViewAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class SimpleBottomSheetAdapter <T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<T> mData;
    private final OnListItemClickListener<T> mItemClickListener;

    public interface OnListItemClickListener <T> {
        public void onListItemClick (final View itemView, final T data, final int position);
    }

    public static class ItemViewHolder <T> extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected final OnListItemClickListener<T> mItemClickListener;
        protected T mData;
        protected TextView mText;

        public ItemViewHolder (final View itemView, final OnListItemClickListener<T> itemClickListener) {
            super (itemView);
            mItemClickListener = itemClickListener;
            mText = (TextView) itemView.findViewById (android.R.id.text1);
            mText.setOnClickListener (this);
        }

        public void bindData (final T data) {
            mText.setText ((String) data);
        }

        public void setData (final T data) {
            mData = data;
        }

        @Override
        public void onClick (View view) {
            final RecyclerView.ViewHolder itemHolder = ToolRecyclerViewAdapter.getViewHolder (itemView);

            final int globalFlatPosition = null != itemHolder
                    ? itemHolder.getAdapterPosition ()
                    : RecyclerView.NO_POSITION;

            if (null != mItemClickListener) {
                mItemClickListener.onListItemClick (itemView, mData, globalFlatPosition);
            }
        }
    }

    public SimpleBottomSheetAdapter (
            final Context context,
            final OnListItemClickListener<T> itemClickListener) {

        this (context, Collections.<T>emptyList (), itemClickListener);
    }

    public SimpleBottomSheetAdapter (
            final Context context,
            final T [] data,
            final OnListItemClickListener<T> itemClickListener) {

        this (context, Arrays.asList (data), itemClickListener);
    }

    public SimpleBottomSheetAdapter (
            final Context context,
            final List<T> data,
            final OnListItemClickListener<T> itemClickListener) {

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

        mData = data;
        mItemClickListener = itemClickListener;
    }

    private View inflate (final ViewGroup parent, final @LayoutRes int layoutId) {
        return mInflater.inflate (layoutId, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final View itemView = inflate (parent, R.layout.list_item_simple);
        return new ItemViewHolder<T> (itemView, mItemClickListener);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final T data = mData.get (position);
        itemViewHolder.bindData (data);
        itemViewHolder.setData (data);
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
