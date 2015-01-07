package org.brainail.Everboxing.ui.notice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

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
public abstract class NoticeOnSceneController {

    private static IntentFilter FILTER;

    static {
        FILTER = new IntentFilter();
    }

    protected boolean mHasScene = false;
    protected NoticeBar mSceneNotice = null;

    private boolean mIsRegistered = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // We can show some notice for it
        }
    };

    private NoticeBar.OnActionCallback mActionCallback = new NoticeBar.OnActionCallback() {
        @Override
        public void onAction(Parcelable token) {
            final Activity scene = rootScene();
            if (null != scene) {
            }
        }
    };

    public abstract Activity rootScene();
    public abstract Object scene();
    public abstract NoticeBar.Builder noticeBuilder();

    public void showScene() {
        mHasScene = true;
        notifyScene();
    }

    public void hideScene() {
        mHasScene = false;
        muteScene();
    }

    private void muteScene() {
        if (null != mSceneNotice) {
            mSceneNotice.clear();
            mSceneNotice = null;
        }
    }

    // We want to work with some elements of UI on UI thread
    private void notifySceneOnUI() {
        final Activity scene = rootScene();
        if (null != scene) {
            scene.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyScene();
                }
            });
        }
    }

    private void notifyScene() {
        if (mHasScene) {
            final NoticeBar.Builder noticeBuilder = noticeBuilder();

            if (null != noticeBuilder) {
                // muteScene();
                inflateNotice(noticeBuilder);
                mSceneNotice = noticeBuilder.show();
            }
        }
    }

    private void inflateNotice(final NoticeBar.Builder noticeBuilder) {
        noticeBuilder.withMessage("");
        noticeBuilder.withActionMessage("");
        noticeBuilder.withStyle(NoticeOnSceneStyleFactory.get(scene()));
        noticeBuilder.withOnActionCallback(mActionCallback);
    }

    public void registerScene() {
        final Activity scene = rootScene();
        if (null != scene && !mIsRegistered && 0 != FILTER.countActions()) {
            scene.registerReceiver(mReceiver, FILTER);
            mIsRegistered = true;
        }
    }

    public void unregisterScene() {
        final Activity scene = rootScene();
        if (null != scene && mIsRegistered) {
            try {
                scene.unregisterReceiver(mReceiver);
            } catch (Exception exception) {
                // Workaround for java.lang.IllegalArgumentException: Receiver not registered
            }

            mIsRegistered = false;
        }
    }

}
