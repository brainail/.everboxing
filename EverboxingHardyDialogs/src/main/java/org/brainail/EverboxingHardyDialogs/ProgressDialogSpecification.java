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
 */
class ProgressDialogSpecification extends BaseDialogSpecification {

    private final boolean mIsIndeterminate;

    protected ProgressDialogSpecification (final Builder<?> builder) {
        super (builder);
        mIsIndeterminate = builder.isIndeterminate;
    }

    public static class Builder <T extends Builder<T>> extends BaseDialogSpecification.Builder<T> {

        private boolean isIndeterminate;

        protected Builder () {
            super ();
        }

        protected Builder (final ProgressDialogSpecification specification) {
            super (specification);
            isIndeterminate = specification.mIsIndeterminate;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // We don't know anything about a progress value
            indeterminate (false);
        }

        public T indeterminate (final boolean isIndeterminate) {
            this.isIndeterminate = isIndeterminate;
            return self ();
        }

        @Override
        ProgressDialogSpecification build () {
            return new ProgressDialogSpecification (this);
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
        args.putBoolean (Args.HAS_PROGRESS, true);
        args.putBoolean (Args.IS_INDETERMINATE_PROGRESS, mIsIndeterminate);
        super.fillBundle (args);
    }

}

