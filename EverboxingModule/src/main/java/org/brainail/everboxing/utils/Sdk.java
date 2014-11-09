package org.brainail.Everboxing.utils;

import android.os.Build;

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
public final class Sdk {

    /**
     * @see android.os.Build.VERSION_CODES#GINGERBREAD
     */
    public static final int GINGERBREAD = Build.VERSION_CODES.GINGERBREAD;

    /**
     * @see android.os.Build.VERSION_CODES#GINGERBREAD_MR1
     */
    public static final int GINGERBREAD_MR1 = Build.VERSION_CODES.GINGERBREAD_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB
     */
    public static final int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB_MR1
     */
    public static final int HONEYCOMB_MR1 = Build.VERSION_CODES.HONEYCOMB_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#HONEYCOMB_MR2
     */
    public static final int HONEYCOMB_MR2 = Build.VERSION_CODES.HONEYCOMB_MR2;

    /**
     * @see android.os.Build.VERSION_CODES#ICE_CREAM_SANDWICH
     */
    public static final int ICE_CREAM_SANDWICH = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    /**
     * @see android.os.Build.VERSION_CODES#ICE_CREAM_SANDWICH_MR1
     */
    public static final int ICE_CREAM_SANDWICH_MR1 = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN
     */
    public static final int JELLY_BEAN = Build.VERSION_CODES.JELLY_BEAN;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN_MR1
     */
    public static final int JELLY_BEAN_MR1 = Build.VERSION_CODES.JELLY_BEAN_MR1;

    /**
     * @see android.os.Build.VERSION_CODES#JELLY_BEAN_MR2
     */
    public static final int JELLY_BEAN_MR2 = Build.VERSION_CODES.JELLY_BEAN_MR2;

    /**
     * @see android.os.Build.VERSION_CODES#KITKAT
     */
    public static final int KITKAT = Build.VERSION_CODES.KITKAT;

    /**
     * @see android.os.Build.VERSION_CODES#LOLLIPOP
     */
    public static final int LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;

    public static boolean isSdkSupported(final int sdkVersion) {
        return Build.VERSION.SDK_INT >= sdkVersion;
    }

}
