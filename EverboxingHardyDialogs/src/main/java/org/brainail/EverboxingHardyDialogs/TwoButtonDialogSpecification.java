package org.brainail.EverboxingHardyDialogs;

import android.os.Bundle;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.ActionRequestCode;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.Args;

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
public class TwoButtonDialogSpecification extends OneButtonDialogSpecification {

    private final String mNegativeButton;
    private final int mNegativeButtonId;
    private final int mNegativeActionRequestCode;

    protected TwoButtonDialogSpecification (final Builder<?> builder) {
        super (builder);
        mNegativeButton = builder.negativeButton;
        mNegativeButtonId = builder.negativeButtonId;
        mNegativeActionRequestCode = builder.negativeActionRequestCode;
    }

    public static class Builder <T extends Builder<T>> extends OneButtonDialogSpecification.Builder<T> {

        private String negativeButton;
        private int negativeButtonId = HardyDialogFragment.NO_RESOURCE_ID;
        private int negativeActionRequestCode;

        protected Builder () {
            super ();
        }

        protected Builder (final TwoButtonDialogSpecification specification) {
            super (specification);
            negativeButton = specification.mNegativeButton;
            negativeButtonId = specification.mNegativeButtonId;
            negativeActionRequestCode = specification.mNegativeActionRequestCode;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // Negative button is "Cancel" by default
            negativeButton (R.string.dialog_button_cancel);
            // Use the predefined request code for negative button
            negativeActionRequestCode (ActionRequestCode.NEGATIVE);
        }

        @Override
        public T noButtons () {
            customNegativeButton (HardyDialogFragment.NO_RESOURCE_ID, null);
            return super.noButtons ();
        }

        public T negativeButton (final String negativeButton) {
            this.negativeButton = negativeButton;
            return self ();
        }

        public T negativeButton (final int stringId) {
            return negativeButton (HardyDialogsContext.get ().getString (stringId));
        }

        public T customNegativeButton (final int resourceId, final int stringId) {
            this.negativeButtonId = resourceId;
            return negativeButton (stringId);
        }

        public T customNegativeButton (final int resourceId, final String negativeButton) {
            this.negativeButtonId = resourceId;
            return negativeButton (negativeButton);
        }

        public T negativeActionRequestCode (final int negativeActionRequestCode) {
            this.negativeActionRequestCode = negativeActionRequestCode;
            return self ();
        }

        @Override
        TwoButtonDialogSpecification build () {
            return new TwoButtonDialogSpecification (this);
        }

    }

    @Override
    public Builder<?> basedOn () {
        return new Builder (this);
    }

    @SuppressWarnings ("rawtypes")
    public static Builder<?> create () {
        return new Builder ();
    }

    @Override
    protected void fillBundle (final Bundle args) {
        args.putString (Args.NEGATIVE_BUTTON, mNegativeButton);
        args.putInt (Args.NEGATIVE_BUTTON_ID, mNegativeButtonId);
        args.putInt (Args.NEGATIVE_ACTION_REQUEST_CODE, mNegativeActionRequestCode);
        super.fillBundle (args);
    }

}

