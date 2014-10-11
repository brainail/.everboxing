package org.brainail.Everboxing.auth;

import android.os.AsyncTask;

/**
 * User: brainail<br/>
 * Date: 11.10.14<br/>
 * Time: 15:43<br/>
 */
public abstract class AbstractAuthTask extends AsyncTask<Void, Void, Void> {

    protected final AuthorizationFlow mAuthFlow;

    AbstractAuthTask(final AuthorizationFlow authFlow) {
        mAuthFlow = authFlow;
    }

}

