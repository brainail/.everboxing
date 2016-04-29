package org.brainail.EverboxingTools.utils.tool;

import android.os.Parcel;
import android.os.ResultReceiver;

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
public final class ToolComponent {

    /**
     * Workaround for {@link android.os.Parcelable} objects.
     *
     * @see <a href="http://stackoverflow.com/questions/5743485/android-resultreceiver-across-packages">
     *          http://stackoverflow.com/questions/5743485/android-resultreceiver-across-packages
     *      </a>
     */
    public static ResultReceiver prepareToTransfer(final ResultReceiver receiver) {
        ResultReceiver resultReceiver = null;

        if (null != receiver) {
            final Parcel parcel = Parcel.obtain();
            receiver.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            resultReceiver = ResultReceiver.CREATOR.createFromParcel(parcel);
            parcel.recycle();
        }

        return resultReceiver;
    }

}
