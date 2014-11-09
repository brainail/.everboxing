package org.brainail.Everboxing.auth;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.brainail.Everboxing.utils.Plogger;

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
