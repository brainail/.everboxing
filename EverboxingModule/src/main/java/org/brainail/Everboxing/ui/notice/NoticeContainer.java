package org.brainail.Everboxing.ui.notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolResources;
import org.brainail.Everboxing.utils.tool.ToolUI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.brainail.Everboxing.ui.notice.NoticeBar.OnVisibilityCallback;
import static org.brainail.Everboxing.ui.notice.NoticeBar.Style;

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
public class NoticeContainer extends FrameLayout {

    private static final class ShowHideDuration {
        private static final int ANIMATION_DURATION = 300;
        private static final int PLACE_OVER_ALL_DURATION = 300;
    }

    private Animation mOutAnimation;
    private Animation mInAnimation;

    private final Queue<NoticeHolder> mNotices = new LinkedList<NoticeHolder>();

    public NoticeContainer(final Context context) {
        super(context);
        init();
    }

    public NoticeContainer(final Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    NoticeContainer(final ViewGroup container) {
        super(container.getContext());
        addOver(container);
        init();
    }

    // Add dynamically
    private void addOver(final ViewGroup container) {
        container.addView(this, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setVisibility(View.GONE);
        setId(R.id.notice_container);
    }

    private void init() {
        // Init animations
        mInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.notice_slide_in_animation);
        mInAnimation.setFillAfter(true);
        mOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.notice_slide_out_animation);
        mOutAnimation.setFillAfter(true);
        mOutAnimation.setDuration(ShowHideDuration.ANIMATION_DURATION);
        mOutAnimation.setAnimationListener(mOutAnimationCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Container for notices is detached from a window
        // then clear all notices
        mInAnimation.cancel();
        mOutAnimation.cancel();
        removeCallbacks(mHideRunnable);
        mNotices.clear();
    }

    public boolean isEmpty() {
        return mNotices.isEmpty();
    }

    public boolean isSingle() {
        return (1 == mNotices.size());
    }

    public Notice peekNotice() {
        return mNotices.peek().noticeData;
    }

    public Notice pollNotice() {
        return mNotices.poll().noticeData;
    }

    public void clearNotices(final boolean animate) {
        mNotices.clear();
        if (animate) {
            mHideRunnable.run();
        }
    }

    public boolean isShowing() {
        return !mNotices.isEmpty();
    }

    public void hide() {
        removeCallbacks(mHideRunnable);
        mHideRunnable.run();
    }

    private void hideNotice() {
        undoPlaceOverAll();
        removeAllViews();

        if (!isEmpty()) {
            sendOnHide(mNotices.poll());
        }

        if (!isEmpty()) {
            showNotice(mNotices.peek());
        } else {
            setVisibility(View.GONE);
        }
    }

    public void showNotice(
            final Notice notice,
            final View noticeView,
            final OnVisibilityCallback callback) {

        showNotice(notice, noticeView, callback, false);
    }

    public void showNotice(
            final Notice notice,
            final View noticeView,
            final OnVisibilityCallback callback,
            final boolean immediately) {

        // Remove current before show
        if (null != noticeView.getParent() && this != noticeView.getParent()) {
            ((ViewGroup) noticeView.getParent()).removeView(noticeView);
        }

        // Add to queue
        final NoticeHolder noticeHolder = new NoticeHolder(notice, noticeView, callback);
        mNotices.offer(noticeHolder);

        // Show only if we have a single notice right now
        if (isSingle()) {
            showNotice(noticeHolder, immediately);
        }
    }

    private void showNotice(final NoticeHolder noticeHolder) {
        showNotice(noticeHolder, false);
    }

    @SuppressLint("RtlHardcoded")
    private void showNotice(final NoticeHolder noticeHolder, final boolean showImmediately) {
        // Make visible
        setVisibility(View.VISIBLE);

        // Trigger callback
        sendOnShow(noticeHolder);

        // Place over
        addView(noticeHolder.notice, getLayoutParams(noticeHolder.notice, noticeHolder.noticeData.style));
        doPlaceOverAll();

        // Fill configuration
        noticeHolder.body.setText(noticeHolder.noticeData.message);
        noticeHolder.action.setTextColor(getActionTextColor(noticeHolder.noticeData.style));
        if (!TextUtils.isEmpty(noticeHolder.noticeData.action)) {
            noticeHolder.action.setVisibility(View.VISIBLE);
            noticeHolder.action.setText(noticeHolder.noticeData.action.toUpperCase());
        } else {
            noticeHolder.action.setVisibility(View.GONE);
        }

        // Show through the animation
        mInAnimation.setDuration(showImmediately ? 0 : ShowHideDuration.ANIMATION_DURATION);
        startAnimation(mInAnimation);

        // Post the action to hide if it isn't permanently
        if (noticeHolder.noticeData.duration > 0) {
            postDelayed(mHideRunnable, noticeHolder.noticeData.duration);
        }
    }

    // Sometimes something (fragments, ..) overlays the notice bar
    private void doPlaceOverAll() {
        postDelayed(mPlaceOverAllAction, ShowHideDuration.PLACE_OVER_ALL_DURATION);
    }

    private void undoPlaceOverAll() {
        removeCallbacks(mPlaceOverAllAction);
    }

    private Runnable mPlaceOverAllAction = new Runnable() {
        @Override
        public void run() {
            bringToFront();
            requestLayout();
            invalidate();
        }
    };

    // Returns text color by style
    private ColorStateList getActionTextColor(final long style) {
        if ((style & Style.ALERT) > 0) return getResources().getColorStateList(R.color.notice_action_color_red);
        if ((style & Style.CONFIRM) > 0) return getResources().getColorStateList(R.color.notice_action_color_green);
        if ((style & Style.INFO) > 0) return getResources().getColorStateList(R.color.notice_action_color_yellow);
        return getResources().getColorStateList(R.color.notice_action_color_default);
    }

    // Returns gravity by style
    private int getGravity(final long style) {
        if ((style & Style.DEFAULT_TOP) > 0) return Gravity.TOP;
        return Gravity.BOTTOM;
    }

    // Returns layout params by style
    private FrameLayout.LayoutParams getLayoutParams(final View view, final long style) {
        final FrameLayout.LayoutParams layoutParams = new LayoutParams(view.getLayoutParams());

        // Set gravity by style
        layoutParams.gravity = Gravity.START | getGravity(style);

        // Check navigation bar
        if (ToolUI.hasHideNavigationFlag((ViewGroup) getParent())) {
            // Only for bottom
            if (Gravity.BOTTOM == layoutParams.gravity) {
                // FIXME#brainail
                layoutParams.bottomMargin += ToolResources.computeNavigationBarHeight(getContext());
            }
        }

        return layoutParams;
    }

    private void sendOnHide(final NoticeHolder noticeHolder) {
        if (null != noticeHolder.visibilityCallback) {
            noticeHolder.visibilityCallback.onHide(mNotices.size());
        }
    }

    private void sendOnShow(final NoticeHolder noticeHolder) {
        if (null != noticeHolder.visibilityCallback) {
            noticeHolder.visibilityCallback.onShow(mNotices.size());
        }
    }

    private final Animation.AnimationListener mOutAnimationCallback = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            hideNotice();
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (View.VISIBLE == getVisibility()) {
                // Simulate onAnimationEnd() callback due to it isn't called sometimes
                // I don't want to override onAnimationEnd() for the whole view
                self().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearAnimation();
                        hideNotice();
                    }
                }, mOutAnimation.getDuration());

                startAnimation(mOutAnimation);
            }
        }
    };

    private ViewGroup self() {
        return this;
    }

    // Restores state from bundle
    public void restoreState(final Bundle state, final View v) {
        ArrayList<Parcelable> notices = state.getParcelableArrayList(Notice.EXTRA_NOTICES);
        boolean showImmediately = true;

        for (final Parcelable notice : notices) {
            showNotice((Notice) notice, v, null, showImmediately);
            showImmediately = false;
        }
    }

    // Stores the current state into bundle
    public Bundle saveState() {
        final ArrayList<Notice> notices = new ArrayList<Notice>();
        final Bundle outState = new Bundle();

        for (final NoticeHolder holder : mNotices) {
            notices.add(holder.noticeData);
        }

        if (!notices.isEmpty()) {
            outState.putParcelableArrayList(Notice.EXTRA_NOTICES, notices);
        }

        return outState;
    }

    private static class NoticeHolder {

        final View notice;
        final TextView body;
        final TextView action;
        final Notice noticeData;
        final OnVisibilityCallback visibilityCallback;

        private NoticeHolder(final Notice noticeData, final View notice, final OnVisibilityCallback visibilityCallback) {
            this.notice = notice;
            this.noticeData = noticeData;
            this.visibilityCallback = visibilityCallback;
            this.action = (TextView) notice.findViewById(R.id.notice_action);
            this.body = (TextView) notice.findViewById(R.id.notice_message);
        }

    }

}