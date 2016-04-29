package org.brainail.EverboxingLexis.ui.views.preference;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingTools.utils.Sdk;
import org.brainail.EverboxingTools.utils.tool.ToolFonts;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import butterknife.ButterKnife;

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
public class PreferenceCategory extends android.preference.PreferenceCategory {

    private TextView mTitleView;

    public PreferenceCategory (Context context) {
        super (context);
    }

    public PreferenceCategory (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public PreferenceCategory (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
    }

    @Override
    protected View onCreateView (ViewGroup parent) {
        final View view = super.onCreateView (parent);

        // Change title options
        mTitleView = ButterKnife.findById (view, android.R.id.title);

        mTitleView.setAllCaps (false);
        mTitleView.setTextColor (ToolResources.retrieveAccentColor (getContext ()));

        mTitleView.setTypeface (ToolFonts.robotoMedium ());

        // We want to look like L+
        if (!Sdk.isSdkSupported (Sdk.LOLLIPOP)) {
            mTitleView.setBackgroundDrawable (null);

            final Resources resources = getContext ().getResources ();
            final int hSpacing = resources.getDimensionPixelSize (R.dimen.preference_category_pre_L_h_side_spacing);
            final int vSpacing = resources.getDimensionPixelSize (R.dimen.preference_category_pre_L_v_side_spacing);
            mTitleView.setPadding (hSpacing, vSpacing, mTitleView.getPaddingRight (), mTitleView.getPaddingBottom ());
        }

        return view;
    }

}