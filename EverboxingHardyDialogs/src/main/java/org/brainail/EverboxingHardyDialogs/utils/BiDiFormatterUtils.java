package org.brainail.EverboxingHardyDialogs.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.text.BidiFormatter;

import java.util.Locale;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class BiDiFormatterUtils {

    public static String wrapStringArguments (
            final Context context,
            final int stringResId,
            final Object ... formatArgs) {

        return wrapStringArguments (context.getResources (), stringResId, formatArgs);
    }

    public static String wrapStringArguments (
            final Resources res,
            final int stringResId,
            final Object ... formatArgs) {

        return res.getString (stringResId, wrapArguments (formatArgs));
    }

    public static String wrapQuantityStringArguments (
            final Context context,
            final int stringResId,
            final int quantity,
            final Object ... formatArgs) {

        return wrapQuantityStringArguments (context.getResources (), stringResId, quantity, formatArgs);
    }

    public static String wrapQuantityStringArguments (
            final Resources res,
            final int stringResId,
            final int quantity,
            final Object ... formatArgs) {

        return res.getQuantityString (stringResId, quantity, wrapArguments (formatArgs));
    }

    public static Object [] wrapArguments (final Object ... args) {
        final BidiFormatter bidiFormatter = BidiFormatter.getInstance (isRtl ());

        for (int i = 0; i < args.length; i++) {
            if (args [i] instanceof String) {
                args [i] = bidiFormatter.unicodeWrap ((String) args[i]);
            }
        }

        return args;
    }

    public static String wrapString (final String string) {
        return BidiFormatter.getInstance (isRtl ()).unicodeWrap (string);
    }

    public static String forceStringToLtr (final String string) {
        return BidiFormatter.getInstance (false).unicodeWrap (string);
    }

    public static boolean isRtl () {
        return isRtl (Locale.getDefault ());
    }

    public static boolean isRtl (final Locale locale) {
        final int direction = Character.getDirectionality (locale.getDisplayName ().charAt (0));

        switch (direction) {
            case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
            case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
                return true;
            default:
                return false;
        }
    }

}

