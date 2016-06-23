package org.brainail.EverboxingSplashFlame.web;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import org.brainail.EverboxingSplashFlame.di.PerFragment;
import org.brainail.EverboxingTools.utils.PooLogger;

import java.util.Locale;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
@PerFragment
public class DefaultWebChromeClient extends WebChromeClient {

    private WebPage mWebPage;

    public DefaultWebChromeClient(WebPage webPage) {
        mWebPage = webPage;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        final String message = String.format(Locale.US, "%s, file: %s:%d",
                consoleMessage.message(),
                consoleMessage.sourceId(),
                consoleMessage.lineNumber());
        PooLogger.verb (message);
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        PooLogger.verb ("onReceivedTitle: view=?, title=?", view, title);
        super.onReceivedTitle(view, title);

        if (mWebPage != null) {
            mWebPage.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        PooLogger.verb ("onProgressChanged: view=?, newProgress=?", view, newProgress);
        super.onProgressChanged(view, newProgress);

        if (mWebPage != null) {
            mWebPage.onProgressChanged(view, newProgress);
        }
    }
}
