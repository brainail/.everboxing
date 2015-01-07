package org.brainail.Everboxing.ui.drawer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolColor;
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

    // Display (where?)
    public static enum LocationType {
        PRIMARY,
        HELP
    }

    // Open (whom?)
    public static enum TargetType {
        FRAGMENT,
        INTENT,
        CLASS
    }

    // Display (how?)
    public static enum LayoutType {

        BASE(R.layout.drawer_section_base),
        NORMAL(R.layout.drawer_section_normal);

        public final int layoutId;

        private LayoutType(final int layoutId) {
            this.layoutId = layoutId;
        }

    }

    // Controller for drawer
    private DrawerSectionsController mDrawerController = null;

    // Position index (negative for help sections)
    private int mPosition = Integer.MIN_VALUE;
    private LocationType mLocationType = LocationType.PRIMARY;

    // Target
    private TargetType mTargetType = null;
    private Object mTarget = null;

    // Visual stuff
    private String mTitle;
    private int mNumberNotifications = 0;
    private int mNotificationsLimit = 99;
    private int mColor = Color.BLACK;
    private boolean mHasColor = false;

    // View holder
    private final DrawerSectionHolder mViewHolder;

    // Feedback for click events
    private DrawerSectionCallback mCallback = null;

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
        updateColors();

        mDrawerController.selectSection(this);

        if (null != mCallback) {
            mCallback.onClick(this);
        }
    }

    public void unselect() {
        mViewHolder.selfView.setSelected(false);
        resetColors();

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
        }

        return this;
    }

    public DrawerSection withIcon(final Bitmap icon) {
        if (null != mViewHolder.selfIcon) {
            mViewHolder.selfIcon.setImageBitmap(icon);
        }

        return this;
    }

    public DrawerSection withTarget(final Fragment target) {
        mTargetType = TargetType.FRAGMENT;
        mTarget = target;
        return this;
    }

    public DrawerSection withTarget(final Intent target) {
        mTargetType = TargetType.INTENT;
        mTarget = target;
        return this;
    }

    public DrawerSection withTarget(final Class<? extends Activity> target) {
        mTargetType = TargetType.CLASS;
        mTarget = target;
        return this;
    }

    public TargetType getTargetType() {
        return mTargetType;
    }

    public Object getTarget() {
        return mTarget;
    }

    public DrawerSection withLocationType(final LocationType type) {
        mLocationType = type;
        return this;
    }

    public LocationType getLocationType() {
        return mLocationType;
    }

    public DrawerSection withSectionColor(final Integer color) {
        mHasColor = null != color;
        mColor = null != color ? color : Color.BLACK;

        if (isSelected()) {
            if (null != color) {
                updateColors();
            } else {
                resetColors();
            }
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
        withNotifications(mNumberNotifications);
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

    private void target() {
        if (mTarget instanceof Class<?>) {
            final Intent targetIntent = new Intent(mDrawerController.scene(), (Class<?>) mTarget);
            mDrawerController.scene().startActivity(targetIntent);
        } else if (mTarget instanceof Intent) {
            mDrawerController.scene().startActivity((Intent) mTarget);
        } else if (mTarget instanceof Fragment) {
            // FIXME#brainail: Handle fragment
        }
    }

    private View.OnClickListener mInternalClickCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            select();
            target();
        }
    };

    private void updateColors() {
        if (mHasColor) {
            mViewHolder.selfText.setTextColor(mColor);

            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.setColorFilter(mColor);
            }
        }
    }

    private void resetColors() {
        if (mHasColor) {
            mViewHolder.selfText.setTextColor(ToolColor.by(R.color.menu_drawer_section_text_color));

            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.clearColorFilter();
            }
        }
    }

}
