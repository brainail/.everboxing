package org.brainail.Everboxing.ui.notice;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Notice implements Parcelable {

    static final String EXTRA_NOTICES = "org.brainail.Everboxing.ui.notices.Saved";

    final String message;
    final String action;
    final Parcelable token;
    final long duration;
    final long style;

    Notice(final NoticeBar.Builder provider) {
        message = provider.body;
        action = provider.action;
        token = provider.token;
        duration = provider.duration;
        style = provider.style;
    }

    Notice(final Parcel p) {
        message = p.readString();
        action = p.readString();
        token = p.readParcelable(p.getClass().getClassLoader());
        duration = p.readLong();
        style = p.readLong();
    }

    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(message);
        out.writeString(action);
        out.writeParcelable(token, 0);
        out.writeLong(duration);
        out.writeLong(style);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Notice> CREATOR = new Parcelable.Creator<Notice>() {

        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        public Notice [] newArray(int size) {
            return new Notice [size];
        }

    };

}