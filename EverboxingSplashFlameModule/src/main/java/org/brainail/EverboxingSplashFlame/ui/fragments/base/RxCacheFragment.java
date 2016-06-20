package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class RxCacheFragment <T> extends Fragment {

    @NonNull
    public static <T> RxCacheFragment<T> newInstance () {
        final RxCacheFragment<T> fragment = new RxCacheFragment<> ();
        fragment.setRetainInstance (true);
        return fragment;
    }

    private T mCache;

    @Nullable
    public T getCache () {
        return mCache;
    }

    public void setCache (final T cache) {
        mCache = cache;
    }

    public static final class Helper {
        @Nullable
        private static <T> RxCacheFragment<T> findFragment (
                final FragmentManager fragmentManager,
                final @NonNull String managerTag) {

            if (null == fragmentManager) {
                return null;
            }

            final Fragment fragment = fragmentManager.findFragmentByTag (managerTag);
            if (fragment instanceof RxCacheFragment) {
                // noinspection unchecked
                return (RxCacheFragment<T>) fragment;
            } else {
                return null;
            }
        }

        @Nullable
        public static <T> T getCache (
                final FragmentManager fragmentManager,
                final @NonNull Object managerTagObject) {

            final RxCacheFragment<T> fragment = findFragment (fragmentManager, getTag (managerTagObject));
            if (null == fragment) {
                return null;
            }

            return fragment.getCache ();
        }

        public static <T> boolean setCache (
                final FragmentManager fragmentManager,
                final @NonNull Object managerTagObject,
                final @NonNull T cache) {

            if (null == fragmentManager) {
                return false;
            }

            RxCacheFragment<T> fragment = findFragment (fragmentManager, getTag (managerTagObject));

            if (null == fragment) {
                fragment = RxCacheFragment.newInstance ();
                fragmentManager.beginTransaction ().add (fragment, getTag (managerTagObject)).commit ();
            }

            fragment.setCache (cache);
            return true;
        }

        @NonNull
        private static String getTag (final @NonNull Object managerTagObject) {
            return "Everboxing.RxCache#" + managerTagObject.getClass ().getCanonicalName ();
        }
    }

}
