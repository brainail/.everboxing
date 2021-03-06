package org.brainail.EverboxingSplashFlame._app.features.flame_preview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BaseFragment;
import org.brainail.EverboxingTools.ui.views.PhotoView;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.brainail.EverboxingTools.utils.tool.ToolFile;

import butterknife.BindView;

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
public class FlamePreviewFragment extends BaseFragment {

    private String mFilePath;

    public static final class Extras {
        public static final String FILE_PATH = "org.brainail.Everboxing.extra#preview.flame.file.path";
    }

    @BindView (R.id.preview_flame)
    protected PhotoView mPreviewFlame;

    private SimpleTarget<Bitmap> mPreviewFlameTarget;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        parseIntentExtras ();
        setHasOptionsMenu (true);
    }

    private void parseIntentExtras() {
        final Intent intent = getActivity ().getIntent ();
        mFilePath = intent.getStringExtra (Extras.FILE_PATH);
        PooLogger.info ("parseIntentExtras: filePath = ?", mFilePath);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_preview_flame, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.set_as_wallpaper:
                mNavigator.openSetAsWallpaperChooser (mFilePath).start ();
                return true;
            case R.id.delete_file:
                ToolFile.deleteFile (mFilePath);
                getActivity ().finish ();
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_preview_flame, container, false);
        return bind (view);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        loadPreview();
    }

    private void loadPreview() {
        mPreviewFlame.setZoom (1f);
        mPreviewFlameTarget = Glide.with (this)
                .load (mFilePath)
                .asBitmap ()
                .diskCacheStrategy (DiskCacheStrategy.ALL)
                .into (new SimpleTarget<Bitmap> () {
                    @Override
                    public void onResourceReady (Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mPreviewFlame.setImageBitmap (resource);
                    }
                });
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();

        if (null != mPreviewFlameTarget) {
            Glide.clear (mPreviewFlameTarget);
        }
    }

}
