package itkach.aard2.utils;

import org.brainail.EverboxingTools.utils.PooLogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {

    public static int compare(long l1, long l2) {
        return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        try {
            Collections.sort(list);
        } catch(Exception e) {
            PooLogger.logW(e, "Error while sorting");
        }
    };

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        try {
            Collections.sort(list, comparator);
        }
        catch(Exception e) {
            PooLogger.logW(e, "Error while sorting");
        }
    }

}