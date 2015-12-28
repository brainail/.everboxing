package org.brainail.EverboxingLexis.ui.views.dialogs.hardy;

import org.brainail.EverboxingHardyDialogs.BaseDialogSpecification;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.LayoutParams;
import org.brainail.EverboxingHardyDialogs.ListDialogSpecification;
import org.brainail.EverboxingHardyDialogs.TwoButtonDialogSpecification;
import org.brainail.EverboxingLexis.R;

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
public final class LexisPaperHardyDialogs {

    public static BaseDialogSpecification.Builder<?> dictionaryScanningDialog () {
        return BaseDialogSpecification.create ()
                .code (LexisPaperHardyDialogsCode.D_DICTIONARY_SCANNING_PROGRESS)
                .contentLayoutParams (new LayoutParams (200 /*dp*/, 200 /*dp*/))
                .content (R.layout.view_scanning_dictionaries)
                .cancelable (false)
                .translucent (true);
    }

    public static ListDialogSpecification.Builder<?> articleDailyStyleDialog () {
        return ListDialogSpecification.create ()
                .code (LexisPaperHardyDialogsCode.D_ARTICLE_DAILY_STYLE)
                .title (R.string.select_style);
    }

    public static ListDialogSpecification.Builder<?> articleLoadRemoteContentModeDialog () {
        return ListDialogSpecification.create ()
                .code (LexisPaperHardyDialogsCode.D_ARTICLE_LOAD_REMOTE_CONTENT_MODE)
                .title (R.string.settings_load_remote_content_dialog_title);
    }

    public static TwoButtonDialogSpecification.Builder<?> dictionaryRemovingConfirmationDialog (final String label) {
        return TwoButtonDialogSpecification.create ()
                .code (LexisPaperHardyDialogsCode.D_DICTIONARY_REMOVING_CONFIRMATION)
                .body (R.string.dictionaries_confirm_forget, label)
                .positiveButton (R.string.dialog_button_yes)
                .negativeButton (R.string.dialog_button_no)
                .restorable (false);
    }

    public static TwoButtonDialogSpecification.Builder<?> listItemsRemovingConfirmationDialog (final String count) {
        return TwoButtonDialogSpecification.create ()
                .code (LexisPaperHardyDialogsCode.D_LIST_ITEMS_REMOVING_CONFIRMATION)
                .body (R.string.blob_descriptor_confirm_delete, count)
                .positiveButton (R.string.dialog_button_yes)
                .negativeButton (R.string.dialog_button_no);
    }

}
