package org.brainail.Everboxing.auth;

import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.tool.ToolDriveScope;

import java.io.IOException;

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
public class ClearTokenTask extends AbstractAuthTask {

    protected final String mEmail;

    ClearTokenTask(final AuthorizationFlow authFlow, final String email) {
        super(authFlow);
        mEmail = email;
    }

    @Override
    protected Void doInBackground(final Void ... params) {
        try {
            clearToken();
        } catch (Exception exception) {
            Plogger.logE(Plogger.LogScope.AUTH_TOKEN, "Something was wrong while clearing token for account");
        }

        return null;
    }

    private void clearToken() throws IOException, GoogleAuthException {
        if (!TextUtils.isEmpty(mEmail)) {
            final String token = GoogleAuthUtil.getToken(JApplication.appContext(), mEmail, ToolDriveScope.formFullScope());
            if (!TextUtils.isEmpty(token)) {
                GoogleAuthUtil.clearToken(JApplication.appContext(), token);
            }
        }
    }

}
