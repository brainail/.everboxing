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
class ThreeButtonDialogSpecification extends TwoButtonDialogSpecification {

    private final String mNeutralButton;
    private final int mNeutralButtonId;
    private final int mNeutralActionRequestCode;

    protected ThreeButtonDialogSpecification (final Builder<?> builder) {
        super (builder);
        mNeutralButton = builder.neutralButton;
        mNeutralButtonId = builder.neutralButtonId;
        mNeutralActionRequestCode = builder.neutralActionRequestCode;
    }

    public static class Builder <T extends Builder<T>> extends TwoButtonDialogSpecification.Builder<T> {

        private String neutralButton;
        private int neutralButtonId = HardyDialogFragment.NO_RESOURCE_ID;
        private int neutralActionRequestCode;

        protected Builder () {
            super ();
        }

        protected Builder (final ThreeButtonDialogSpecification specification) {
            super (specification);
            neutralButton = specification.mNeutralButton;
            neutralButtonId = specification.mNeutralButtonId;
            neutralActionRequestCode = specification.mNeutralActionRequestCode;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // Use the predefined request code for neutral button
            neutralActionRequestCode (ActionRequestCode.NEUTRAL);
        }

        @Override
        public T noButtons () {
            customNeutralButton (HardyDialogFragment.NO_RESOURCE_ID, null);
            return super.noButtons ();
        }

        public T neutralButton (final String neutralButton) {
            this.neutralButton = neutralButton;
            return self ();
        }

        public T neutralButton (final int stringId) {
            return neutralButton (ViberApplication.getInstance ().getString (stringId));
        }

        public T customNeutralButton (final int resourceId, final int stringId) {
            this.neutralButtonId = resourceId;
            return neutralButton (stringId);
        }

        public T customNeutralButton (final int resourceId, final String neutralButton) {
            this.neutralButtonId = resourceId;
            return neutralButton (neutralButton);
        }

        public T neutralActionRequestCode (final int neutralActionRequestCode) {
            this.neutralActionRequestCode = neutralActionRequestCode;
            return self ();
        }

        @Override
        ThreeButtonDialogSpecification build () {
            return new ThreeButtonDialogSpecification (this);
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
        args.putString (Args.NEUTRAL_BUTTON, mNeutralButton);
        args.putInt (Args.NEUTRAL_BUTTON_ID, mNeutralButtonId);
        args.putInt (Args.NEUTRAL_ACTION_REQUEST_CODE, mNeutralActionRequestCode);
        super.fillBundle (args);
    }

}

