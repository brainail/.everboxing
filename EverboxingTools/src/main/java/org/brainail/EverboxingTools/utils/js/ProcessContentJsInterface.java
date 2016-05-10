package org.brainail.EverboxingTools.utils.js;

import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

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
public class ProcessContentJsInterface {

    public static final String JS_INTERFACE_NAME = "ProcessContentJsInterface";

    public static final String JS_ALL_TEXT_SELECTION = "javascript:window." +
            ProcessContentJsInterface.JS_INTERFACE_NAME +
            ".processContent(document.getElementsByTagName('body')[0].innerText);";

    public static final String JS_PARTIAL_TEXT_SELECTION = "javascript:" +
            "(function getSelectedText() {" +
            "var txt;" +
            "if (window.getSelection) {" +
            "txt = window.getSelection().toString();" +
            "} else if (window.document.getSelection) {" +
            "txt = window.document.getSelection().toString();" +
            "} else if (window.document.selection) {" +
            "txt = window.document.selection.createRange().text;" +
            "}" + ProcessContentJsInterface.JS_INTERFACE_NAME +
            ".processSelection(txt);" +
            "})()";

    public static interface ISelectionHelper {
        public void onAllTextSelection (final String selection);
        public void onPartialTextSelection (final String selection);
    }

    private final WeakReference<ISelectionHelper> mSelectionHelper;

    public ProcessContentJsInterface (final ISelectionHelper selectionHelper) {
        mSelectionHelper = new WeakReference<> (selectionHelper);
    }

    @JavascriptInterface
    public void processContent (final String content) {
        if (null != mSelectionHelper) {
            final ISelectionHelper selectionHelper = mSelectionHelper.get ();
            if (null != selectionHelper) {
                selectionHelper.onAllTextSelection (content);
            }
        }
    }

    @JavascriptInterface
    public void processSelection (final String content) {
        if (null != mSelectionHelper) {
            final ISelectionHelper selectionHelper = mSelectionHelper.get ();
            if (null != selectionHelper) {
                selectionHelper.onPartialTextSelection (content);
            }
        }
    }

}
