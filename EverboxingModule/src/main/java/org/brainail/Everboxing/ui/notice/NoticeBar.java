package org.brainail.Everboxing.ui.notice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;

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
        // At the top (implementation isn't completed due to guidelines)
        static final long DEFAULT_TOP = 1 << 0;
        // Red action color
        public static final long ALERT = 1 << 1;
        // Green action color
        public static final long CONFIRM = 1 << 1;
        // Yellow action color
        public static final long INFO = 1 << 1;
    }

    private View mNotice;
    private TextView mText;
    private TextView mActionText;
    private NoticeContainer mContainer;

    private OnActionCallback mActionCallback;
    private OnVisibilityCallback mVisibilityCallback;

    public interface OnActionCallback {
        public void onAction(final Parcelable token);
    }

    public interface OnVisibilityCallback {
        public void onShow(final int size);
        public void onHide(final int size);
    }

    public NoticeBar(final Activity activity) {
        final ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        final View notice = activity.getLayoutInflater().inflate(R.layout.notice, container, false);
        init(container, notice);
    }

    public NoticeBar(final Context context, final View root) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notice_container, (ViewGroup) root);
        final View notice = inflater.inflate(R.layout.notice, (ViewGroup) root, false);
        init((ViewGroup) root, notice);
    }

    private void init(final ViewGroup container, final View notice) {
        mContainer = (NoticeContainer) container.findViewById(R.id.notice_container);
        if (null == mContainer) {
            mContainer = new NoticeContainer(container);
        }

        mNotice = notice;
        mText = (TextView) notice.findViewById(R.id.notice_message);
        mActionText = (TextView) notice.findViewById(R.id.notice_action);
        mActionText.setOnClickListener(mInternalOnActionCallback);
    }

    public static class Builder {

        private NoticeBar noticeBar;
        String message;
        String actionMessage;
        long style = Style.DEFAULT;
        Parcelable token;
        long duration = Duration.MEDIUM;

        private OnActionCallback actionCallback;
        private OnVisibilityCallback visibilityCallback;

        public Builder() {}

        // @package-local for controller
        Builder(final Activity activity) {
            noticeBar = new NoticeBar(activity);
        }

        // @package-local for controller
        Builder(final Context context, final View root) {
            noticeBar = new NoticeBar(context, root);
        }

        public Builder withText(final String textProvider) {
            message = textProvider;
            return this;
        }

        public Builder withText(final int resId) {
            message = JApplication.appContext().getString(resId);
            return this;
        }

        public Builder withActionText(final String textProvider) {
            actionMessage = textProvider;
            return this;
        }

        public Builder withActionText(final int resId) {
            actionMessage = JApplication.appContext().getString(resId);
            return this;
        }

        public Builder withStyle(final long styleMask) {
            style = styleMask;
            return this;
        }

        public Builder withToken(final Parcelable tokenProvider) {
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
            message = provider.message;
            duration = provider.duration;
            actionMessage = provider.actionMessage;
            actionCallback = provider.actionCallback;
            visibilityCallback = provider.visibilityCallback;

            return this;
        }

        // @package-local for controller
        NoticeBar show(final Bundle savedState) {
            // If this is non-empty builder
            if (null != noticeBar) {

                // Keep callback for actions
                if (null != actionCallback) {
                    noticeBar.withCallback(actionCallback);
                }

                // Keep callback for visibility
                if (null != visibilityCallback) {
                    noticeBar.withCallback(visibilityCallback);
                }

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
        mContainer.showNotice(notice, mNotice, mVisibilityCallback);
    }

    public int getHeight() {
        mNotice.measure(
                View.MeasureSpec.makeMeasureSpec(mNotice.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mNotice.getHeight(), View.MeasureSpec.AT_MOST)
        );

        return mNotice.getMeasuredHeight();
    }

    public View getView() {
        return mNotice;
    }

    private final View.OnClickListener mInternalOnActionCallback = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mActionCallback && mContainer.isShowing()) {
                mActionCallback.onAction(mContainer.peekNotice().token);
            }

            mContainer.hide();
        }

    };

    public NoticeBar withCallback(final OnActionCallback callback) {
        mActionCallback = callback;
        return this;
    }

    public NoticeBar withCallback(final OnVisibilityCallback callback) {
        mVisibilityCallback = callback;
        return this;
    }

    private void clear(final boolean animate) {
        mContainer.clearNotices(animate);
    }

    public void clear() {
        clear(true);
    }

    // See android.app.Activity#onRestoreInstanceState(android.os.Bundle)
    public void onRestoreInstanceState(final Bundle state) {
        mContainer.restoreState(state, mNotice);
    }

    // See android.app.Activity#onSaveInstanceState(android.os.Bundle)
    public Bundle onSaveInstanceState() {
        return mContainer.saveState();
    }

    static Bundle retrieveSavedState(final Bundle savedState) {
        Bundle noticesState = null;

        // Try to grab only our states
        if (null != savedState && null != savedState.get(Notice.EXTRA_NOTICES)) {
            noticesState = new Bundle();
            noticesState.putParcelableArrayList(Notice.EXTRA_NOTICES, savedState.getParcelableArrayList(Notice.EXTRA_NOTICES));
        }

        return noticesState;
    }

}
