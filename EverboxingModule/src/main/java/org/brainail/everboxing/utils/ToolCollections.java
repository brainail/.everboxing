package org.brainail.Everboxing.utils;

import java.util.Collection;
import java.util.Collections;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * &copy; 2014 brainail <br/><br/>
 *
 * This program is free software: you can redistribute it and/or modify <br/>
 * it under the terms of the GNU General Public License as published by <br/>
 * the Free Software Foundation, either version 3 of the License, or <br/>
 * (at your option) any later version. <br/><br/>
 *
 * This program is distributed in the hope that it will be useful, <br/>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of <br/>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the <br/>
 * GNU General Public License for more details. <br/>
 *
 * You should have received a copy of the GNU General Public License <br/>
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
