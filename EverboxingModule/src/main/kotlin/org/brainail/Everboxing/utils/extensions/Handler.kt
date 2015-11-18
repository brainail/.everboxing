package org.brainail.Everboxing.utils.extensions

import android.os.Handler
import android.os.Message

public fun Handler.post(action: () -> Unit): Boolean = post(Runnable(action))
public fun Handler.atFrontOfQueue(action: () -> Unit): Boolean = postAtFrontOfQueue(Runnable(action))
public fun Handler.atTime(uptimeMillis: Long, action: () -> Unit): Boolean = postAtTime(Runnable(action), uptimeMillis)
public fun Handler.deylaed(delayMillis: Long, action: () -> Unit): Boolean = postDelayed(Runnable(action), delayMillis)
public fun handler(handleMessage: (Message) -> Boolean): Handler {
    return Handler { p0 -> if (p0 == null) false else handleMessage(p0) }
}