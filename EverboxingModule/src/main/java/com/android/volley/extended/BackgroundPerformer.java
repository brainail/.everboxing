package com.android.volley.extended;

import android.os.SystemClock;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

/**
 * This file is part of Everboxing modules. <br/><br/>

 * The MIT License (MIT) <br/><br/>

 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>

 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>

 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class BackgroundPerformer implements Network {

    @Override
    public NetworkResponse performRequest(final Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            try {
                final NetworkResponse response = null/* = request.doInBackground() */;
                logRequest(SystemClock.elapsedRealtime() - requestStart, request);
                return response;
            } catch (Throwable error) {
                attemptRetryOnError("background", request, new VolleyError(error));
            }
        }
    }

    // Logs requests that took over 3000 ms to complete.
    private void logRequest(final long requestLifetime, final Request<?> request) {
        if (VolleyLog.DEBUG && requestLifetime > 3000) {
            VolleyLog.d(
                "HTTP response for request=<%s> [lifetime=%d], [size=%s], [retryCount=%s]",
                request, requestLifetime, "unknown", request.getRetryPolicy().getCurrentRetryCount()
            );
        }
    }

    // Attempts to prepare the request for a retry. If there are no more attempts remaining in the
    // request's retry policy, a timeout exception is thrown.
    private static void attemptRetryOnError(final String log, final Request<?> request, final VolleyError error) throws VolleyError {
        final RetryPolicy retryPolicy = request.getRetryPolicy();
        final int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(error);
        } catch (VolleyError caughtError) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", log, oldTimeout));
            throw caughtError;
        }

        request.addMarker(String.format("%s-retry [timeout=%s]", log, oldTimeout));
    }

}
