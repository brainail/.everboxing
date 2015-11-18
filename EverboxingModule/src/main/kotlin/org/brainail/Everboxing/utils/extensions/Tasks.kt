package org.brainail.Everboxing.utils.extensions

import android.os.Looper
import android.os.Handler
import org.brainail.Everboxing.utils.extensions.post
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

private val uiHandler = Handler(Looper.getMainLooper())

public fun Any?.mainThread(runnable: () -> Unit) {
    uiHandler.post(runnable)
}

public fun Any?.async(runnable: () -> Unit) {
    Thread(runnable).start()
}

public fun Any?.async(runnable: () -> Unit, executor: ExecutorService): Future<out Any?> {
    return executor.submit(runnable)
}
