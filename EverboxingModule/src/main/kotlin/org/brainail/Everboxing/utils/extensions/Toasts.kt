package org.brainail.Everboxing.utils.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import android.app.Fragment
import android.support.v4.app.Fragment as SupportFragment

public fun Context.toast(messageResId: Int) {
    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
}

public fun Context.longToast(messageResId: Int) {
    Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
}

public fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

public fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

public fun Fragment.toast(messageResId: Int) {
    Toast.makeText(activity, messageResId, Toast.LENGTH_SHORT).show()
}

public fun Fragment.longToast(messageResId: Int) {
    Toast.makeText(activity, messageResId, Toast.LENGTH_LONG).show()
}

public fun Fragment.toast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

public fun Fragment.longToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}

public fun SupportFragment.toast(messageResId: Int) {
    Toast.makeText(activity, messageResId, Toast.LENGTH_SHORT).show()
}

public fun SupportFragment.longToast(messageResId: Int) {
    Toast.makeText(activity, messageResId, Toast.LENGTH_LONG).show()
}

public fun SupportFragment.toast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

public fun SupportFragment.longToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}



