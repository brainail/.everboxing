package org.brainail.Everboxing.utils.tool;

import java.util.Collection;
import java.util.Collections;

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
