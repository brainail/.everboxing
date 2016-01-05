package itkach.aard2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.brainail.EverboxingLexis.JApplication;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
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

    private Slobber mSlobber;

    public BlobDescriptorList bookmarks;
    public BlobDescriptorList history;
    public SlobDescriptorList dictionaries;

    private static int PREFERRED_PORT = 8013;
    private int port = -1;

    private String mLookupQuery = "";
    public BlobListAdapter mLastResult;

    private DescriptorStore<BlobDescriptor> bookmarkStore;
    private DescriptorStore<BlobDescriptor> historyStore;
    private DescriptorStore<SlobDescriptor> dictStore;
    private ObjectMapper mMapper;

    private List<AppCompatActivity> mArticleActivities;

    public static String JS_STYLE_SWITCHER;
    public static String JS_USER_STYLE;
    public static String JS_CLEAR_USER_STYLE;
    public static String JS_SET_CANNED_STYLE;

    private static final String AARD2_APP_PREF_NAME = "app";

    public static Application app () {
        return (Application) mApp;
    }

    @SuppressLint ("MissingSuperCall")
    @Override
    public void onCreate () {
        super.onCreate ();

        if (Build.VERSION.SDK_INT >= 19) {
            try {
                final Method setWebContentsDebuggingEnabledMethod
                        = WebView.class.getMethod ("setWebContentsDebuggingEnabled", boolean.class);
                setWebContentsDebuggingEnabledMethod.invoke (null, true);
            } catch (Exception e1) {
                Plogger.logE ("setWebContentsDebuggingEnabledMethod method not found");
            }
        }

        mArticleActivities = Collections.synchronizedList (new ArrayList<AppCompatActivity> ());

        mMapper = new ObjectMapper ();
        mMapper.configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        dictStore = new DescriptorStore<SlobDescriptor> (mMapper, getDir ("dictionaries", MODE_PRIVATE));
        bookmarkStore = new DescriptorStore<BlobDescriptor> (mMapper, getDir ("bookmarks", MODE_PRIVATE));
        historyStore = new DescriptorStore<BlobDescriptor> (mMapper, getDir ("history", MODE_PRIVATE));
        mSlobber = new Slobber ();

        startWebServer ();

        try {
            InputStream is;
            is = getClass ().getClassLoader ().getResourceAsStream ("styleswitcher.js");
            JS_STYLE_SWITCHER = readTextFile (is, 0);
            is = getAssets ().open ("userstyle.js");
            JS_USER_STYLE = readTextFile (is, 0);
            is = getAssets ().open ("clearuserstyle.js");
            JS_CLEAR_USER_STYLE = readTextFile (is, 0);
            is = getAssets ().open ("setcannedstyle.js");
            JS_SET_CANNED_STYLE = readTextFile (is, 0);
        } catch (final Exception exception) {
            // ...
        }

        String initialQuery = prefs ().getString ("query", "");

        mLastResult = new BlobListAdapter (this);

        dictionaries = new SlobDescriptorList (dictStore);
        bookmarks = new BlobDescriptorList (bookmarkStore);
        history = new BlobDescriptorList (historyStore);

        dictionaries.registerDataSetObserver (new DataSetObserver () {
            @Override
            synchronized public void onChanged () {
                mLastResult.setData (new ArrayList<Slob.Blob> ().iterator ());
                mSlobber.setSlobs (null);
                List<Slob> slobs = new ArrayList<Slob> ();
                for (SlobDescriptor sd : dictionaries) {
                    Slob s = sd.load ();
                    if (s != null) {
                        slobs.add (s);
                    }
                }
                mSlobber.setSlobs (slobs);
                lookup (mLookupQuery);
                bookmarks.notifyDataSetChanged ();
                history.notifyDataSetChanged ();
            }
        });

        dictionaries.load ();
        lookup (initialQuery, false);
        bookmarks.load ();
        history.load ();
    }

    public static String readTextFile (InputStream is, int maxSize) throws IOException, FileTooBigException {
        InputStreamReader reader = new InputStreamReader (is, "UTF-8");
        StringWriter sw = new StringWriter ();
        char[] buf = new char[16384];
        int count = 0;
        while (true) {
            int read = reader.read (buf);
            if (read == -1) {
                break;
            }
            count += read;
            if (maxSize > 0 && count > maxSize) {
                throw new FileTooBigException ();
            }
            sw.write (buf, 0, read);
        }
        reader.close ();
        return sw.toString ();
    }

    private void startWebServer () {
        int portCandidate = PREFERRED_PORT;
        try {
            mSlobber.start ("127.0.0.1", portCandidate);
            port = portCandidate;
        } catch (IOException e) {
            Plogger.logW (String.format ("Failed to start on preferred port %d", portCandidate));
            Set<Integer> seen = new HashSet<Integer> ();
            seen.add (PREFERRED_PORT);
            Random rand = new Random ();
            int attemptCount = 0;
            while (true) {
                int value = 1 + (int) Math.floor ((65535 - 1025) * rand.nextDouble ());
                portCandidate = 1024 + value;
                if (seen.contains (portCandidate)) {
                    continue;
                }
                attemptCount += 1;
                seen.add (portCandidate);
                Exception lastError;
                try {
                    mSlobber.start ("127.0.0.1", portCandidate);
                    port = portCandidate;
                    break;
                } catch (IOException e1) {
                    lastError = e1;
                    Plogger.logW (String.format ("Failed to start on port %d", portCandidate));
                }
                if (attemptCount >= 20) {
                    throw new RuntimeException ("Failed to start web server", lastError);
                }
            }
        }
    }

    public SharedPreferences prefs () {
        return this.getSharedPreferences (AARD2_APP_PREF_NAME, AppCompatActivity.MODE_PRIVATE);
    }

    public void push (AppCompatActivity activity) {
        this.mArticleActivities.add (activity);
        Plogger.logD ("AppCompatActivity added, stack size " + this.mArticleActivities.size ());
        if (this.mArticleActivities.size () > 3) {
            Plogger.logD ("Max stack size exceeded, finishing oldest activity");
            this.mArticleActivities.get (0).finish ();
        }
    }

    public void pop (AppCompatActivity activity) {
        this.mArticleActivities.remove (activity);
    }


    public Slob[] getActiveSlobs () {
        List<Slob> result = new ArrayList (dictionaries.size ());
        for (SlobDescriptor sd : dictionaries) {
            if (sd.isActive) {
                Slob s = mSlobber.getSlob (sd.id);
                if (s != null) {
                    result.add (s);
                }
            }
        }
        return result.toArray (new Slob[result.size ()]);
    }

    public Slob[] getFavoriteSlobs () {
        List<Slob> result = new ArrayList (dictionaries.size ());
        for (SlobDescriptor sd : dictionaries) {
            if (sd.isActive && sd.priority > 0) {
                Slob s = mSlobber.getSlob (sd.id);
                if (s != null) {
                    result.add (s);
                }
            }
        }
        return result.toArray (new Slob[result.size ()]);
    }

    public Iterator<Blob> find (String key) {
        return Slob.find (key, getActiveSlobs ());
    }

    public Iterator<Blob> find (String key, String preferredSlobId) {
        //When following links we want to consider all dictionaries
        //including the ones user turned off
        return find (key, preferredSlobId, false);
    }

    public Slob.PeekableIterator<Blob> find (String key, String preferredSlobId, boolean activeOnly) {
        return this.find (key, preferredSlobId, activeOnly, null);
    }

    public Slob.PeekableIterator<Blob> find (
            String key, String preferredSlobId,
            boolean activeOnly, Slob.Strength upToStrength) {
        long t0 = System.currentTimeMillis ();
        Slob[] slobs = activeOnly ? getActiveSlobs () : mSlobber.getSlobs ();
        Slob.PeekableIterator<Blob> result = Slob.find (key, slobs, mSlobber.getSlob (preferredSlobId), upToStrength);
        Plogger.logD (String.format ("find ran in %dms", System.currentTimeMillis () - t0));
        return result;
    }

    public Blob random () {
        final Slob[] slobs = SettingsManager.getInstance ().retrieveAppShouldUseFavouriteToRandomLookup ()
                ? getFavoriteSlobs ()
                : getActiveSlobs ();

        return mSlobber.findRandom (slobs);
    }

    public String getUrl (Blob blob) {
        return String.format (CONTENT_URL_TEMPLATE, port, Slobber.mkContentURL (blob));
    }

    public Slob getSlob (String slobId) {
        return mSlobber.getSlob (slobId);
    }

    private Thread discoveryThread;
    private DictionaryFinder dictFinder = new DictionaryFinder ();

    public synchronized void cancelFindDictionaries () {
        dictFinder.cancel ();
    }

    public synchronized void findDictionaries (
            final DictionaryDiscoveryCallback callback) {
        if (discoveryThread != null) {
            return;
        }
        dictionaries.clear ();
        discoveryThread = new Thread (new Runnable () {
            @Override
            public void run () {
                final List<SlobDescriptor> result = dictFinder.findDictionaries ();
                discoveryThread = null;
                Handler h = new Handler (Looper.getMainLooper ());
                h.post (new Runnable () {
                    @Override
                    public void run () {
                        dictionaries.addAll (result);
                        callback.onDiscoveryFinished ();
                    }
                });
            }
        });
        discoveryThread.start ();
    }

    public synchronized boolean addDictionary (File file) {
        SlobDescriptor newDesc = SlobDescriptor.fromFile (file);
        if (newDesc.id != null) {
            for (SlobDescriptor d : dictionaries) {
                if (d.id != null && d.id.equals (newDesc.id)) {
                    return true;
                }
            }
        }
        dictionaries.add (newDesc);
        return false;
    }

    public Slob findSlob (String slobOrUri) {
        return mSlobber.findSlob (slobOrUri);
    }

    public String getSlobURI (String slobId) {
        return mSlobber.getSlobURI (slobId);
    }

    public int bookmarksSize () {
        return null != bookmarks ? bookmarks.size () : 0;
    }

    public int historySize () {
        return null != history ? history.size () : 0;
    }

    public int dictionariesSize () {
        return null != dictionaries ? dictionaries.size () : 0;
    }

    public int activeDictionariesSize () {
        int activeDictionaries = 0;
        if (null != dictionaries) {
            for (SlobDescriptor dict : dictionaries) {
                activeDictionaries += dict.isActive ? 1 : 0;
            }
        }
        return activeDictionaries;
    }

    public void addBookmark (String contentURL) {
        bookmarks.add (contentURL);
    }

    public void removeBookmark (String contentURL) {
        bookmarks.remove (contentURL);
    }

    public boolean isBookmarked (String contentURL) {
        return bookmarks.contains (contentURL);
    }

    private void setLookupResult (String query, Iterator<Slob.Blob> data) {
        this.mLastResult.setData (data);
        mLookupQuery = query;
        SharedPreferences.Editor edit = prefs ().edit ();
        edit.putString ("query", query);
        edit.apply ();
    }

    public String getLookupQuery () {
        return mLookupQuery;
    }

    private AsyncTask<Void, Void, Iterator<Blob>> currentLookupTask;

    public void lookup (String query) {
        this.lookup (query, true);
    }

    private void lookup (final String query, boolean async) {
        if (currentLookupTask != null) {
            currentLookupTask.cancel (false);
            notifyLookupCanceled (query);
            currentLookupTask = null;
        }
        notifyLookupStarted (query);
        if (query == null || query.equals ("")) {
            setLookupResult ("", new ArrayList<Slob.Blob> ().iterator ());
            notifyLookupFinished (query);
            return;
        }

        if (async) {
            currentLookupTask = new AsyncTask<Void, Void, Iterator<Blob>> () {

                @Override
                protected Iterator<Blob> doInBackground (Void... params) {
                    return find (query);
                }

                @Override
                protected void onPostExecute (Iterator<Blob> result) {
                    if (!isCancelled ()) {
                        setLookupResult (query, result);
                        notifyLookupFinished (query);
                        currentLookupTask = null;
                    }
                }

            };
            currentLookupTask.execute ();
        } else {
            setLookupResult (query, find (query));
            notifyLookupFinished (query);
        }
    }

    private void notifyLookupStarted (String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupStarted (query);
        }
    }

    private void notifyLookupFinished (String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupFinished (query);
        }
    }

    private void notifyLookupCanceled (String query) {
        for (LookupListener l : lookupListeners) {
            l.onLookupCanceled (query);
        }
    }

    private List<LookupListener> lookupListeners = new ArrayList<LookupListener> ();

    public void addLookupListener (LookupListener listener) {
        lookupListeners.add (listener);
    }

    public void removeLookupListener (LookupListener listener) {
        lookupListeners.remove (listener);
    }

    public static class FileTooBigException extends IOException {}

}
