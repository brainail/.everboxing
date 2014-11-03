package org.brainail.Everboxing.utils;

import com.google.api.services.drive.DriveScopes;

/**
 * User: brainail<br/>
 * Date: 02.08.14<br/>
 * Time: 18:34<br/>
 */
public final class ToolDriveScope {

    public static final String USER_INFO = "https://www.googleapis.com/auth/userinfo.profile";
    public static final String APP_DATA = DriveScopes.DRIVE_APPDATA;

    public static String formFullScope() {
        final StringBuilder fullScope = new StringBuilder("oauth2:");

        fullScope.append(USER_INFO).append(ToolStrings.SPACE);
        fullScope.append(APP_DATA);

        return fullScope.toString();
    }

}
