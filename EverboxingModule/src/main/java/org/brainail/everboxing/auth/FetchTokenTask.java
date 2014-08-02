package org.brainail.Everboxing.auth;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * User: brainail<br/>
 * Date: 02.08.14<br/>
 * Time: 17:47<br/>
 */
public class FetchTokenTask extends AbstractFetchTokenTask {

    public FetchTokenTask(final AuthorizationFlow authFlow, final String email, final String scope) {
        super(authFlow, email, scope);
    }

    @Override
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mAuthFlow.getActivity(), mEmail, mScope);
        } catch (UserRecoverableAuthException exception) {
            // GooglePlayServices.apk is either old, disabled, or not present, which is
            // recoverable, so we need to show the user some UI through the activity.
            mAuthFlow.handleError(exception);
        } catch (GoogleAuthException exception) {
            onError("Unrecoverable error " + exception.getMessage(), exception);
        }

        return null;
    }

}
