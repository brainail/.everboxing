package org.brainail.EverboxingSplashFlame.utils.tool;

import android.support.annotation.AttrRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingTools.utils.callable.Colorable;
import org.brainail.EverboxingTools.utils.callable.Titleable;
import org.brainail.EverboxingTools.utils.tool.ToolColor;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;
import org.brainail.EverboxingTools.utils.tool.ToolManifest;

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
public final class ToolToolbar {

    private final @Nullable AppCompatActivity mScene;
    private final @Nullable String mTitleIntentExtra;
    private final @Nullable String mColorIntentExtra;
    private final @Nullable String mTitleCallback;
    private final @AttrRes int mToolbarCustomStyle;
    private final @IdRes int mAppContentId;

    public ToolToolbar (
            final @Nullable AppCompatActivity scene,
            final @Nullable String titleIntentExtra,
            final @Nullable String colorIntentExtra,
            final @AttrRes int toolbarCustomStyle,
            final @IdRes int appContentId,
            final @StringRes int titleFallback) {

        this(
                scene,
                titleIntentExtra,
                colorIntentExtra,
                toolbarCustomStyle,
                appContentId,
                ToolResources.string (scene, titleFallback));
    }

    public ToolToolbar (
            final @Nullable AppCompatActivity scene,
            final @Nullable String titleIntentExtra,
            final @Nullable String colorIntentExtra,
            final @AttrRes int toolbarCustomStyle,
            final @IdRes int appContentId,
            final @Nullable String titleFallback) {

        mScene = scene;
        mTitleIntentExtra = titleIntentExtra;
        mColorIntentExtra = colorIntentExtra;
        mToolbarCustomStyle = toolbarCustomStyle;
        mAppContentId = appContentId;
        mTitleCallback = titleFallback;
    }

    public void updateToolbarTitle (final String presentedTitle) {
        if (null == mScene) {
            // No place
            return;
        }

        // Use presented
        String toolbarTitle = presentedTitle;

        // Try to use title from fragment if necessary
        if (TextUtils.isEmpty (toolbarTitle)) {
            final Fragment fragment = ToolFragments.topFragment (mScene);
            if (fragment instanceof Titleable) {
                final String fragmentTitle = ((Titleable) fragment).title ();
                if (!TextUtils.isEmpty (fragmentTitle)) {
                    toolbarTitle = fragmentTitle;
                }
            }
        }

        // Try to get from the activity's intent (perhaps it was started from the Drawer)
        if (TextUtils.isEmpty (toolbarTitle) && null != mScene.getIntent ()) {
            toolbarTitle = mScene.getIntent ().getStringExtra (mTitleIntentExtra);
        }

        // Check manifest
        if (TextUtils.isEmpty (toolbarTitle)) {
            toolbarTitle = ToolManifest.activityLabel (mScene);
        }

        // Try to use app name as the last resort
        if (TextUtils.isEmpty (toolbarTitle)) {
            toolbarTitle = mTitleCallback;
        }

        // Set title for toolbar
        final ActionBar toolbar = mScene.getSupportActionBar ();
        if (null != toolbar) {
            toolbar.setTitle (toolbarTitle);
        }
    }

    public void updateToolbarColor (final Integer presentedColor) {
        if (null == mScene) {
            // No place
            return;
        }

        // Use presented
        Integer toolbarColor = presentedColor;

        // Try to use color from fragment if necessary
        if (null == toolbarColor) {
            final Fragment fragment = ToolFragments.topFragment (mScene);
            if (fragment instanceof Colorable) {
                final Integer fragmentColor = ((Colorable) fragment).color ();
                if (null != fragmentColor) {
                    toolbarColor = fragmentColor;
                }
            }
        }

        // Try to get from the activity's intent (perhaps it was started from the Drawer)
        if (null == toolbarColor && null != mScene.getIntent ()) {
            toolbarColor = (Integer) mScene.getIntent ().getSerializableExtra (mColorIntentExtra);
        }

        // Try to get color from custom toolbar theme's background
        if (null == toolbarColor) {
            final Integer customToolbarColor
                    = ToolResources.retrieveCustomToolbarThemeColor (mScene, mToolbarCustomStyle);
            if (null != customToolbarColor) {
                toolbarColor = customToolbarColor;
            }
        }

        // Try to use app color as the last resort
        final int primaryColor = ToolResources.retrievePrimaryColor (mScene);
        final int primaryDarkColor = ToolResources.retrievePrimaryDarkColor (mScene);
        if (null == toolbarColor) {
            toolbarColor = primaryColor;
        }

        // Set color for toolbar & status bar
        final Toolbar toolbar = ((BaseActivity) mScene).getPrimaryToolbar ();
        if (null != toolbar) {
            final View appContentWindow = mScene.findViewById (mAppContentId);

            // Similar to getWindow() to set background color for status bar
            if (null != appContentWindow) {
                final int windowColor = (toolbarColor == primaryColor)
                        ? primaryDarkColor : ToolColor.darkenColor (toolbarColor);

                // noinspection WrongConstant,ResourceAsColor
                appContentWindow.setBackgroundColor (windowColor);
            }

            toolbar.setBackgroundColor (toolbarColor);
        }
    }

}
