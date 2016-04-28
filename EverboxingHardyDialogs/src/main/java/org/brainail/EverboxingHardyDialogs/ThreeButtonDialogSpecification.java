package org.brainail.EverboxingHardyDialogs;

import android.os.Bundle;

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
 * <br/><br/>
 *
 * Dialog specification with three buttons: positive button, neutral button, negative button <br/>
 * We extends from the {@link TwoButtonDialogSpecification} because this one
 * contains all info from the {@link TwoButtonDialogSpecification} <br/>
 * - neutral button text <br/>
 * - action request code for neutral button  <br/>
 *
 * @author emalyshev
 */
public class ThreeButtonDialogSpecification extends TwoButtonDialogSpecification {

    private final String mNeutralButton;
    private final int mNeutralButtonId;
    private final int mNeutralActionRequestCode;
    private final boolean mDisableDismissOnNeutralButton;

    protected ThreeButtonDialogSpecification (final Builder<?> builder) {
        super (builder);
        mNeutralButton = builder.neutralButton;
        mNeutralButtonId = builder.neutralButtonId;
        mNeutralActionRequestCode = builder.neutralActionRequestCode;
        mDisableDismissOnNeutralButton = builder.disableDismissOnNeutralButton;
    }

    public static class Builder <T extends Builder<T>> extends TwoButtonDialogSpecification.Builder<T> {

        private String neutralButton;
        private int neutralButtonId = HardyDialogFragment.NO_RESOURCE_ID;
        private int neutralActionRequestCode;
        private boolean disableDismissOnNeutralButton = false;

        protected Builder () {
            super ();
        }

        protected Builder (final ThreeButtonDialogSpecification specification) {
            super (specification);
            neutralButton = specification.mNeutralButton;
            neutralButtonId = specification.mNeutralButtonId;
            neutralActionRequestCode = specification.mNeutralActionRequestCode;
            disableDismissOnNeutralButton = specification.mDisableDismissOnNeutralButton;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // Use the predefined request code for neutral button
            neutralActionRequestCode (HardyDialogFragment.ActionRequestCode.NEUTRAL);
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

        /**
         * Right now it works only for custom buttons.
         */
        public T disableDismissOnNeutralButton () {
            this.disableDismissOnNeutralButton = true;
            return self ();
        }

        public T neutralButton (final int stringId) {
            return neutralButton (HardyDialogsContext.get ().getString (stringId));
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
        args.putBoolean (Args.DISABLE_DISMISS_ON_NEUTRAL_BUTTON, mDisableDismissOnNeutralButton);
        super.fillBundle (args);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof ThreeButtonDialogSpecification)) {
            return false;
        }

        if (! super.equals (o)) {
            return false;
        }

        ThreeButtonDialogSpecification that = (ThreeButtonDialogSpecification) o;

        if (mNeutralButtonId != that.mNeutralButtonId) {
            return false;
        }

        return mNeutralButton != null ? mNeutralButton.equals (that.mNeutralButton) : that.mNeutralButton == null;
    }

    @Override
    public int hashCode () {
        int result = super.hashCode ();
        result = 31 * result + (mNeutralButton != null ? mNeutralButton.hashCode () : 0);
        result = 31 * result + mNeutralButtonId;
        return result;
    }

}
