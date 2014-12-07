package org.brainail.Everboxing.auth;

import android.text.TextUtils;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.tool.ToolErrors;
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
public abstract class AbstractFetchTokenTask extends AbstractAuthTask {

    protected final String mScope;
    protected final String mEmail;

    AbstractFetchTokenTask(final AuthorizationFlow authFlow, final String email, final String scope) {
        super(authFlow);
        mScope = scope;
        mEmail = email;
    }

    // Get a authentication token if one is not available. If the error is not recoverable then
    // it displays the error message on parent activity right away.
    protected abstract String fetchToken() throws IOException;

    @Override
    protected Void doInBackground(final Void ... params) {
        try {
            fetchUserInfo();
        } catch (IOException exception) {
            onError(exception);
            ToolUI.showToast(mAuthFlow.getActivity(), R.string.auth_flow_fetch_token_io_error);
        }

        return null;
    }

    private void fetchUserInfo() throws IOException {
        final String authToken = fetchToken();

        if (!TextUtils.isEmpty(authToken)) {
            // STUFF: http://stackoverflow.com/questions/23493556/authorizing-my-application-to-use-an-existing-folder
            // STUFF: https://developers.google.com/drive/web/appdata
            mAuthFlow.onAuthTokenFetched(authToken);
        }
    }

    protected void onError(final Exception error) {
        Plogger.logE(Plogger.LogScope.AUTH_TOKEN, ToolErrors.toString(error));
    }

}
