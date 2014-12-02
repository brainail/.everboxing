package org.brainail.Everboxing.auth;

import org.brainail.Everboxing.utils.Plogger;
import org.brainail.Everboxing.utils.tool.ToolErrors;
import org.brainail.Everboxing.utils.tool.ToolUI;

import java.io.IOException;

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
