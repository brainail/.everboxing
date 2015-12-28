package org.brainail.EverboxingLexis.ui.views.dialogs.hardy;

import org.brainail.EverboxingHardyDialogs.BaseHardyDialogsHandlers;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;

import itkach.aard2.slob.SlobDescriptorList;
import itkach.aard2.utils.RemoteContentMode;

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
public class LexisPaperHardyDialogsHandlers extends BaseHardyDialogsHandlers {

    public static final class ArticleLoadRemoteContentMode extends HardyDialogFragment.IsolatedDialogHandler {
        @Override
        public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String itemTag) {
            super.onDialogListAction (dialog, whichItem, itemTag);
            if (dialog.isDialogWithCode (LexisPaperHardyDialogsCode.D_ARTICLE_LOAD_REMOTE_CONTENT_MODE)) {
                SettingsManager.getInstance().saveLoadRemoteContentMode (RemoteContentMode.valueOf (itemTag));
            }
        }
    }

    public static final class DictionaryRemovingConfirmation extends HardyDialogFragment.IsolatedDialogHandler {

        private int mDataPosition;
        private SlobDescriptorList mData;

        private DictionaryRemovingConfirmation() {}

        public DictionaryRemovingConfirmation(final SlobDescriptorList data, final int dataPosition) {
            mData = data;
            mDataPosition = dataPosition;
        }

        @Override
        public void onDialogAction (HardyDialogFragment dialog, int actionRequestCode) {
            super.onDialogAction (dialog, actionRequestCode);
            if (dialog.isDialogWithCode (LexisPaperHardyDialogsCode.D_DICTIONARY_REMOVING_CONFIRMATION)) {
                if (HardyDialogFragment.ActionRequestCode.POSITIVE == actionRequestCode) {
                    mData.remove (mDataPosition);
                }
            }
        }

    }

}
