package org.brainail.Everboxing.auth;

import android.app.Activity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

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
