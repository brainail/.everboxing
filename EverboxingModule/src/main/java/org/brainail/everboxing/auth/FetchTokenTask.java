package org.brainail.Everboxing.auth;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.brainail.Everboxing.JApplication;
import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolUI;

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
public class FetchTokenTask extends AbstractFetchTokenTask {

    public FetchTokenTask(final AuthorizationFlow authFlow, final String email, final String scope) {
        super(authFlow, email, scope);
    }

    @Override
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(JApplication.appContext(), mEmail, mScope);
        } catch (UserRecoverableAuthException exception) {
            // GooglePlayServices.apk is either old, disabled, or not present, which is
            // recoverable, so we need to show the user some UI through the activity.
            mAuthFlow.handleError(exception);
        } catch (GoogleAuthException exception) {
            onError(exception);
            ToolUI.showToast(mAuthFlow.getActivity(), R.string.auth_flow_device_suitability);
        }

        return null;
    }

}
