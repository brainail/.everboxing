package org.brainail.EverboxingTools.utils;

import android.support.annotation.IntRange;

import static java.lang.Thread.currentThread;

/**
 * @author emalyshev
 */
public class TraceHelper {

    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        // Finds out the index of "this code" in the returned stack trace
        // because it differs in JDK 1.5 and 1.6
        int elementIndex = 0;
        for (final StackTraceElement stackTraceElement : currentThread ().getStackTrace ()) {
            ++ elementIndex;
            if (stackTraceElement.getClassName ().equals (TraceHelper.class.getName ())) {
                break;
            }
        }

        CLIENT_CODE_STACK_INDEX = elementIndex;
    }

    public static String currentMethod () {
        return currentThread ().getStackTrace () [CLIENT_CODE_STACK_INDEX].getMethodName ();
    }

    public static String previousMethod (final @IntRange(from = -9, to = -1) int shift) {
        return currentThread ().getStackTrace () [CLIENT_CODE_STACK_INDEX + (- shift)].getMethodName ();
    }

    public static String previousCallLine (final @IntRange(from = -9, to = -1) int shift) {
        final StackTraceElement trace = Thread.currentThread ().getStackTrace () [CLIENT_CODE_STACK_INDEX + (- shift)];
        return trace.getFileName () + ":" + trace.getLineNumber ();
    }

}
