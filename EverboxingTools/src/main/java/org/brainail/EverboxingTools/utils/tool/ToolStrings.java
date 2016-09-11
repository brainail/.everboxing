package org.brainail.EverboxingTools.utils.tool;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.View;

import org.brainail.EverboxingTools.utils.Sdk;
import org.brainail.EverboxingTools.utils.text.spannable.ColoredURLSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public final class ToolStrings {

    // match '?' if not after '\'
    private static final Pattern FORMAT_PATTERN = Pattern.compile("(?<!\\\\)\\?");

    public static String EMPTY = "";
    public static String PLUS = "+";
    public static String SPACE = " ";

    // Checks that the string is empty or is null.
    public static boolean isNullOrEmpty (final String value) {
        return null == value || 0 == value.length ();
    }

    // Checks that some string is null/empty.
    public static boolean isAnyNullOrEmpty (final String... values) {
        if (null == values) {
            return false;
        }

        for (final String value : values) {
            if (isNullOrEmpty (value)) {
                return true;
            }
        }

        return false;
    }

    // Checks that the string is null and if so, returns a def-value.
    public static String nullToDef (final String value, final String defValue) {
        return null == value ? defValue : value;
    }

    // Checks that the string is null/empty and if so, returns a def-value.
    public static String emptyToDef (final String value, final String defValue) {
        return isNullOrEmpty (value) ? defValue : value;
    }

    public static String capitalize (final String str) {
        if (isNullOrEmpty (str)) {
            return EMPTY;
        }

        final char firstChar = str.charAt (0);
        if (Character.isUpperCase (firstChar)) {
            return str;
        } else {
            return Character.toUpperCase (firstChar) + str.substring (1);
        }
    }

    public static List<String> grabTtsWords (final String text) {
        final String [] words = text.split ("\\s+");
        final StringBuilder currentText = new StringBuilder ();
        final List<String> resultWords = new ArrayList<> ();

        for (int i = 0; i < words.length; ++ i) {
            // final String word = words [i].replaceAll ("[^\\w]", "");
            final String word = words [i]; //.replaceAll ("[^\\w]", "");
            if (!TextUtils.isEmpty (word)) {
                currentText.append (word).append (" ");
            }

            if (currentText.length () > 200) {
                resultWords.add (currentText.toString ());
                currentText.setLength (0);
            }
        }

        if (currentText.length () > 0) {
            resultWords.add (currentText.toString ());
            currentText.setLength (0);
        }

        return resultWords;
    }

    // format messages with '?' as a placeholder
    public static String formatArgs (final Locale locale, final String message, final Object ... args) {
        if (null != message) {
            final int argsCount = args == null ? 1 : args.length;

            if (0 == argsCount) {
                return message;
            } else {
                final Matcher m = FORMAT_PATTERN.matcher(message);
                // replace matches with %s, then escaped sequence '\?' with a single '?'
                final String fmt = m.replaceAll("%s").replace("\\?", "?");
                return null != locale ? String.format(locale, fmt, args) : String.format(fmt, args);
            }
        }

        return null;
    }

    /**
     * Use {@link View#getTag(int)} with {@code textStringId} as key to identify later.
     */
    public static CharSequence createTextWithLink(
            final @NonNull Context context,
            final @StringRes int textStringId,
            final @ColorRes int linkColorId,
            final @StringRes int linkStringId,
            final boolean isLinkUnderline,
            final String textSeparator,
            final @Nullable View.OnClickListener clickListener) {

        final Resources resources = context.getResources ();
        final SpannableStringBuilder textBuilder = new SpannableStringBuilder();
        String text = resources.getString(textStringId);
        SpannableString textSpannable = new SpannableString(text);
        textBuilder.append(textSpannable);

        textBuilder.append(textSeparator);

        text = resources.getString(linkStringId);
        textSpannable = new SpannableString(text);
        final int textColor = ContextCompat.getColor(context, linkColorId);
        final CharacterStyle textSpan = new ColoredURLSpan (text, textColor, isLinkUnderline) {
            @Override
            public void onClick(View widget) {
                // Set tag to identify later
                widget.setTag (textStringId, linkStringId);

                if (null != clickListener) {
                    clickListener.onClick(widget);
                }

                widget.setTag (textStringId, null);
            }
        };

        textSpannable.setSpan(textSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textBuilder.append(textSpannable);

        return textBuilder;
    }

    public static Spanned fromHtmlCompat(final String text) {
        if (Sdk.isSdkSupported (Sdk.N)) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            // noinspection deprecation
            return Html.fromHtml(text);
        }
    }

}
