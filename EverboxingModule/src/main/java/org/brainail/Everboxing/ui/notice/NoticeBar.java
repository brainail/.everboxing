package org.brainail.Everboxing.ui.notice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        public static final short LONG = 5000;
        public static final short MEDIUM = 3000;
        public static final short SHORT = 2000;
        public static final short PERMANENT = 0;
    }

    public enum Style {
        // At the bottom
        DEFAULT,
        // At the top
        DEFAULT_TOP
    }

    private View mNoticeView;
    private TextView mNoticeMessage;
    private TextView mNoticeAction;
    private NoticeContainer mNoticeContainer;

    private OnActionCallback mActionCallback;
    private OnVisibilityCallback mVisibilityCallback;

    private final Context mContext;

    public interface OnActionCallback {
        public void onAction(final Parcelable token);
    }

    public interface OnVisibilityCallback {
        public void onShow(final int size);
        public void onHide(final int size);
    }

    public NoticeBar(final Activity activity) {
        mContext = activity.getApplicationContext();
        final ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        final View notice = activity.getLayoutInflater().inflate(R.layout.notice, container, false);
        init(container, notice);
    }

    public NoticeBar(final Context context, final View root) {
        mContext = context.getApplicationContext();
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notice_container, (ViewGroup) root);
        final View notice = inflater.inflate(R.layout.notice, (ViewGroup) root, false);
        init((ViewGroup) root, notice);
    }

    private void init(final ViewGroup container, final View notice) {
        mNoticeContainer = (NoticeContainer) container.findViewById(R.id.notice_container);
        if (null == mNoticeContainer) {
            mNoticeContainer = new NoticeContainer(container);
        }

        mNoticeView = notice;
        mNoticeMessage = (TextView) notice.findViewById(R.id.notice_message);
        mNoticeAction = (TextView) notice.findViewById(R.id.notice_action);
        mNoticeAction.setOnClickListener(mInternalOnActionCallback);
    }

    public static class Builder {

        private NoticeBar noticeBar;
        private Context context;
        private String message;
        private String actionMessage;
        private Style style = Style.DEFAULT;
        private Parcelable token;
        private short duration = Duration.MEDIUM;

        public Builder(final Activity activity) {
            context = activity.getApplicationContext();
            noticeBar = new NoticeBar(activity);
        }

        public Builder(final Context context, final View root) {
            this.context = context;
            noticeBar = new NoticeBar(context, root);
        }

        public Builder withMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder withMessageId(final int resId) {
            if (resId > 0) {
                message = context.getString(resId);
            }

            return this;
        }

        public Builder withActionMessage(final String actionMessage) {
            this.actionMessage = actionMessage;
            return this;
        }

        public Builder withActionMessageId(final int resId) {
            if (resId > 0) {
                actionMessage = context.getString(resId);
            }

            return this;
        }

        public Builder withStyle(final Style style) {
            this.style = style;
            return this;
        }

        public Builder withToken(final Parcelable token) {
            this.token = token;
            return this;
        }

        public Builder withDuration(final short duration) {
            this.duration = duration;
            return this;
        }

        public Builder withOnActionCallback(final OnActionCallback callback) {
            noticeBar.setOnActionCallback(callback);
            return this;
        }

        public Builder withOnVisibilityCallback(final OnVisibilityCallback callback) {
            noticeBar.setOnVisibilityCallback(callback);
            return this;
        }

        public NoticeBar show() {
            noticeBar.showMessage(new Notice(message, actionMessage, token, duration, style));
            return noticeBar;
        }

    }

    private void showMessage(final Notice notice) {
        mNoticeContainer.showNotice(notice, mNoticeView, mVisibilityCallback);
    }

    public int getHeight() {
        mNoticeView.measure(
                View.MeasureSpec.makeMeasureSpec(mNoticeView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mNoticeView.getHeight(), View.MeasureSpec.AT_MOST)
        );

        return mNoticeView.getMeasuredHeight();
    }

    public View getContainerView() {
        return mNoticeView;
    }

    private final View.OnClickListener mInternalOnActionCallback = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mActionCallback && mNoticeContainer.isShowing()) {
                mActionCallback.onAction(mNoticeContainer.peekNotice().token);
            }

            mNoticeContainer.hide();
        }

    };

    public NoticeBar setOnActionCallback(final OnActionCallback callback) {
        mActionCallback = callback;
        return this;
    }

    public NoticeBar setOnVisibilityCallback(final OnVisibilityCallback callback) {
        mVisibilityCallback = callback;
        return this;
    }

    private void clear(final boolean animate) {
        mNoticeContainer.clearNotices(animate);
    }

    public void clear() {
        clear(true);
    }

     // See android.app.Activity#onRestoreInstanceState(android.os.Bundle)
    public void onRestoreInstanceState(final Bundle state) {
        mNoticeContainer.restoreState(state, mNoticeView);
    }

    // See android.app.Activity#onSaveInstanceState(android.os.Bundle)
    public Bundle onSaveInstanceState() {
        return mNoticeContainer.saveState();
    }

}
