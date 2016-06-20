package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

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
public class RxBaseFragment extends BaseFragment {

    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription ();

    protected final void bindSubscription (final Subscription subscription) {
        if (null == subscription) {
            // Ignore incorrect subscription
            return;
        }

        mCompositeSubscription.add (subscription);
    }

    protected final <T> Observable<T> getCachedObservable (final boolean createIfNull) {
        Observable<T> cachedObservable = RxCacheFragment.Helper.getCache (getFragmentManager (), this);

        if (null == cachedObservable && createIfNull) {
            cachedObservable = createCachedObservable ();
            RxCacheFragment.Helper.setCache (getFragmentManager (), this, cachedObservable);
        }

        return cachedObservable;
    }

    protected final <T> Observable<T> cacheObservable (final Observable<T> cachedObservable) {
        RxCacheFragment.Helper.setCache (getFragmentManager (), this, cachedObservable);
        return cachedObservable;
    }

    protected <T> Observable<T> createCachedObservable () {
        return null;
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
        mCompositeSubscription.clear ();
    }

}
