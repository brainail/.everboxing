package org.brainail.Everboxing.auth;

import android.os.AsyncTask;

import org.brainail.Everboxing.utils.ErrorUtils;
import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.UIUtils;

import java.io.IOException;

/**
 * User: brainail<br/>
 * Date: 02.08.14<br/>
 * Time: 17:48<br/>
 */
public abstract class AbstractFetchTokenTask extends AsyncTask<Void, Void, Void> {

    protected final AuthorizationFlow mAuthFlow;
    protected final String mScope;
    protected final String mEmail;

    AbstractFetchTokenTask(final AuthorizationFlow authFlow, final String email, final String scope) {
        mAuthFlow = authFlow;
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
            onError("Following Error occurred, please try again. " + exception.getMessage(), exception);
        }

        return null;
    }

    private void fetchUserInfo() throws IOException {
        final String authToken = fetchToken();
        if (null != authToken) {
            UIUtils.showToast(mAuthFlow.getActivity(), "Hello " + mEmail + "! I'm glad to see you!");
            mAuthFlow.onAuthTokenFetched(authToken);
        }
    }

    protected void onError(final String errorMessage, final Exception error) {
        UIUtils.showToast(mAuthFlow.getActivity(), errorMessage);
        Plogger.logE(Plogger.LogScope.AUTH_TOKEN, ErrorUtils.toString(error));
    }

}
