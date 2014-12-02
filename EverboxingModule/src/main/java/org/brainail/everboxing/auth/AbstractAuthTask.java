package org.brainail.Everboxing.auth;

import android.os.AsyncTask;

public abstract class AbstractAuthTask extends AsyncTask<Void, Void, Void> {

    protected final AuthorizationFlow mAuthFlow;

    AbstractAuthTask(final AuthorizationFlow authFlow) {
        mAuthFlow = authFlow;
    }

}

