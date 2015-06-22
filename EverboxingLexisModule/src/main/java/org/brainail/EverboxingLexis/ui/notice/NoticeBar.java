package org.brainail.EverboxingLexis.ui.notice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.brainail.EverboxingLexis.JApplication;
import org.brainail.EverboxingLexis.R;

import butterknife.ButterKnife;

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
public class NoticeBar {

    public static final class Duration {
        public static final long LONG = 5000;
        public static final long MEDIUM = 3500;
        public static final long SHORT = 2000;
        public static final long PERMANENT = 0;
    }

    public static final class Style {
        // At the bottom
        public static final long DEFAULT = 0;
        // Red action color
        public static final long ALERT = 1 << 1;
        // Green action color
        public static final long CONFIRM = 1 << 2;
        // Yellow action color
        public static final long INFO = 1 << 3;
    }

    NoticeOnSceneController mNoticesController;
    private NoticeContainer mContainer;
    private View mNoticeView;

    public interface OnActionCallback {
        public void onAction(final String token);
    }

    public interface OnVisibilityCallback {
        public void onShow(final String token, final int activeSize);
        public void onMute(final String token, final int activeSize);
    }

    public NoticeBar(final NoticeOnSceneController controller, final Activity activity) {
        mNoticesController = controller;

        final ViewGroup container = ButterKnife.findById(activity, android.R.id.content);
        final View notice = activity.getLayoutInflater().inflate(R.layout.notice, container, false);

        init(container, notice);
    }

    public NoticeBar(final NoticeOnSceneController controller, final Context context, final View root) {
        mNoticesController = controller;

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notice_container, (ViewGroup) root);
        final View noticeView = inflater.inflate(R.layout.notice, (ViewGroup) root, false);

        init((ViewGroup) root, noticeView);
    }

    private void init(final ViewGroup container, final View noticeView) {
        mNoticeView = noticeView;

        mContainer = ButterKnife.findById(container, R.id.notice_container);
        if (null == mContainer) mContainer = new NoticeContainer(container);
        mContainer.attachController(mNoticesController);

        final TextView actionText = ButterKnife.findById(noticeView, R.id.notice_action);
        actionText.setOnClickListener(mInternalOnActionCallback);
    }

    public static class Builder {

        private NoticeBar noticeBar;

        String body = null;
        String action = null;
        String token = null;
        long style = Style.DEFAULT;
        long duration = Duration.MEDIUM;

        OnActionCallback actionCallback;
        OnVisibilityCallback visibilityCallback;

        public Builder() {}

        // @package-local for controller
        Builder(final NoticeOnSceneController controller, final Activity activity) {
            noticeBar = new NoticeBar(controller, activity);
        }

        // @package-local for controller
        Builder(final NoticeOnSceneController controller, final Context context, final View root) {
            noticeBar = new NoticeBar(controller, context, root);
        }

        public Builder withText(final String textProvider) {
            body = textProvider;
            return this;
        }

        public Builder withText(final int resId) {
            body = JApplication.appContext().getString(resId);
            return this;
        }

        public Builder withActionText(final String textProvider) {
            action = textProvider;
            return this;
        }

        public Builder withActionText(final int resId) {
            action = JApplication.appContext().getString(resId);
            return this;
        }

        public Builder withStyle(final long styleMask) {
            style = styleMask;
            return this;
        }

        public Builder withToken(final String tokenProvider) {
            token = tokenProvider;
            return this;
        }

        public Builder withDuration(final long durationMillis) {
            duration = durationMillis;
            return this;
        }

        public Builder withCallback(final OnActionCallback callback) {
            actionCallback = callback;
            return this;
        }

        public Builder withCallback(final OnVisibilityCallback callback) {
            visibilityCallback = callback;
            return this;
        }

        // @package-local for controller
        Builder inflateFrom(final Builder provider) {
            if (null == provider) {
                // No provider. It seems like existence of some saved state.
                return this;
            }

            // Inflate params
            style = provider.style;
            token = provider.token;
            body = provider.body;
            duration = provider.duration;
            action = provider.action;

            actionCallback = provider.actionCallback;
            visibilityCallback = provider.visibilityCallback;

            return this;
        }

        // @package-local for controller
        NoticeBar show(final Bundle savedState) {
            // If this is non-empty builder
            if (null != noticeBar) {
                // Restore notices or show the built notice
                if (null != savedState) {
                    noticeBar.onRestoreInstanceState(savedState);
                } else {
                    noticeBar.show(new Notice(this));
                }
            }

            return noticeBar;
        }

    }

    private void show(final Notice notice) {
        mContainer.showNotice(notice, mNoticeView);
    }

    public int getHeight() {
        mNoticeView.measure(
                View.MeasureSpec.makeMeasureSpec(mNoticeView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mNoticeView.getHeight(), View.MeasureSpec.AT_MOST)
        );

        return mNoticeView.getMeasuredHeight();
    }

    public View getView() {
        return mNoticeView;
    }

    private final View.OnClickListener mInternalOnActionCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContainer.sendOnAction();
            mContainer.hide();
        }
    };

    private void muteAll(final boolean mutePresent) {
        mContainer.clearNotices(mutePresent);
    }

    public void muteAll() {
        muteAll(true);
    }

    public void muteOthers() {
        muteAll(false);
    }

    // See android.app.AppCompatActivity#onRestoreInstanceState(android.os.Bundle)
    public void onRestoreInstanceState(final Bundle state) {
        mContainer.restoreState(state, mNoticeView);
    }

    // See android.app.AppCompatActivity#onSaveInstanceState(android.os.Bundle)
    public Bundle onSaveInstanceState() {
        return mContainer.saveState();
    }

    static Bundle retrieveSavedState(final Bundle savedState) {
        Bundle noticesState = null;

        // Try to grab only our states
        if (null != savedState && null != savedState.get(Notice.EXTRA_NOTICES)) {
            noticesState = new Bundle();
            noticesState.putParcelableArrayList(
                Notice.EXTRA_NOTICES,
                savedState.getParcelableArrayList(Notice.EXTRA_NOTICES)
            );
        }

        return noticesState;
    }

}
