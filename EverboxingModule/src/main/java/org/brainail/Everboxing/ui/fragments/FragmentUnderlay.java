package org.brainail.Everboxing.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.callable.Tagable;
import org.brainail.Everboxing.utils.callable.Titleable;
import org.brainail.Everboxing.utils.tool.ToolFragments;

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
public class FragmentUnderlay extends Fragment implements Tagable, /* Colorable, */ Titleable {

    @Override public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final FrameLayout view = new FrameLayout (getActivity ());

        final Button button = new Button (getActivity ());
        button.setText ("Go upper!");
        view.addView (button, new FrameLayout.LayoutParams (400, 200, Gravity.CENTER));

        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                ToolFragments.openFragment ((AppCompatActivity) getActivity (), new FragmentOverlay ());
            }
        });
        view.setBackgroundResource (R.color.md_light_blue_A200);
        return view;
    }

    @Override public String tag () {
        return "FragmentUnderlay#Tag";
    }

    // @Override public Integer color () {
        // return getResources ().getColor (R.color.md_purple_500);
    // }

    @Override public String title () {
        return "FragmentUnderlay";
    }

}
