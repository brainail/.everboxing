package org.brainail.Everboxing.ui.notice;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import org.brainail.Everboxing.JApplication;

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
class NoticeOnFragmentSceneController extends NoticeOnSceneController {

    private WeakReference<Fragment> mSceneRef = new WeakReference<Fragment>(null);

    NoticeOnFragmentSceneController(final Fragment scene) {
        mSceneRef = new WeakReference<Fragment>(scene);
    }

    @Override
    public Activity rootScene() {
        final Fragment scene = mSceneRef.get();
        return (null != scene ? scene.getActivity() : null);
    }

    @Override
    public Object scene() {
        return mSceneRef.get();
    }

    @Override
    public NoticeBar.Builder noticeBuilder() {
        final Fragment scene = mSceneRef.get();

        if (isVisibleScene()) {
            final View root = scene.getView();
            if (null != root) {
                return new NoticeBar.Builder(JApplication.appContext(), root);
            }
        }

        return null;
    }

    private boolean isVisibleScene() {
        final Fragment scene = mSceneRef.get();
        return
            null != scene &&
            scene.isAdded() &&
            null != scene.getView() &&
            !scene.isRemoving() &&
            scene.getUserVisibleHint();
    }

}
