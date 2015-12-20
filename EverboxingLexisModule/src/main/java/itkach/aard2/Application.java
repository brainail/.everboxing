package itkach.aard2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.brainail.EverboxingLexis.JApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import itkach.aard2.callbacks.DictionaryDiscoveryCallback;
import itkach.aard2.callbacks.LookupListener;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.slob.BlobDescriptorList;
import itkach.aard2.slob.DescriptorStore;
import itkach.aard2.slob.SlobDescriptor;
import itkach.aard2.slob.SlobDescriptorList;
import itkach.aard2.ui.adapters.BlobListAdapter;
import itkach.aard2.utils.DictionaryFinder;
import itkach.slob.Slob;
import itkach.slob.Slob.Blob;
import itkach.slobber.Slobber;

public class Application extends JApplication {

    public static final String LOCALHOST = "127.0.0.1";
    public static final String CONTENT_URL_TEMPLATE = "http://" + LOCALHOST + ":%s%s";

    private Slobber slobber;

    public BlobDescriptorList bookmarks;
    public BlobDescriptorList history;
    public SlobDescriptorList dictionaries;

    private static int PREFERRED_PORT = 8013;
    private int port = -1;

    public BlobListAdapter lastResult;

    private DescriptorStore<BlobDescriptor> bookmarkStore;
    private DescriptorStore<BlobDescriptor> historyStore;
    private DescriptorStore<SlobDescriptor> dictStore;

    private ObjectMapper mapper;

    private String lookupQuery = "";

    private List<AppCompatActivity> articleActivities;

    public static String jsStyleSwitcher;
    public static String jsUserStyle;
    public static String jsClearUserStyle;
    public static String jsSetCannedStyle;

    private static final String PREF = "app";
    public static final String PREF_RANDOM_FAV_LOOKUP = "onlyFavDictsForRandomLookup";

