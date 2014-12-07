package org.brainail.Everboxing.utils.tool;

import java.io.Closeable;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 *
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class Tool {

    // Simple class to work with masks.
    public static class BitMask {

        int mMask;

        BitMask() {
            mMask = 0;
        }

        BitMask(final int initMask) {
            mMask = initMask;
        }

        void apply(final int what) {
            mMask |= what;
        }

        void cancel(final int what) {
            if (isPresent(what)) {
                mMask ^= what;
            }
        }

        void modify(final int what, final boolean state) {
            if (state) {
                apply(what);
            } else {
                cancel(what);
            }
        }

        boolean isPresent(final int what) {
            return (mMask & what) > 0;
        }

    }

    public static void closeSilently(final Closeable closeable) {
        try {
            if (null != closeable) closeable.close();
        } catch (Exception e) {
            // Do nothing
        }
    }

}
