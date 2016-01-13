package org.brainail.EverboxingLexis.utils.tool;

import android.speech.tts.TextToSpeech;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;
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
public final class ToolTts {

    public static Pair<List<String>, List<String>> supportedLanguages (final TextToSpeech textToSpeech) {
        final List<String> supportedLocalesNames = new ArrayList<> ();
        final List<String> supportedLocalesTags = new ArrayList<> ();

        if (null != textToSpeech) {
            for (final Locale locale : Locale.getAvailableLocales ()) {
                int availableResult = TextToSpeech.LANG_NOT_SUPPORTED;

                try {
                    availableResult = textToSpeech.isLanguageAvailable (locale);
                } catch (final Exception exception) {
                    // To fix something like: java.lang.IllegalArgumentException: Invalid int: "OS"
                    availableResult = TextToSpeech.LANG_NOT_SUPPORTED;
                }

                if (TextToSpeech.LANG_COUNTRY_AVAILABLE == availableResult) {
                    supportedLocalesNames.add (locale.getDisplayName ());
                    supportedLocalesTags.add (locale.toString ());
                }
            }
        }

        return Pair.create (supportedLocalesNames, supportedLocalesTags);
    }

}
