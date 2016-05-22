package org.brainail.EverboxingSplashFlame.utils.tool;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.activities.BaseActivity;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSection;
import org.brainail.EverboxingTools.utils.callable.Colorable;
import org.brainail.EverboxingTools.utils.callable.Titleable;
import org.brainail.EverboxingTools.utils.tool.ToolColor;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;
import org.brainail.EverboxingTools.utils.tool.ToolManifest;

import butterknife.ButterKnife;

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
public final class ToolToolbar {

    public static void updateToolbarTitle(final AppCompatActivity activity, final String title) {
        // No place
        if (null == activity) return;

        // Use presented
        String toolbarTitle = title;

        // Try to use title from fragment if necessary
        if (TextUtils.isEmpty(toolbarTitle)) {
            final Fragment fragment = ToolFragments.topFragment(activity);
            if (fragment instanceof Titleable) {
                final String fragmentTitle = ((Titleable) fragment).title();
                if (!TextUtils.isEmpty(fragmentTitle)) toolbarTitle = fragmentTitle;
            }
        }

        // Try to get from the activity's intent (perhaps it was started from the Drawer)
        if (TextUtils.isEmpty(toolbarTitle) && null != activity.getIntent()) {
            toolbarTitle = activity.getIntent().getStringExtra(DrawerSection.ExtraKey.TITLE);
        }

        // Check manifest
        if (TextUtils.isEmpty(toolbarTitle)) {
            toolbarTitle = ToolManifest.activityLabel(activity);
        }

        // Try to use app name as the last resort
        if (TextUtils.isEmpty(toolbarTitle)) {
            toolbarTitle = ToolResources.string(R.string.app_name);
        }

        // Set title for toolbar
        final ActionBar toolbar = activity.getSupportActionBar();
        if (null != toolbar) toolbar.setTitle(toolbarTitle);
    }

    public static void updateToolbarColor(final AppCompatActivity activity, final Integer color) {
        // No place
        if (null == activity) return;

        // Use presented
        Integer toolbarColor = color;

        // Try to use color from fragment if necessary
        if (null == toolbarColor) {
            final Fragment fragment = ToolFragments.topFragment(activity);
            if (fragment instanceof Colorable) {
                final Integer fragmentColor = ((Colorable) fragment).color();
                if (null != fragmentColor) toolbarColor = fragmentColor;
            }
        }

        // Try to get from the activity's intent (perhaps it was started from the Drawer)
        if (null == toolbarColor && null != activity.getIntent()) {
            toolbarColor = (Integer) activity.getIntent().getSerializableExtra(DrawerSection.ExtraKey.COLOR);
        }

        // Try to use app color as the last resort
        final int primaryColor = ToolResources.retrievePrimaryColor(activity);
        final int primaryDarkColor = ToolResources.retrievePrimaryDarkColor(activity);
        if (null == toolbarColor) {
            toolbarColor = primaryColor;
        }

        // Set color for toolbar & status bar
        final Toolbar toolbar = ((BaseActivity) activity).getPrimaryToolbar();
        if (null != toolbar) {
            final View appContentWindow = ButterKnife.findById(activity, R.id.app_content);

            // Similar to getWindow() to set background color for status bar
            if (null != appContentWindow) {
                final int windowColor = (toolbarColor == primaryColor)
                        ? primaryDarkColor : ToolColor.darkenColor(toolbarColor);

                //noinspection WrongConstant,ResourceAsColor
                appContentWindow.setBackgroundColor(windowColor);
            }

            toolbar.setBackgroundColor(toolbarColor);
        }
    }

}
