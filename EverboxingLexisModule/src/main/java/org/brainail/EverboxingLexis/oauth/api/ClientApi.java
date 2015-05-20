package org.brainail.EverboxingLexis.oauth.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
public abstract class ClientApi <T> implements UserInfoApi.AuthCallback {

    public interface Supportable {
        public boolean usePlayServices ();
        public ClientApi getPlayServices ();
    }

    public abstract T api ();

    public void connect () {}

    public void disconnect () {}

    public void revoke () {}

    protected T initAPI () {
        return null;
    }

    public boolean isConnected () {
        return false;
    }

    public UserInfoApi.AuthCallback authorizer () {
        return null;
    }

    public UserInfoApi formUserInfo () {
        return null;
    }

    public boolean handleOnResult (final int requestCode, final int resultCode, final Intent data) {
        return false;
    }

    public void onStart () {}

    public void onStop () {}

    public void onDestroy () {}

    public void onSave (final Bundle outState) {}

    public boolean useOn (final Activity scene) {
        return false;
    }

    @Override
    public void onAuthSucceeded (final UserInfoApi userInfo) {
        final UserInfoApi.AuthCallback authorizer = authorizer ();
        if (null != authorizer) {
            authorizer.onAuthSucceeded (userInfo);
        }
    }

    @Override
    public void onUnauthSucceeded () {
        final UserInfoApi.AuthCallback authorizer = authorizer ();
        if (null != authorizer) {
            authorizer.onUnauthSucceeded ();
        }
    }

}


