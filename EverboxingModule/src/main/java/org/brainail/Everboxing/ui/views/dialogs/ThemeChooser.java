package org.brainail.Everboxing.ui.views.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.views.ColorCircleView;
import org.brainail.Everboxing.utils.manager.ThemeManager;
import org.brainail.Everboxing.utils.tool.ToolResources;

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
public class ThemeChooser extends DialogFragment implements MaterialDialog.ListCallback {

    public static final String MANAGER_TAG = "org.brainail.Everboxing.tag#theme.chooser";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.settings_change_theme_dialog_title))
                .adapter(new ItemAdapter(getActivity(), ThemeManager.AppTheme.themes(getActivity())), this)
                .build();

        final ListView listView = dialog.getListView();
        if (null != listView) {
            listView.setVerticalScrollBarEnabled(false);
            listView.setHorizontalScrollBarEnabled(false);
            listView.setScrollbarFadingEnabled(true);
            listView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        }

        return dialog;
    }

    @Override
    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
        onThemeSelected(which);
    }

    private void onThemeSelected(final int which) {
        // Close dialog
        dismiss();

        // Apply theme
        ThemeManager.checkOnChange(getActivity(), ThemeManager.AppTheme.values() [which]);
    }

    //
    // +------------------------------------------------------------+
    // | Adapter for list view items                                |
    // +------------------------------------------------------------+
    // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    private static class ItemAdapter extends BaseAdapter {

        private final Context mContext;
        private CharSequence [] mItems;

        public ItemAdapter(final Context context, final String [] items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public CharSequence getItem(int position) {
            return mItems [position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        @SuppressLint("ViewHolder")
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = View.inflate(mContext, R.layout.theme_chooser_item, null);

            // Title
            final TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(mItems [position]);

            // Color
            final ColorCircleView colorView = (ColorCircleView) view.findViewById(R.id.color);
            final int themeResId = ThemeManager.AppTheme.values() [position].getThemeResId();
            colorView.setFillColor(ToolResources.retrievePrimaryColor(mContext, themeResId));

            return view;
        }

    }

}