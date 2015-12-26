package org.brainail.EverboxingLexis.ui.drawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.SectionedDrawerActivity;
import org.brainail.EverboxingLexis.utils.tool.ToolColor;
import org.brainail.EverboxingLexis.utils.tool.ToolFragments;
import org.brainail.EverboxingLexis.utils.tool.ToolStrings;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

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
public class DrawerSection implements DrawerLayout.DrawerListener {

    // To save state
    public static abstract class ExtraKey {
        public static final String POSITION = "org.brainail.Everboxing.extra#drawer.section.position";
        public static final String COLOR = "org.brainail.Everboxing.extra#drawer.section.color";
        public static final String TITLE = "org.brainail.Everboxing.extra#drawer.section.title";
    }

    // Display (where?)
    public static enum LocationType {
        PRIMARY,
        HELP;
    }

    // Open (whom?)
    public static enum TargetType {

        FRAGMENT,
        INTENT,
        CLASS,
        CALLBACK;

        public boolean isInplace () {
            boolean inplace = this == FRAGMENT;
            inplace |= this == CALLBACK;
            return inplace;
        }

    }

    // Display (how?)
    public static enum LayoutType {

        BASE (R.layout.drawer_section_base),
        NORMAL (R.layout.drawer_section_normal);

        public final int layoutId;

