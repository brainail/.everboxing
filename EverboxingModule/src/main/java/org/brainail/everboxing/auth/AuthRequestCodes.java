package org.brainail.Everboxing.auth;

/**
* User: brainail<br/>
* Date: 11.10.14<br/>
* Time: 14:25<br/>
*/
public final class AuthRequestCodes {

    private static final int BASE = 1000;
    public static final int PICK_ACCOUNT = BASE;
    public static final int RECOVER_FROM_AUTH_ERROR = BASE + 1;
    public static final int RECOVER_FROM_PLAY_SERVICES_ERROR = BASE + 2;

    public static boolean isErrorRecover(final int code) {
        boolean result = RECOVER_FROM_AUTH_ERROR == code;
        result |= RECOVER_FROM_PLAY_SERVICES_ERROR == code;
        return result;
    }

}
