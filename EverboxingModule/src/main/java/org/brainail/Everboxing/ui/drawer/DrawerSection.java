package org.brainail.Everboxing.ui.drawer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolStrings;

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
public class DrawerSection {

    // Where
    public static enum LocationType {
        PRIMARY,
        HELP
    }

    // Whom
    public static enum TargetType {
        FRAGMENT,
        INTENT
    }

    // How
    public static enum LayoutType {

        BASE(R.layout.drawer_section_base),
        NORMAL(R.layout.drawer_section_normal);

        public final int layoutId;

        private LayoutType(final int layoutId) {
            this.layoutId = layoutId;
        }

    }

    private DrawerSectionsController mDrawerController = null;

    private int mPosition = Integer.MIN_VALUE;
    private TargetType mTargetType = null;
    private LocationType mLocationType = LocationType.PRIMARY;

    private Fragment mTargetFragment = null;
    private Intent mTargetIntent = null;

    private int mNumberNotifications = 0;
    private int mNotificationsLimit = 99;
    private String mTitle;

    private final DrawerSectionHolder mViewHolder;

    private DrawerSectionCallback mCallback = null;
    private boolean mHasColor = false;
    private int mColor = Color.BLACK;

    public DrawerSection(final Context context) {
        this(context, LayoutType.BASE);
    }

    public DrawerSection(final Context context, final LayoutType layoutType) {
        mViewHolder = DrawerSectionHolder.inflate(context, layoutType);
        mViewHolder.selfView.setOnClickListener(mInternalClickCallback);
    }

    // @package-local for controller.
    DrawerSection withPosition(final int position) {
        mPosition = position;
        return this;
    }

    // @package-local for controller.
    DrawerSection withController(final DrawerSectionsController controller) {
        mDrawerController = controller;
        return this;
    }

    // @package-local for controller.
    View selfView() {
        return mViewHolder.selfView;
    }

    public void select() {
        mViewHolder.selfView.setSelected(true);
        updateIcon(mColor, 1.0f);

        mDrawerController.selectSection(this);

        if (null != mCallback) {
            mCallback.onClick(this);
        }
    }

    public void unselect() {
        mViewHolder.selfView.setSelected(false);
        updateIcon(Color.BLACK, 0.5f);

        mDrawerController.unselectSection(this);
    }

    public int getPosition() {
        return mPosition;
    }

    public DrawerSection withOnClickCallback(final DrawerSectionCallback callback) {
        mCallback = callback;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public DrawerSection withTitle(final String title) {
        mTitle = title;
        mViewHolder.selfText.setText(title);
        return this;
    }

    public DrawerSection withIcon(final Drawable icon) {
        if (null != mViewHolder.selfIcon) {
            mViewHolder.selfIcon.setImageDrawable(icon);
            mViewHolder.selfIcon.setColorFilter(mColor);
        }

        return this;
    }

    public DrawerSection withIcon(final Bitmap icon) {
        if (null != mViewHolder.selfIcon) {
            mViewHolder.selfIcon.setImageBitmap(icon);
            mViewHolder.selfIcon.setColorFilter(mColor);
        }

        return this;
    }

    public DrawerSection withTarget(final Fragment target) {
        mTargetType = TargetType.FRAGMENT;
        mTargetFragment = target;
        return this;
    }

    public DrawerSection withTarget(final Intent target) {
        mTargetType = TargetType.INTENT;
        mTargetIntent = target;
        return this;
    }

    public TargetType getTargetType() {
        return mTargetType;
    }

    public Fragment getTargetFragment() {
        return mTargetFragment;
    }

    public Intent getTargetIntent() {
        return mTargetIntent;
    }

    public DrawerSection withLocationType(final LocationType type) {
        mLocationType = type;
        return this;
    }

    public LocationType getLocationType() {
        return mLocationType;
    }

    public DrawerSection withSectionColor(int color) {
        mHasColor = true;
        mColor = color;

        if (isSelected()) {
            updateIcon(mColor, 1f);
        }

        return this;
    }

    public boolean hasColor() {
        return mHasColor;
    }

    public int getColor() {
        return mColor;
    }

    public DrawerSection withNotificationsLimit(final int notificationsLimit) {
        mNotificationsLimit = notificationsLimit;
        return this;
    }

    public DrawerSection withNotifications(final int numberNotifications) {
        String notificationsText = String.valueOf(numberNotifications);
        if (numberNotifications < 1 || numberNotifications > mNotificationsLimit) {
            notificationsText = numberNotifications < 1 ? ToolStrings.EMPTY : mNotificationsLimit + ToolStrings.PLUS;
        }

        mViewHolder.selfNotifications.setText(notificationsText);
        mNumberNotifications = numberNotifications;

        return this;
    }

    public int getNumberNotifications() {
        return mNumberNotifications;
    }

    private boolean isSelected() {
        return mViewHolder.selfView.isSelected();
    }

    private View.OnClickListener mInternalClickCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            select();
        }
    };

    private void updateIcon(final int color, final float alpha) {
        if (mHasColor) {
            mViewHolder.selfText.setTextColor(color);

            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.setColorFilter(color);
                mViewHolder.selfIcon.setAlpha(alpha);
            }
        }
    }

}
