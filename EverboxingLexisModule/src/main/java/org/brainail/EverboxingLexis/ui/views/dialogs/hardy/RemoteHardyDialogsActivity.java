package org.brainail.EverboxingLexis.ui.views.dialogs.hardy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.brainail.EverboxingLexis.utils.manager.ThemeManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
public class RemoteHardyDialogsActivity extends org.brainail.EverboxingHardyDialogs.RemoteHardyDialogsActivity {

    // Theme. I use null to define that this is full recreating
    private ThemeManager.AppTheme mTheme = null;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        getWindow ().setBackgroundDrawable (null);

        // Init & check theme
        mTheme = ThemeManager.checkOnCreate (this, mTheme);
    }

    @Override
    protected void attachBaseContext (final Context baseContext) {
        // Attach the Calligraphy
        super.attachBaseContext (CalligraphyContextWrapper.wrap (baseContext));
    }

    @Override
    protected void onResume () {
        ThemeManager.checkOnResume (this, mTheme);
        super.onResume ();
    }

}
