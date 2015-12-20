package org.brainail.EverboxingHardyDialogs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class HardyDialogsHelper {

    public static void dismissIsolated (final Context context, final BaseHardyDialogsCode code) {
        final Intent intent = new Intent (RemoteHardyDialogsActivity.FilterAction.HIDE_REMOTE_DIALOG);
        intent.putExtra (RemoteHardyDialogsActivity.FilterExtra.DIALOG_CODE, code);
        context.sendBroadcast (intent);
    }

    public static void dismissDialog (final Fragment fragment, final BaseHardyDialogsCode code) {
        if (! dismissDialog (fragment.getChildFragmentManager (), code)) {
            dismissDialog (fragment.getFragmentManager (), code);
        }
    }

    public static void dismissDialogAllowingStateLoss (final Fragment fragment, final BaseHardyDialogsCode code) {
        if (! dismissDialogAllowingStateLoss (fragment.getChildFragmentManager (), code)) {
            dismissDialogAllowingStateLoss (fragment.getFragmentManager (), code);
        }
    }

    public static boolean dismissDialog (final FragmentManager fragmentManager, final BaseHardyDialogsCode code) {
        final HardyDialogFragment dialog = findDialog (fragmentManager, code);

        if (null != dialog) {
            dialog.dismiss ();
            return true;
        }

        return false;
    }

    public static boolean dismissDialogAllowingStateLoss (final FragmentManager fragmentManager, final BaseHardyDialogsCode code) {
        final HardyDialogFragment dialog = findDialog (fragmentManager, code);

        if (null != dialog) {
            dialog.dismissAllowingStateLoss ();
            return true;
        }

        return false;
    }

    public static HardyDialogFragment findDialog (final FragmentManager fragmentManager, final BaseHardyDialogsCode code) {
        final Fragment currentDialog = fragmentManager.findFragmentByTag (code.managerTag ());

        if (currentDialog instanceof HardyDialogFragment) {
            return (HardyDialogFragment) currentDialog;
        } else {
            return null;
        }
    }

}
