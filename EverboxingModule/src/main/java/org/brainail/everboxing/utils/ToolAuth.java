package org.brainail.Everboxing.utils;

import android.content.Intent;

import com.google.android.gms.common.AccountPicker;

import static org.brainail.Everboxing.auth.AuthorizationFlow.ACCOUNT_TYPES;

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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public final class ToolAuth {

    public static Intent formChooseGoogleAccountsIntent() {
        return AccountPicker.newChooseAccountIntent(null, null, ACCOUNT_TYPES, false, null, null, null, null);
    }

}
