package org.brainail.Everboxing.utils.tool;

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
public final class ToolStrings {

    public static String EMPTY = "";
    public static String SPACE = " ";

    // Checks that the string is empty or is null.
    public static boolean isNullOrEmpty(final String value) {
        return null == value || 0 == value.length();
    }

    // Checks that some string is null/empty.
    public static boolean isAnyNullOrEmpty(final String ... values) {
        if (null == values) {
            return false;
        }

        for (final String value : values) {
            if (isNullOrEmpty(value)) {
                return true;
            }
        }

        return false;
    }

    // Checks that the string is null and if so, returns a def-value.
    public static String nullToDef(final String value, final String defValue) {
        return null == value ? defValue : value;
    }

    // Checks that the string is null/empty and if so, returns a def-value.
    public static String emptyToDef(final String value, final String defValue) {
        return isNullOrEmpty(value) ? defValue : value;
    }

    public static String capitalize(final String str) {
        if (isNullOrEmpty(str)) {
            return EMPTY;
        }

        final char firstChar = str.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return str;
        } else {
            return Character.toUpperCase(firstChar) + str.substring(1);
        }
    }

}
