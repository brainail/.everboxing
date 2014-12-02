package org.brainail.Everboxing.utils.manager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.brainail.Everboxing.JApplication;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
public final class VolleyManager {

    public static class RequestTag {
        public static final String AUTH = "org.brainail.Everboxing.authRequestTag";
    }

    private RequestQueue mAuthRequestQueue;

    private VolleyManager() {
        initializeQueues();
    }

    private static class LazyHolder {
        private static final VolleyManager INSTANCE = new VolleyManager();
    }

    public static VolleyManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void initializeQueues() {
        mAuthRequestQueue = Volley.newRequestQueue(JApplication.appContext());
    }

    public <T> void addAuthRequest(final Request<T> request) {
        request.setTag(RequestTag.AUTH);
        mAuthRequestQueue.add(request);
    }

    public void cancelAuthRequests() {
        mAuthRequestQueue.cancelAll(RequestTag.AUTH);
    }

}
