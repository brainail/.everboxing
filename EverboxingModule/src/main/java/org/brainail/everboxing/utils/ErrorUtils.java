package org.brainail.Everboxing.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * User: brainail<br/>
 * Date: 02.08.14<br/>
 * Time: 18:07<br/>
 */
public final class ErrorUtils {

    public static String toString(final Throwable error) {
        final Writer errorWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(errorWriter);

        try {
            if (null != error) {
                error.printStackTrace(printWriter);
                return errorWriter.toString();
            }
        } finally{
            printWriter.close();
        }

        return StringUtils.EMPTY;
    }

}
