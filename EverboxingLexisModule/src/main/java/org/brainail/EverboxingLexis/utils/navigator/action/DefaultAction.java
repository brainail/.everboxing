package org.brainail.EverboxingLexis.utils.navigator.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
class DefaultAction implements NavigatorAction {

    private Context mContext;
    private Intent mIntent;

    private Bundle mSharedExtras;

    public DefaultAction (Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void start () {
        startActivity ();
    }

    protected final boolean startActivity () {
        if (mIntent == null) {
            return false;
        }

        if (!(mContext instanceof Activity)) {
            mIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            if (mSharedExtras != null) {
                mIntent.putExtras (mSharedExtras);
            }
            mContext.startActivity (mIntent);
        } catch (final Exception ignored) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public Intent getTargetIntent () {
        return mIntent;
    }

    @Override
    public NavigatorAction setSharedExtras (Bundle extras) {
        mSharedExtras = new Bundle (extras);
        return this;
    }

    protected void setTargetIntent (Intent intent) {
        mIntent = intent;
    }

}
