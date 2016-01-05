package org.brainail.EverboxingLexis.ui.views.dialogs.hardy;

import org.brainail.EverboxingHardyDialogs.BaseHardyDialogsCode;

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
public class LexisPaperHardyDialogsCode extends BaseHardyDialogsCode {

    // Dictionaries scanning
    public static final BaseHardyDialogsCode D_DICTIONARY_SCANNING_PROGRESS
            = new BaseHardyDialogsCode ("dictionary.scanning.progress");

    // Choose daily style (dark or light ...)
    public static final BaseHardyDialogsCode D_ARTICLE_DAILY_STYLE
            = new BaseHardyDialogsCode ("article.daily.style");

    // Choose load remote content mode
    public static final BaseHardyDialogsCode D_ARTICLE_LOAD_REMOTE_CONTENT_MODE
            = new BaseHardyDialogsCode ("article.load.remote.content.mode");

    // Choose speech language
    public static final BaseHardyDialogsCode D_SPEECH_LANGUAGE
            = new BaseHardyDialogsCode ("speech.languagee");

    // Confirm that you want to remove a dictionary
    public static final BaseHardyDialogsCode D_DICTIONARY_REMOVING_CONFIRMATION
            = new BaseHardyDialogsCode ("dictionary.removing.confirmation");

    // Confirm that you want to remove selected items
    public static final BaseHardyDialogsCode D_LIST_ITEMS_REMOVING_CONFIRMATION
            = new BaseHardyDialogsCode ("list.items.removing.confirmation");

    public LexisPaperHardyDialogsCode (String code) {
        super (code);
    }

}
