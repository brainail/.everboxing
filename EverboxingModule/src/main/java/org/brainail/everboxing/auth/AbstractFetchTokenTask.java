package org.brainail.Everboxing.auth;

import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.ToolErrors;
import org.brainail.Everboxing.utils.ToolUI;

import java.io.IOException;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * &copy; 2014 brainail <br/><br/>
 *
 * This program is free software: you can redistribute it and/or modify <br/>
 * it under the terms of the GNU General Public License as published by <br/>
 * the Free Software Foundation, either version 3 of the License, or <br/>
 * (at your option) any later version. <br/><br/>
 *
 * This program is distributed in the hope that it will be useful, <br/>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of <br/>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the <br/>
 * GNU General Public License for more details. <br/>
 *
 * You should have received a copy of the GNU General Public License <br/>
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    protected Void doInBackground(Void... params) {
        try {
            fetchUserInfo();
        } catch (IOException exception) {
            onError("Following Error occurred, please try again. " + exception.getLocalizedMessage(), exception);
        }

        return null;
    }

    private void fetchUserInfo() throws IOException {
        final String authToken = fetchToken();
        if (null != authToken) {
            ToolUI.showToast(mAuthFlow.getActivity(), "Hello " + mEmail + "! I'm glad to see you!");
            // STUFF: http://stackoverflow.com/questions/23493556/authorizing-my-application-to-use-an-existing-folder
            // STUFF: https://developers.google.com/drive/web/appdata
            mAuthFlow.onAuthTokenFetched(authToken);
        }
    }

    protected void onError(final String errorMessage, final Exception error) {
        ToolUI.showToast(mAuthFlow.getActivity(), errorMessage);
        Plogger.logE(Plogger.LogScope.AUTH_TOKEN, ToolErrors.toString(error));
    }

}
