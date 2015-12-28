package itkach.aard2.utils;

import org.brainail.EverboxingLexis.utils.Plogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {

    static final String TAG = Util.class.getSimpleName();

    public static int compare(long l1, long l2) {
        return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        try {
            Collections.sort(list);
        } catch(Exception e) {
            Plogger.logW(e, "Error while sorting");
        }
    };

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        try {
            Collections.sort(list, comparator);
        }
        catch(Exception e) {
            //From http://www.oracle.com/technetwork/java/javase/compatibility-417013.html#source
            /*
            Synopsis: Updated sort behavior for Arrays and Collections may throw an IllegalArgumentException
            Description: The sorting algorithm used by java.util.Arrays.sort and (indirectly) by
                         java.util.Collections.sort has been replaced. The new sort implementation may
                         throw an IllegalArgumentException if it detects a Comparable that violates
                         the Comparable contract. The previous implementation silently ignored such a situation.
                         If the previous behavior is desired, you can use the new system property,
                         java.util.Arrays.useLegacyMergeSort, to restore previous mergesort behavior.
            Nature of Incompatibility: behavioral
            RFE: 6804124
             */
            //Name comparators use ICU collation key comparison. Given Unicode collation complexity
            //it's hard to be sure that collation key comparisons won't trigger an exception. It certainly
            //does at least for some keys in ICU 53.1.
            //Incorrect or no sorting seems preferable than a crashing app.
            //TODO perhaps java.util.Collections.sort shouldn't be used at all
            Plogger.logW(e, "Error while sorting");
        }
    }
}