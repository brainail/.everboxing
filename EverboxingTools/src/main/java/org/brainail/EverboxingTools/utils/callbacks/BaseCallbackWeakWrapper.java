package org.brainail.EverboxingTools.utils.callbacks;

import org.brainail.EverboxingTools.BuildConfig;

import java.lang.ref.WeakReference;

public abstract class BaseCallbackWeakWrapper {

    protected final WeakReference<Object> mCallbackRef;
    protected final WeakReference<AnonymousClassKeeper> mAnonymousClassKeeper;

    public BaseCallbackWeakWrapper (
            final AnonymousClassKeeper anonymousClassKeeper,
            final Object callback) {

        mAnonymousClassKeeper = new WeakReference<> (anonymousClassKeeper);
        mCallbackRef = new WeakReference<> (callback);

        retainCallback (callback);
    }

    private void retainCallback (final Object callback) {
        final AnonymousClassKeeper anonymousClassKeeper = mAnonymousClassKeeper.get ();

        if (null != anonymousClassKeeper) {
            anonymousClassKeeper.retain (callback);
        } else if (BuildConfig.DEBUG && callback.getClass ().isAnonymousClass ()) {
            throw new IllegalArgumentException ("AnonymousClassKeeper must be provided for " + callback.getClass ());
        }
    }

    protected final void releaseCallback (final Object callback) {
        final AnonymousClassKeeper anonymousClassKeeper = mAnonymousClassKeeper.get ();
        if (null != anonymousClassKeeper) {
            anonymousClassKeeper.release (callback);
        }
    }

}
