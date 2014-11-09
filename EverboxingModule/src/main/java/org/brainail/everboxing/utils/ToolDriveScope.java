package org.brainail.Everboxing.utils;

import com.google.api.services.drive.DriveScopes;

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
