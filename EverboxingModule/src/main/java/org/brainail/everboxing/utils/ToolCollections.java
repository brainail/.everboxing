package org.brainail.Everboxing.utils;

import java.util.Collection;
import java.util.Collections;

/**
 * User: brainail<br/>
 * Date: 27.07.14<br/>
 * Time: 11:59<br/>
 */
public final class ToolCollections {

    public static <T> Iterable<T> emptyIfNull(final Iterable<T> iterable) {
        return null == iterable ? Collections.<T>emptyList() : iterable;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T [] emptyIfNull(final T [] array) {
        return null == array ? (T []) (new Object [0]) : array;
    }

    public static <T> boolean isNullOrEmpty(final T [] array) {
        return null == array || 0 == array.length;
    }

    public static boolean isAnyNull(final Object ... objects) {
        for (final Object object : emptyIfNull(objects)) {
            if (null == object) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAnyNullOrEmpty(final Collection ... collections) {
        for (final Collection collection : emptyIfNull(collections)) {
            if (null == collection || collection.isEmpty()) {
                return true;
            }
        }

        return false;
    }

}
