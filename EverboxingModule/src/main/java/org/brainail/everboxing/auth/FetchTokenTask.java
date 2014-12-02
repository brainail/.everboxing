package org.brainail.Everboxing.auth;

import android.app.Activity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class FetchTokenTask extends AbstractFetchTokenTask {

    public FetchTokenTask(final AuthorizationFlow authFlow, final String email, final String scope) {
        super(authFlow, email, scope);
    }

    @Override
    protected String fetchToken() throws IOException {
        try {
            final Activity activity = mAuthFlow.getActivity();
            if (null != activity) {
                return GoogleAuthUtil.getToken(activity, mEmail, mScope);
            }
        } catch (UserRecoverableAuthException exception) {
            // GooglePlayServices.apk is either old, disabled, or not present, which is
            // recoverable, so we need to show the user some UI through the activity.
            mAuthFlow.handleError(exception);
        } catch (GoogleAuthException exception) {
            onError("Unrecoverable error " + exception.getLocalizedMessage(), exception);
        }

        return null;
    }

}
