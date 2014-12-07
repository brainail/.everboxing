package org.brainail.Everboxing.utils.tool;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

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
public final class ToolIO {

    public static final int IO_BUFFER_SIZE = 4 * 1024;
    public static final String IO_CHARSET_UTF8 = "UTF-8";
    public static final String CONTENT_ENCODING_GZIP = "gzip";

    public static String readStream(final InputStream inStream) throws IOException {
        final StringBuilder streamBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, IO_CHARSET_UTF8));

        int readChars;
        char [] buffer = new char [IO_BUFFER_SIZE];

        while ((readChars = reader.read(buffer)) >= 0) {
            streamBuilder.append(buffer, 0, readChars);
        }

        return streamBuilder.toString();
    }

    public static String decodeBase64Stream(final InputStream inStream) throws IOException {
        return new String(Base64.decode(readStream(inStream), Base64.DEFAULT), IO_CHARSET_UTF8);
    }

    public static InputStream totHttpInputStream(final HttpURLConnection httpConnection) throws IOException {
        InputStream httpInputStream = httpConnection.getInputStream();
        final String contentEncoding = httpConnection.getContentEncoding();

        if (null != contentEncoding && contentEncoding.toLowerCase(Locale.US).contains(CONTENT_ENCODING_GZIP)) {
            httpInputStream = new GZIPInputStream(httpInputStream);
        }

        return httpInputStream;
    }

}
