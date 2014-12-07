package org.brainail.Everboxing.ui.views;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 *
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class GooglePsErrorDialog extends DialogFragment {

    public static final String MANAGER_TAG = "org.brainail.Everboxing.GooglePlayServicesErrorDialogTag";

    public static class Args {
        public static final String STATUS_CODE = "org.brainail.Everboxing.statusCodeArg";
        public static final String REQUEST_CODE = "org.brainail.Everboxing.requestCodeArg";
    }

    private int mStatusCode;
    private int mRequestCode;

    public static void show(final Activity activity, final int statusCode, final int requestCode) {
        if (null != activity) {
            newInstance(statusCode, requestCode).show(activity.getFragmentManager(), MANAGER_TAG);
        }
    }

    public static void show(final Activity activity, final int statusCode) {
        show(activity, statusCode, 0);
    }

    public static GooglePsErrorDialog newInstance(final int statusCode, final int requestCode) {
        final GooglePsErrorDialog dialog = new GooglePsErrorDialog();

        final Bundle args = new Bundle();
        args.putInt(Args.STATUS_CODE, statusCode);
        args.putInt(Args.REQUEST_CODE, requestCode);
        dialog.setArguments(args);

        return dialog;
    }

    public static GooglePsErrorDialog newInstance(final int statusCode) {
        return newInstance(statusCode, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        mStatusCode = args.getInt(Args.STATUS_CODE, ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
        mRequestCode = args.getInt(Args.REQUEST_CODE, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Workaround for
        // android.view.WindowLeaked:
        //     Activity has leaked window com.android.internal.policy.impl.PhoneWindow$DecorView@41522b80
        //     that was originally added here (Dialog#show())
        final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(mStatusCode, getActivity(), mRequestCode);
        return null != errorDialog ? errorDialog : super.onCreateDialog(savedInstanceState);
    }

}