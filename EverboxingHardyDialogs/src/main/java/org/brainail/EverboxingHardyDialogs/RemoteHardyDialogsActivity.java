package org.brainail.EverboxingHardyDialogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;

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
public class RemoteHardyDialogsActivity extends AppCompatActivity {

    public static abstract class FilterAction {
        public static final String HIDE_REMOTE_DIALOG = "org.brainail.EverboxingHardyDialogs.filter#hide.remote.dialog";
    }

    private static IntentFilter FILTER;

    static {
        FILTER = new IntentFilter ();
        FILTER.addAction (FilterAction.HIDE_REMOTE_DIALOG);
    }

    public static abstract class Extra {
        public static final String DIALOG_TYPE = "org.brainail.EverboxingHardyDialogs.extra#dialog.type";
    }

    public static abstract class Value {
        public static final String REMOTE_DIALOG = "org.brainail.EverboxingHardyDialogs.extra_value#remote.dialog";
    }

    public static abstract class FilterExtra {
        public static final String DIALOG_CODE = "org.brainail.EverboxingHardyDialogs.extra#dialog.code";
    }

    public static abstract class FilterValue {}

    static final String INTENT_ACTION = "org.brainail.EverboxingHardyDialogs.action.view.remote.dialog";

    public static Intent getIntent (String type) {
        return new Intent (INTENT_ACTION).putExtra (Extra.DIALOG_TYPE, type);
    }

    private LinkedList<Intent> mPendingDialogs = new LinkedList<> ();

    private boolean mIsRegistered = false;

    @Override
    protected void onStart () {
        super.onStart ();
        resolveIntent (getIntent (), false);
    }

    @Override
    protected void onNewIntent (Intent intent) {
        resolveIntent (intent, true);
        super.onNewIntent (intent);
    }

    private void resolveIntent (final Intent intent, final boolean isNew) {
        setIntent (intent);
        registerReceiver ();

        final String type = intent.getStringExtra (Extra.DIALOG_TYPE);

        // Try to show an isolated dialog
        if (Value.REMOTE_DIALOG.equals (type)) {
            if (isNew || mPendingDialogs.isEmpty ()) {
                mPendingDialogs.addLast (intent);
            }

            // If we have only one candidate to show then do it
            if (1 == mPendingDialogs.size ()) {
                HardyDialogFragment.handleIsolated (this, mPendingDialogs.getFirst ());
            }

            return;
        }
    }

    @Override
    public void finish () {
        // Ok we've finished with current dialog
        if (! mPendingDialogs.isEmpty ()) {
            mPendingDialogs.removeFirst ();
        }

        // If we have something to show then do it otherwise bye-bye
        if (! mPendingDialogs.isEmpty ()) {
            HardyDialogFragment.handleIsolated (this, mPendingDialogs.getFirst ());
        } else {
            super.finish ();
        }
    }

    @Override
    protected void onPause () {
        super.onPause ();

        // To avoid flickering
        if (isFinishing ()) {
            overridePendingTransition (0, R.anim.non_flickering_slide_out_right);
        }
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver ();
        super.onDestroy ();
    }

    private void registerReceiver () {
        if (! mIsRegistered) {
            registerReceiver (mReceiver, FILTER);
            mIsRegistered = true;
        }
    }

    private void unregisterReceiver () {
        if (mIsRegistered) {
            try {
                unregisterReceiver (mReceiver);
            } catch (final Exception exception) {
                // Workaround for java.lang.IllegalArgumentException: Receiver not registered
            }

            mIsRegistered = false;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (FilterAction.HIDE_REMOTE_DIALOG.equals (intent.getAction ())) {
                hideHardyDialog ((BaseHardyDialogsCode) intent.getSerializableExtra (FilterExtra.DIALOG_CODE));
            }
        }
    };

    private void hideHardyDialog (final BaseHardyDialogsCode code) {
        HardyDialogsHelper.dismissDialog (getSupportFragmentManager (), code);
    }

}
