package org.brainail.Everboxing.utils;

import android.os.Build;

/**
 * User: brainail<br/>
 * Date: 13.07.14<br/>
 * Time: 14:16<br/>
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
