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
 * Dialog specification with one positive button <br/>
 * We extends from the {@link BaseDialogSpecification} because this one
 * contains all info from the {@link BaseDialogSpecification} <br/>
 * - positive button text <br/>
 * - action request code for positive button  <br/>
 *
 * @author emalyshev
 */
public class OneButtonDialogSpecification extends BaseDialogSpecification {

    private final String mPositiveButton;
    private final int mPositiveButtonId;
    private final int mPositiveActionRequestCode;
    private final boolean mDisableDismissOnPositiveButton;

    protected OneButtonDialogSpecification (final Builder<?> builder) {
        super (builder);
        mPositiveButton = builder.positiveButton;
        mPositiveButtonId = builder.positiveButtonId;
        mPositiveActionRequestCode = builder.positiveActionRequestCode;
        mDisableDismissOnPositiveButton = builder.disableDismissOnPositiveButton;
    }

    // Builder for OneButtonDialogSpecification
    // We extends from the BaseDialogSpecification.Builder because
    // we want to have abilities to set title, body, and so on ...
    public static class Builder <T extends Builder<T>> extends BaseDialogSpecification.Builder<T> {

        private String positiveButton;
        private int positiveButtonId = HardyDialogFragment.NO_RESOURCE_ID;
        private int positiveActionRequestCode;
        private boolean disableDismissOnPositiveButton = false;

        protected Builder () {
            super ();
        }

        protected Builder (final OneButtonDialogSpecification specification) {
            super (specification);
            positiveButton = specification.mPositiveButton;
            positiveButtonId = specification.mPositiveButtonId;
            positiveActionRequestCode = specification.mPositiveActionRequestCode;
            disableDismissOnPositiveButton = specification.mDisableDismissOnPositiveButton;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // Positive button is "Ok" by default
            positiveButton (R.string.dialog_button_ok);
            // Use the predefined request code for positive button
            positiveActionRequestCode (HardyDialogFragment.ActionRequestCode.POSITIVE);
        }

        @Override
        public T noButtons () {
            customPositiveButton (HardyDialogFragment.NO_RESOURCE_ID, null);
            return super.noButtons ();
        }

        public T customPositiveButton (final int resourceId, final int stringId) {
            this.positiveButtonId = resourceId;
            return positiveButton (stringId);
        }

        public T customPositiveButton (final int resourceId, final String positiveButton) {
            this.positiveButtonId = resourceId;
            return positiveButton (positiveButton);
        }

        public T positiveButton (final String positiveButton) {
            this.positiveButton = positiveButton;
            return self ();
        }

        /**
         * Right now it works only for custom buttons.
         */
        public T disableDismissOnPositiveButton () {
            this.disableDismissOnPositiveButton = true;
            return self ();
        }

        public T positiveButton (final int stringId) {
            return positiveButton (HardyDialogsContext.get ().getString (stringId));
        }

        public T positiveActionRequestCode (final int positiveActionRequestCode) {
            this.positiveActionRequestCode = positiveActionRequestCode;
            return self ();
        }

        @Override
        OneButtonDialogSpecification build () {
            return new OneButtonDialogSpecification (this);
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
        args.putString (Args.POSITIVE_BUTTON, mPositiveButton);
        args.putInt (Args.POSITIVE_BUTTON_ID, mPositiveButtonId);
        args.putInt (Args.POSITIVE_ACTION_REQUEST_CODE, mPositiveActionRequestCode);
        args.putBoolean (Args.DISABLE_DISMISS_ON_POSITIVE_BUTTON, mDisableDismissOnPositiveButton);
        super.fillBundle (args);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof OneButtonDialogSpecification)) {
            return false;
        }

        if (! super.equals (o)) {
            return false;
        }

        OneButtonDialogSpecification that = (OneButtonDialogSpecification) o;

        if (mPositiveButtonId != that.mPositiveButtonId) {
            return false;
        }

        return mPositiveButton != null ? mPositiveButton.equals (that.mPositiveButton) : that.mPositiveButton == null;
    }

    @Override
    public int hashCode () {
        int result = super.hashCode ();
        result = 31 * result + (mPositiveButton != null ? mPositiveButton.hashCode () : 0);
        result = 31 * result + mPositiveButtonId;
        return result;
    }

}
