package org.brainail.EverboxingTools.utils.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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
public class OnGestureListener implements View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private Object mTag;

    public OnGestureListener (final Context context, final Object tag) {
        mGestureDetector = new GestureDetector (context, new GestureListener ());
        mTag = tag;
    }

    public boolean onTouch (final View view, final MotionEvent event) {
        mGestureDetector.onTouchEvent (event);
        return false;
    }

    private OnGestureListener self () {
        return this;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown (MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap (MotionEvent event) {
            self ().onDoubleTap (event, mTag);
            return super.onDoubleTap (event);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            self ().onLongPress (event, mTag);
            super.onLongPress (event);
        }

    }

    public void onDoubleTap (MotionEvent event, Object tag) {
        // To be overridden when implementing listener
    }

    public void onLongPress (MotionEvent event, Object tag) {
        // To be overridden when implementing listener
    }

}
