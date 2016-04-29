package org.brainail.EverboxingTools.utils;

/**
 * @author emalyshev
 */
public class TraceHelper {

    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        // Finds out the index of "this code" in the returned stack trace
        // because it differs in JDK 1.5 and 1.6
        int elementIndex = 0;
        for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            ++ elementIndex;
            if (stackTraceElement.getClassName().equals(TraceHelper.class.getName())) {
                break;
            }
        }

        CLIENT_CODE_STACK_INDEX = elementIndex;
    }

    public static String currentMethod() {
        return Thread.currentThread().getStackTrace() [CLIENT_CODE_STACK_INDEX].getMethodName();
    }

    public static String previousMethod() {
        return Thread.currentThread().getStackTrace() [CLIENT_CODE_STACK_INDEX + 1].getMethodName();
    }

}
