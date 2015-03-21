package org.brainail.Everboxing.ui.notice;

import android.app.Activity;

import java.lang.ref.WeakReference;

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
class NoticeOnActivitySceneController extends NoticeOnSceneController {

    private WeakReference<Activity> mSceneRef = new WeakReference<>(null);
    private NoticeBar.Builder mNoticeBuilder = null;

    NoticeOnActivitySceneController(final Activity scene) {
        mSceneRef = new WeakReference<>(scene);
    }

    @Override
    protected Activity rootScene() {
        return mSceneRef.get();
    }

    @Override
    protected Object scene() {
        return mSceneRef.get();
    }

    @Override
    protected NoticeBar.Builder noticeBuilder() {
        final Activity scene = mSceneRef.get();

        if (null != scene) {
            // Keep once
            if (null == mNoticeBuilder) {
                mNoticeBuilder = new NoticeBar.Builder(this, scene);
            }

            return mNoticeBuilder;
        }

        return null;
    }

}
