package org.brainail.EverboxingSplashFlame.ui.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.brainail.EverboxingSplashFlame.Constants;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.activities.FlameActivity;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolUI;
import org.brainail.EverboxingTools.utils.tool.ToolPhone;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
public class FavouriteFragment extends BaseFragment {
    @BindView (R.id.flame_it)
    protected Button mFlameIt;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_favourite, container, false);
        ButterKnife.bind (this, view);
        return view;
    }

    @OnClick (R.id.flame_it)
    void flameIt () {
        AsyncTaskCompat.executeParallel (new AsyncTask<Void, Void, String> () {
            @Override
            protected String doInBackground (Void... params) {
                File filePath = new File (Constants.APP_MEDIA_DIR_PATH, "fractal_" + System.currentTimeMillis () + ".jpeg");
                if (! filePath.getParentFile ().exists ()) {
                    filePath.getParentFile ().mkdirs ();
                }
                final Point sizes = ToolPhone.realScreenSize (getContext ());
                // ToolFractal.warmUp (filePath.getAbsolutePath (), sizes.x, sizes.y, 7);
                return filePath.getAbsolutePath ();
            }

            @Override
            protected void onPostExecute (String path) {
                super.onPostExecute (path);
                ToolUI.showToast (FavouriteFragment.this, "We are freaky guys (.)*(.)");
                startActivity (new Intent (getActivity (), FlameActivity.class));
//                Glide.with (FavouriteFragment.this)
//                        .load (path)
//                        .centerCrop ()
//                        .diskCacheStrategy (DiskCacheStrategy.NONE)
//                        .crossFade ()
//                        .into (mPlaceholder);
            }
        });
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

    }
}