        private LayoutType (final int layoutId) {
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
    private boolean mOpenTarget = false;

    // Visual stuff
    private String mTitle;
    private String mName;
    private int mNumberNotifications = 0;
    private int mNotificationsLimit = 99;
    private int mColor = Color.BLACK;
    private boolean mHasColor = false;

    // View holder
    private final DrawerSectionHolder mViewHolder;

    // Feedback for click events
    private DrawerSectionCallback mCallback = null;

    public DrawerSection (final Context context) {
        this (context, LayoutType.BASE);
    }

    public DrawerSection (final Context context, final LayoutType layoutType) {
        mViewHolder = DrawerSectionHolder.inflate (context, layoutType);
        mViewHolder.selfView.setOnClickListener (mInternalClickCallback);
    }

    // @package-local for controller.
    DrawerSection withPosition (final int position) {
        mPosition = position;
        return this;
    }

    // @package-local for controller.
    DrawerSection withController (final DrawerSectionsController controller) {
        mDrawerController = controller;
        return this;
    }

    // @package-local for controller.
    View selfView () {
        return mViewHolder.selfView;
    }

    public DrawerSection select (final boolean internalAction) {
        mViewHolder.selfView.setSelected (true);
        updateColors ();

        mDrawerController.selectSection (this);

        if (null != mCallback && !internalAction) {
            mCallback.onClick (this);
        }

        return this;
    }

    public void unselect () {
        mViewHolder.selfView.setSelected (false);
        resetColors ();

        mDrawerController.unselectSection (this);
    }

    public int getPosition () {
        return mPosition;
    }

    public DrawerSection withOnClickCallback (final DrawerSectionCallback callback) {
        mCallback = callback;
        return this;
    }

    public String getTitle () {
        return mTitle;
    }

    public DrawerSection withTitle (final String title) {
        mTitle = title;

        if (TextUtils.isEmpty (mName)) {
            mName = mTitle;
        }

        return this;
    }

    public String getName () {
        return mName;
    }

    public DrawerSection withName (final String name) {
        mName = name;
        mViewHolder.selfText.setText (name);
        return this;
    }

    public DrawerSection withIcon (final Drawable icon) {
        if (null != mViewHolder.selfIcon) {
            mViewHolder.selfIcon.setImageDrawable (icon);
        }

        updateTextAndIconColor ();
        return this;
    }

    public DrawerSection withIcon (final Bitmap icon) {
        if (null != mViewHolder.selfIcon) {
            mViewHolder.selfIcon.setImageBitmap (icon);
        }

        updateTextAndIconColor ();
        return this;
    }

    public DrawerSection withTarget (final DrawerSectionCallback target) {
        mTargetType = TargetType.CALLBACK;
        mTarget = target;
        return this;
    }

    public DrawerSection withTarget (final Fragment target) {
        mTargetType = TargetType.FRAGMENT;
        mTarget = target;
        return this;
    }

    public DrawerSection withTarget (final Intent target) {
        mTargetType = TargetType.INTENT;
        mTarget = target;
        return this;
    }

    public DrawerSection withTarget (final Class<? extends AppCompatActivity> target) {
        mTargetType = TargetType.CLASS;
        mTarget = target;
        return this;
    }

    public TargetType getTargetType () {
        return mTargetType;
    }

    public Object getTarget () {
        return mTarget;
    }

    public DrawerSection withLocationType (final LocationType type) {
        mLocationType = type;
        return this;
    }

    public LocationType getLocationType () {
        return mLocationType;
    }

    public DrawerSection withSectionColor (final Integer color) {
        mHasColor = null != color;
        mColor = null != color ? color : Color.BLACK;
        updateTextAndIconColor ();
        return this;
    }

    private void updateTextAndIconColor () {
        if (isSelected ()) {
            updateColors ();
        } else {
            resetColors ();
        }
    }

    public boolean hasColor () {
        return mHasColor;
    }

    public int getColor () {
        return mColor;
    }

    public DrawerSection withNotificationsLimit (final int notificationsLimit) {
        mNotificationsLimit = notificationsLimit;
        withNotifications (mNumberNotifications);
        return this;
    }

    public DrawerSection withNotifications (final int numberNotifications) {
        String notificationsText = String.valueOf (numberNotifications);
        if (numberNotifications < 1 || numberNotifications > mNotificationsLimit) {
            notificationsText = numberNotifications < 1 ? ToolStrings.EMPTY : mNotificationsLimit + ToolStrings.PLUS;
        }

        mViewHolder.selfNotifications.setText (notificationsText);
        mNumberNotifications = numberNotifications;

        return this;
    }

    public int getNumberNotifications () {
        return mNumberNotifications;
    }

    private boolean isSelected () {
        return mViewHolder.selfView.isSelected ();
    }

    void target () {
        mOpenTarget = true;
        ToolUI.toggleMenuDrawer (drawerLayout (), false);
    }

    void openTarget (final boolean internalAction) {
        mOpenTarget = false;

        // We don't want to be selected for targets which open a new window
        if (null != mTargetType && !mTargetType.isInplace ()) unselect ();

        if (TargetType.CALLBACK == mTargetType) {
            ((DrawerSectionCallback) mTarget).onTargetClick (this);
        } else if (TargetType.CLASS == mTargetType) {
            final Intent targetIntent = new Intent (scene (), (Class<?>) mTarget);
            scene ().startActivity (wrapIntentOnStart (targetIntent));
        } else if (TargetType.INTENT == mTargetType) {
            scene ().startActivity (wrapIntentOnStart ((Intent) mTarget));
        } else if (TargetType.FRAGMENT == mTargetType) {
            if (!internalAction || !ToolFragments.isPresented (scene (), (Fragment) mTarget)) {
                ToolFragments.openDrawerFragment (scene (), (Fragment) mTarget);
            }
        }
    }

    private Intent wrapIntentOnStart (final Intent intent) {
        final Intent wrappedIntent = new Intent (intent);
        wrappedIntent.putExtra (ExtraKey.COLOR, hasColor () ? (Integer) mColor : (Integer) null);
        wrappedIntent.putExtra (ExtraKey.TITLE, getTitle ());
        return wrappedIntent;
    }

    private SectionedDrawerActivity scene () {
        return mDrawerController.scene ();
    }

    private DrawerLayout drawerLayout () {
        return scene ().getDrawerLayout ();
    }

    private final View.OnClickListener mInternalClickCallback = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            select (false);
            target ();
        }
    };

    private void updateColors () {
        if (mHasColor) {
            mViewHolder.selfText.setTextColor (mColor);

            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.setColorFilter (mColor);
            }
        }
    }

    private void resetColors () {
        mViewHolder.selfText.setTextColor (ToolColor.by (R.color.menu_drawer_section_text_color));

        if (mHasColor) {
            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.setColorFilter (mColor);
            }
        } else {
            if (null != mViewHolder.selfIcon) {
                mViewHolder.selfIcon.clearColorFilter ();
            }
        }
    }

    @Override
    public void onDrawerSlide (View drawerView, float slideOffset) {
        // Do nothing
    }

    @Override
    public void onDrawerOpened (View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerStateChanged (int newState) {
        // Do nothing
    }

    @Override
    public void onDrawerClosed (View drawerView) {
        if (mOpenTarget) openTarget (false);
    }

}
