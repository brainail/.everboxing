package org.brainail.EverboxingHardyDialogs;

import android.os.Parcel;
import android.os.Parcelable;

import org.brainail.EverboxingHardyDialogs.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;

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
public final class HardyDialogVerifier {

    public static boolean verify (final BaseDialogSpecification dialogSpecification, final boolean isIsolated) {
        if (isIsolated) {
            if (!verifyUselessCallbacks (dialogSpecification)) {
                return false;
            }
        }

        verifyRestorableOption (dialogSpecification);

        if (!verifyAttachedData (dialogSpecification.attachedData (), dialogSpecification.code ())) {
            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException ("Your attached data isn't correct/transferable");
            }

            return false;
        }

        if (dialogSpecification.isRestorable ()
                && dialogSpecification.hasCallbacks ()
                && null != dialogSpecification.isolatedHandler ()) {

            if (!verifyAttachedData (dialogSpecification.isolatedHandler (), dialogSpecification.code ())) {
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException ("Your IsolatedDialogHandler isn't correct/transferable");
                }

                return false;
            }

            if (!Modifier.isStatic (dialogSpecification.isolatedHandler ().getClass ().getModifiers ())) {
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException ("Your IsolatedDialogHandler isn't an instance of static class");
                }

                return false;
            }
        }

        return true;
    }

    private static boolean verifyUselessCallbacks (final BaseDialogSpecification dialogSpecification) {
        if (dialogSpecification.hasCallbacks () && null == dialogSpecification.isolatedHandler ()) {
            if (BuildConfig.DEBUG) {
                throw new IllegalArgumentException ("Use only IsolatedDialogHandler as a callback for isolated dialogs");
            }

            return false;
        }

        return true;
    }

    private static void verifyRestorableOption (final BaseDialogSpecification dialogSpecification) {
        if (!dialogSpecification.isRestorable () && BuildConfig.DEBUG) {
            // LogWarn("You are going to show a non-restorable dialog (code: ?). Please recheck it.", dialogDescriptor.code());
        }
    }

    private static boolean verifyAttachedData (final Object attachedData, DialogCode code) {
        try {
            if (attachedData instanceof Serializable) {
                ObjectOutputStream objectStream = null;
                try {
                    objectStream = new ObjectOutputStream (new ByteArrayOutputStream ());
                    objectStream.writeObject (attachedData);
                } finally {
                    IoUtils.close (objectStream);
                }
            } else if (attachedData instanceof Parcelable) {
                ((Parcelable) attachedData).writeToParcel (Parcel.obtain (), 0);
            }
        } catch (final Exception exception) {
            // LogError(exception, "verifyAttachedData failed, dialog code: ?", code);
            return false;
        }

        return true;
    }

}
