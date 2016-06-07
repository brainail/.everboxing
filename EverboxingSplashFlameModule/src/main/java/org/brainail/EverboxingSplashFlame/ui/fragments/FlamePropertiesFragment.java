package org.brainail.EverboxingSplashFlame.ui.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingSplashFlame.Constants;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.activities.FlamePreviewActivity;
import org.brainail.EverboxingSplashFlame.ui.views.dialogs.hardy.SplashFlameHardyDialogs;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolFractal;
import org.brainail.EverboxingTools.utils.tool.ToolPhone;

import java.io.File;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.brainail.EverboxingSplashFlame.ui.views.dialogs.hardy.SplashFlameHardyDialogsCode.D_GENERATING_FLAME_PROGRESS;

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
public class FlamePropertiesFragment extends BaseFragment {
    @BindView (R.id.flame_it)
    protected Button mFlameIt;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_flame, container, false);
        ButterKnife.bind (this, view);
        return view;
    }

    @OnClick (R.id.flame_it)
    void flameIt () {
        SplashFlameHardyDialogs.generatingFlameDialog ().show (this);
        Task.callInBackground (new Callable<String> () {
            @Override
            public String call () throws Exception {
                final String filePath = warmUp ();
                return filePath;
            }
        }).continueWith (new Continuation<String, Object> () {
            @Override
            public Object then (Task<String> task) throws Exception {
                HardyDialogsHelper.dismissDialog (FlamePropertiesFragment.this, D_GENERATING_FLAME_PROGRESS);
                openPreview(task.getResult ());
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private String warmUp() {
        final File filePath = new File (
                Constants.APP_MEDIA_DIR_PATH,
                "fractal_" + System.currentTimeMillis () + ".jpeg");

        if (! filePath.getParentFile ().exists ()) {
            filePath.getParentFile ().mkdirs ();
        }

        final Point sizes = ToolPhone.realScreenSize (getContext ());
        ToolFractal.warmUp (filePath.getAbsolutePath (), Math.max (sizes.x, sizes.y), Math.min (sizes.x, sizes.y), 7);

        return filePath.getAbsolutePath ();
    }

    @UiThread
    private void openPreview(final String filePath) {
        // Use Navigator, Dagger
        final Intent previewIntent = new Intent (getActivity (), FlamePreviewActivity.class);
        previewIntent.putExtra ("flame_file_path", filePath);
        startActivity (previewIntent);
    }
}
