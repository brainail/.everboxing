package org.brainail.Everboxing.utils.extensions

import android.content.IntentFilter

public inline fun intentFilter(body: IntentFilter.() -> Unit): IntentFilter {
    val intentFilter = IntentFilter()
    intentFilter.body()
    return intentFilter
}