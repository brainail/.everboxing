package org.brainail.EverboxingTools.utils.callbacks;

/**
 * This class keeps some sdk callbacks weakly to protect us from memory leaks.
 * Also we keep our callbacks strongly via {@link AnonymousClassKeeper} to deal with anonymous classes.
 *
 * @author emalyshev
 */
public class CustomCallbacksWeakWrapper
        extends BaseCallbackWeakWrapper
        /* implements CustomCallback */ {

    public CustomCallbacksWeakWrapper (
            final AnonymousClassKeeper anonymousClassKeeper,
            final Object callback) {

        super(anonymousClassKeeper, callback);
    }

    public CustomCallbacksWeakWrapper (final Object callback) {
        super(null, callback);
    }

    /*
    @Override
    public void onCallback() {
        final Object callback = mCallbackRef.get();
        if (callback instanceof OurCallback) {
            releaseCallback(callback);
            ((OurCallback) callback).onCallback();
        }
    }
    */

}
