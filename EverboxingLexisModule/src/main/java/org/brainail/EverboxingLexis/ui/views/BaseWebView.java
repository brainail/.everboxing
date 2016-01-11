package org.brainail.EverboxingLexis.ui.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.Sdk;
import org.brainail.EverboxingLexis.utils.tool.ToolWebView;

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
public abstract class BaseWebView extends WebView {

    // Javascript interfaces mapping
    // protected Map<String, Object> mJavascriptInterfaces = new HashMap<String, Object> ();

    public BaseWebView (Context context) {
        super (context);
    }

    public BaseWebView (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public BaseWebView (Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    @TargetApi (Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super (context, attrs, defStyleAttr, defStyleRes);
    }

    public BaseWebView (Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super (context, attrs, defStyleAttr, privateBrowsing);
    }

    public void performOnPause() {
        ToolWebView.doWebViewOnPause(this);
    }

    public void performOnResume() {
        ToolWebView.doWebViewOnResume(this);
    }

    public void performOnDestroy() {
        // removeJavascriptInterfaces();
        destroy();
    }

    @Override
    // Javascript is required and its content is known
    @SuppressLint ({"AddJavascriptInterface", "JavascriptInterface"})
    public void addJavascriptInterface(final Object object, final String jsName) {
        super.addJavascriptInterface(object, jsName);
        // mJavascriptInterfaces.put(jsName, object);
    }

    @Override
    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    public void removeJavascriptInterface(final String jsName) {
        if (Sdk.isSdkSupported(Sdk.HONEYCOMB)) {
            super.removeJavascriptInterface(jsName);
        }

        // mJavascriptInterfaces.remove(jsName);
    }

//    public Object getJavascriptInterface(final String jsName) {
//        return mJavascriptInterfaces.get(jsName);
//    }
//
//    void removeJavascriptInterfaces() {
//        for (final String jsName : mJavascriptInterfaces.keySet()) {
//            removeJavascriptInterface(jsName);
//        }
//    }

    private WebView self() {
        return this;
    }

    @SuppressLint ("SetJavaScriptEnabled")
    protected void initWebSetting (final Context context) {
        final WebSettings settings = getSettings ();

        // JS
        settings.setJavaScriptEnabled (true);

        // Zoom
        settings.setBuiltInZoomControls (true);
        settings.setDisplayZoomControls (false);

        // Cache
        // settings.setAppCachePath (new File (getContext ().getCacheDir (), WebArticlesCacheOptions.CACHE_PATH).getAbsolutePath ());
        // settings.setDatabasePath (getContext ().getDatabasePath (WebArticlesCacheOptions.DATABASE_PATH).getAbsolutePath ());
        // settings.setAppCacheMaxSize (WebArticlesCacheOptions.CACHE_SIZE_BYTES);
        // settings.setAppCacheEnabled (true);
        // settings.setDomStorageEnabled (true);
        // settings.setAllowFileAccess (true);
        // settings.setDatabaseEnabled (true);
        // settings.setCacheMode (WebSettings.LOAD_DEFAULT);

        addJavascriptInterfaces(context);
    }

    protected void addJavascriptInterfaces (final Context context) {
        // ...
    }

    public static class LoggingWebChromeClient extends WebChromeClient {

        public boolean onConsoleMessage(final ConsoleMessage cm) {
            Plogger.logW("Console message: %s.\n-- From line: %d", cm.message(), cm.lineNumber());
            return true;
        }

    }

}
