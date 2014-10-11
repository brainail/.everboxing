package org.brainail.Everboxing.utils;

import android.content.Intent;

import com.google.android.gms.common.AccountPicker;

import static org.brainail.Everboxing.auth.AuthorizationFlow.ACCOUNT_TYPES;

/**
 * User: brainail<br/>
 * Date: 11.10.14<br/>
 * Time: 14:44<br/>
 */
public final class ToolAuth {

    public static Intent formChooseGoogleAccountsIntent() {
        return AccountPicker.newChooseAccountIntent(null, null, ACCOUNT_TYPES, false, null, null, null, null);
    }

}
