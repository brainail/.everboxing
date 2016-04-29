package org.brainail.EverboxingTools.utils.tool;

import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;

import org.brainail.EverboxingTools.ToolsContext;

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
public final class ToolFonts {

    public static class RobotoFonts {
        public static String ASSETS_THIN = "fonts/Roboto-Thin.ttf";
        public static String ASSETS_LIGHT = "fonts/Roboto-Light.ttf";
        public static String ASSETS_REGULAR = "fonts/Roboto-Regular.ttf";
        public static String ASSETS_MEDIUM = "fonts/Roboto-Medium.ttf";
    }

    private static final ArrayMap<String, Typeface> CACHE = new ArrayMap<String, Typeface> (1);

    private static Typeface typeface (final String fontPath) {
        synchronized (CACHE) {
            if (! CACHE.containsKey(fontPath)) {
                final Typeface typeface = Typeface.createFromAsset (ToolsContext.get ().getAssets (), fontPath);
                CACHE.put (fontPath, typeface);
            }

            return CACHE.get (fontPath);
        }
    }

    public static Typeface robotoThin () {
        return typeface (RobotoFonts.ASSETS_THIN);
    }

    public static Typeface robotoLight () {
        return typeface (RobotoFonts.ASSETS_LIGHT);
    }

    public static Typeface robotoMedium () {
        return typeface (RobotoFonts.ASSETS_MEDIUM);
    }

}
