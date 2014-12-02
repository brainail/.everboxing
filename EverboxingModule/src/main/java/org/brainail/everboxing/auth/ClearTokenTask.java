package org.brainail.Everboxing.auth;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.brainail.Everboxing.utils.Plogger;

import java.io.IOException;

public class ClearTokenTask extends AbstractAuthTask {

    protected final String mEmail;

    ClearTokenTask(final AuthorizationFlow authFlow, final String email) {
        super(authFlow);
        mEmail = email;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            clearToken();
        } catch (Exception exception) {
            Plogger.logE(Plogger.LogScope.AUTH_TOKEN, "Something was wrong while clearing token for account");
        }

        return null;
    }

    private void clearToken() throws IOException, GoogleAuthException {
        GoogleAuthUtil.clearToken(mAuthFlow.getActivity(), mEmail);
    }

}
