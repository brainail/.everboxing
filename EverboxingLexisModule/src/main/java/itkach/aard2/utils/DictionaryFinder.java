package itkach.aard2.utils;

import org.brainail.EverboxingTools.utils.PooLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import itkach.aard2.slob.SlobDescriptor;

public class DictionaryFinder {

    private final static String T = "DictionaryFinder";

    private Set<String> excludedScanDirs = new HashSet<String>() {
        {
            add("/proc");
            add("/dev");
            add("/etc");
            add("/sys");
            add("/acct");
            add("/cache");
        }
    };

    private boolean cancelRequested;

    private FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File dir, String filename) {
            return filename.toLowerCase().endsWith(".slob") || new File(dir, filename).isDirectory();
        }
    };

    private List<File> discover() {
        File scanRoot = new File("/");
        List<File> result = new ArrayList<File>();
        result.addAll(scanDir(scanRoot));
        return result;
    }

    private List<File> scanDir(File dir) {
        if (cancelRequested) {
            return Collections.emptyList();
        }
        String absolutePath = dir.getAbsolutePath();
        if (excludedScanDirs.contains(absolutePath)) {
            PooLogger.logD(String.format("%s is excluded", absolutePath));
            return Collections.emptyList();
        }

        if (dir.isHidden()) {
            PooLogger.logD(String.format("%s is hidden", absolutePath));
            return Collections.emptyList();
        }
        PooLogger.logD("Scanning " + absolutePath);
        List<File> candidates = new ArrayList<File>();
        File[] files = dir.listFiles(fileFilter);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (cancelRequested) {
                    break;
                }
                File file = files[i];
                if (file.isDirectory()) {
                    candidates.addAll(scanDir(file));
                } else {
                    if (!file.isHidden() && file.isFile()) {
                        candidates.add(file);
                    }
                }
            }
        }
        return candidates;
    }

    public synchronized List<SlobDescriptor> findDictionaries() {
        cancelRequested = false;
        PooLogger.logD("starting dictionary discovery");
        long t0 = System.currentTimeMillis();
        List<File> candidates = discover();
        PooLogger.logD("dictionary discovery took " + (System.currentTimeMillis() - t0));
        List<SlobDescriptor> descriptors = new ArrayList<SlobDescriptor>();
        Set<String> seen = new HashSet<String>();
        for (File f : candidates) {
            SlobDescriptor sd = SlobDescriptor.fromFile(f);
            if (sd.id != null && seen.contains(sd.id)) {
                continue;
            }
            seen.add(sd.id);
            long currentTime = System.currentTimeMillis();
            sd.createdAt = currentTime;
            sd.lastAccess = currentTime;
            descriptors.add(sd);
        }
        return descriptors;
    }

    public void cancel() {
        cancelRequested = true;
    }

}
