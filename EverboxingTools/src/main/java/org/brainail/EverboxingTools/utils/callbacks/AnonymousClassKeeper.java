package org.brainail.EverboxingTools.utils.callbacks;

import java.util.HashSet;
import java.util.Set;

/**
 * This class was introduced to keep objects which were created via anonymous classes strongly.
 * To solve problem when we want to keep such kinda objects somewhere else using {@link java.lang.ref.WeakReference}
 *
 * @author emalyshev
 */
public final class AnonymousClassKeeper {

    private final Set<Object> mObjects = new HashSet<> ();

    public <T> T retain (final T object) {
        mObjects.add (object);
        return object;
    }

    public boolean release (final Object object) {
        return mObjects.remove (object);
    }

}