    @SuppressLint ("MissingSuperCall")
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 19) {
            try {
                Method setWebContentsDebuggingEnabledMethod = WebView.class.getMethod(
                        "setWebContentsDebuggingEnabled", boolean.class);
                setWebContentsDebuggingEnabledMethod.invoke(null, true);
            } catch (NoSuchMethodException e1) {
                Log.d(getClass().getName(),
                        "setWebContentsDebuggingEnabledMethod method not found");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        articleActivities = Collections.synchronizedList(new ArrayList<AppCompatActivity>());

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        dictStore = new DescriptorStore<SlobDescriptor>(mapper, getDir("dictionaries", MODE_PRIVATE));
        bookmarkStore = new DescriptorStore<BlobDescriptor>(mapper, getDir(
                "bookmarks", MODE_PRIVATE));
        historyStore = new DescriptorStore<BlobDescriptor>(mapper, getDir(
                "history", MODE_PRIVATE));
        slobber = new Slobber();

        long t0 = System.currentTimeMillis();

        startWebServer();

        Log.d(getClass().getName(), String.format("Started web server on port %d in %d ms",
                port, (System.currentTimeMillis() - t0)));
        try {
            InputStream is;
            is = getClass().getClassLoader().getResourceAsStream("styleswitcher.js");
            jsStyleSwitcher = readTextFile(is, 0);
            is = getAssets().open("userstyle.js");
            jsUserStyle = readTextFile(is, 0);
            is = getAssets().open("clearuserstyle.js");
            jsClearUserStyle = readTextFile(is, 0);
            is = getAssets().open("setcannedstyle.js");
            jsSetCannedStyle = readTextFile(is, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String initialQuery = prefs().getString("query", "");

        lastResult = new BlobListAdapter(this);

        dictionaries = new SlobDescriptorList(this, dictStore);
        bookmarks = new BlobDescriptorList(this, bookmarkStore);
        history = new BlobDescriptorList(this, historyStore);

        dictionaries.registerDataSetObserver(new DataSetObserver() {
            @Override
            synchronized public void onChanged() {
                lastResult.setData(new ArrayList<Slob.Blob>().iterator());
                slobber.setSlobs(null);
                List<Slob> slobs = new ArrayList<Slob>();
                for (SlobDescriptor sd : dictionaries) {
                    Slob s = sd.load();
                    if (s != null) {
                        slobs.add(s);
                    }
                }
                slobber.setSlobs(slobs);
                lookup(lookupQuery);
                bookmarks.notifyDataSetChanged();
                history.notifyDataSetChanged();
            }
        });

        dictionaries.load();
        lookup(initialQuery, false);
        bookmarks.load();
        history.load();
    }

    public static String readTextFile(InputStream is, int maxSize) throws IOException, FileTooBigException {
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        StringWriter sw = new StringWriter();
        char[] buf = new char[16384];
        int count = 0;
        while (true) {
            int read = reader.read(buf);
            if (read == -1) {
                break;
            }
            count += read;
            if (maxSize > 0 && count > maxSize) {
                throw new FileTooBigException();
            }
            sw.write(buf, 0, read);
        }
        reader.close();
        return sw.toString();
    }

    private void startWebServer() {
        int portCandidate = PREFERRED_PORT;
        try {
            slobber.start("127.0.0.1", portCandidate);
            port = portCandidate;
        } catch (IOException e) {
            Log.w(getClass().getName(),
                    String.format("Failed to start on preferred port %d",
                            portCandidate), e);
            Set<Integer> seen = new HashSet<Integer>();
            seen.add(PREFERRED_PORT);
            Random rand = new Random();
            int attemptCount = 0;
            while (true) {
                int value = 1 + (int) Math.floor((65535 - 1025) * rand.nextDouble());
                portCandidate = 1024 + value;
                if (seen.contains(portCandidate)) {
                    continue;
                }
                attemptCount += 1;
                seen.add(portCandidate);
                Exception lastError;
                try {
                    slobber.start("127.0.0.1", portCandidate);
                    port = portCandidate;
                    break;
                } catch (IOException e1) {
                    lastError = e1;
                    Log.w(getClass().getName(),
                            String.format("Failed to start on port %d",
                                    portCandidate), e1);
                }
                if (attemptCount >= 20) {
                    throw new RuntimeException("Failed to start web server", lastError);
                }
            }
        }
    }

    public SharedPreferences prefs() {
        return this.getSharedPreferences(PREF, AppCompatActivity.MODE_PRIVATE);
    }

    public void push(AppCompatActivity activity) {
        this.articleActivities.add(activity);
        Log.d(getClass().getName(), "AppCompatActivity added, stack size " + this.articleActivities.size());
        if (this.articleActivities.size() > 3) {
            Log.d(getClass().getName(), "Max stack size exceeded, finishing oldest activity");
            this.articleActivities.get(0).finish();
        }
    }

    public void pop(AppCompatActivity activity) {
        this.articleActivities.remove(activity);
    }


    public Slob[] getActiveSlobs() {
        List<Slob> result = new ArrayList(dictionaries.size());
        for (SlobDescriptor sd : dictionaries) {
            if (sd.isActive) {
                Slob s = slobber.getSlob(sd.id);
                if (s != null) {
                    result.add(s);
                }
            }
        }
        return result.toArray(new Slob[result.size()]);
    }

    public Slob[] getFavoriteSlobs() {
        List<Slob> result = new ArrayList(dictionaries.size());
        for (SlobDescriptor sd : dictionaries) {
            if (sd.isActive && sd.priority > 0) {
                Slob s = slobber.getSlob(sd.id);
                if (s != null) {
                    result.add(s);
                }
            }
        }
        return result.toArray(new Slob[result.size()]);
    }

    public Iterator<Blob> find(String key) {
        return Slob.find(key, getActiveSlobs());
    }

    public Iterator<Blob> find(String key, String preferredSlobId) {
        //When following links we want to consider all dictionaries
        //including the ones user turned off
        return find(key, preferredSlobId, false);
    }

    public Slob.PeekableIterator<Blob> find(String key, String preferredSlobId, boolean activeOnly) {
        return this.find(key, preferredSlobId, activeOnly, null);
    }

    public Slob.PeekableIterator<Blob> find(
            String key, String preferredSlobId,
            boolean activeOnly, Slob.Strength upToStrength) {
        long t0 = System.currentTimeMillis();
        Slob[] slobs = activeOnly ? getActiveSlobs() : slobber.getSlobs();
        Slob.PeekableIterator<Blob> result = Slob.find(key, slobs, slobber.getSlob(preferredSlobId), upToStrength);
        Log.d(getClass().getName(), String.format("find ran in %dms", System.currentTimeMillis() - t0));
        return result;
    }

    public boolean isOnlyFavDictsForRandomLookup() {
        final SharedPreferences prefs = prefs();
        return prefs.getBoolean(Application.PREF_RANDOM_FAV_LOOKUP, false);
    }

    public void setOnlyFavDictsForRandomLookup(boolean value) {
        final SharedPreferences prefs = prefs();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Application.PREF_RANDOM_FAV_LOOKUP, value);
        editor.commit();
    }

    public Blob random() {
        Slob[] slobs = isOnlyFavDictsForRandomLookup() ? getFavoriteSlobs() : getActiveSlobs();
        return slobber.findRandom(slobs);
    }

    public String getUrl(Blob blob) {
        return String.format(CONTENT_URL_TEMPLATE,
                port, Slobber.mkContentURL(blob));
    }

    public Slob getSlob(String slobId) {
        return slobber.getSlob(slobId);
    }

    private Thread discoveryThread;
    private DictionaryFinder dictFinder = new DictionaryFinder();

    public synchronized void cancelFindDictionaries() {
        dictFinder.cancel();
    }

    public synchronized void findDictionaries(
            final DictionaryDiscoveryCallback callback) {
        if (discoveryThread != null) {
            return;
        }
        dictionaries.clear();
        discoveryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<SlobDescriptor> result = dictFinder.findDictionaries();
                discoveryThread = null;
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        dictionaries.addAll(result);
                        callback.onDiscoveryFinished();
                    }
                });
            }
        });
        discoveryThread.start ();
    }

    public synchronized boolean addDictionary(File file) {
        SlobDescriptor newDesc = SlobDescriptor.fromFile(file);
        if (newDesc.id != null) {
            for (SlobDescriptor d : dictionaries) {
                if (d.id != null && d.id.equals(newDesc.id)) {
                    return true;
                }
            }
        }
        dictionaries.add (newDesc);
        return false;
    }

    public Slob findSlob(String slobOrUri) {
        return slobber.findSlob(slobOrUri);
    }

    public String getSlobURI(String slobId) {
        return slobber.getSlobURI (slobId);
    }

    public int bookmarksSize() {
        return null != bookmarks ? bookmarks.size() : 0;
    }

    public int historySize() {
        return null != history ? history.size() : 0;
    }

    public int dictionariesSize() {
        return null != dictionaries ? dictionaries.size() : 0;
    }

    public int activeDictionariesSize() {
        int activeDictionaries = 0;
        if (null != dictionaries) {
            for (SlobDescriptor dict : dictionaries) {
                activeDictionaries += dict.isActive ? 1 : 0;
            }
        }
        return activeDictionaries;
    }

    public void addBookmark(String contentURL) {
        bookmarks.add(contentURL);
    }

    public void removeBookmark(String contentURL) {
        bookmarks.remove(contentURL);
    }

    public boolean isBookmarked(String contentURL) {
        return bookmarks.contains(contentURL);
    }

    private void setLookupResult(String query, Iterator<Slob.Blob> data) {
        this.lastResult.setData(data);
        lookupQuery = query;
        SharedPreferences.Editor edit = prefs().edit();
        edit.putString("query", query);
        edit.apply();
    }

    public String getLookupQuery() {
        return lookupQuery;
    }

    private AsyncTask<Void, Void, Iterator<Blob>> currentLookupTask;

    public void lookup(String query) {
        this.lookup(query, true);
    }

    private void lookup(final String query, boolean async) {
        if (currentLookupTask != null) {
            currentLookupTask.cancel(false);
            notifyLookupCanceled(query);
            currentLookupTask = null;
        }
        notifyLookupStarted(query);
        if (query == null || query.equals("")) {
            setLookupResult("", new ArrayList<Slob.Blob>().iterator());
            notifyLookupFinished(query);
            return;
        }

        if (async) {
            currentLookupTask = new AsyncTask<Void, Void, Iterator<Blob>>() {

                @Override
                protected Iterator<Blob> doInBackground(Void... params) {
                    return find(query);
                }

                @Override
                protected void onPostExecute(Iterator<Blob> result) {
                    if (!isCancelled()) {
                        setLookupResult(query, result);
                        notifyLookupFinished(query);
                        currentLookupTask = null;
                    }
                }

            };
            currentLookupTask.execute();
        } else {
            setLookupResult(query, find(query));
            notifyLookupFinished(query);
        }
    }

    private void notifyLookupStarted(String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupStarted(query);
        }
    }

    private void notifyLookupFinished(String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupFinished(query);
        }
    }

    private void notifyLookupCanceled(String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupCanceled(query);
        }
    }

    private List<LookupListener> lookupListeners = new ArrayList<LookupListener>();

    public void addLookupListener(LookupListener listener) {
        lookupListeners.add(listener);
    }

    public void removeLookupListener(LookupListener listener) {
        lookupListeners.remove(listener);
    }

    public static class FileTooBigException extends IOException {}

}
