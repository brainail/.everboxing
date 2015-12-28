package itkach.aard2.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import itkach.aard2.Application;
import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.utils.RemoteContentMode;
import itkach.aard2.utils.Util;

public class ArticleWebView extends WebView {

    private final String styleSwitcherJs;
    private final String defaultStyleTitle;
    private final String autoStyleTitle;

    public static final String PREF = "articlePreferences";
    private static final String PREF_TEXT_ZOOM = "textZoom";
    private static final String PREF_STYLE = "style.";
    private static final String PREF_STYLE_AVAILABLE = "style.available.";

    public Set<String> externalSchemes = new HashSet<String> () {{
        add ("https"); add ("ftp"); add ("sftp"); add ("mailto"); add ("geo");
    }};

    private SortedSet<String> styleTitles = new TreeSet<String> ();

    private String currentSlobId;
    private String currentSlobUri;
    private ConnectivityManager connectivityManager;

    private Timer timer;
    private TimerTask applyStylePref;

    private boolean forceLoadRemoteContent;

    public static interface OnScrollDirectionListener {
        public void onScrollUp ();
        public void onScrollDown ();
    }

    public static final int SCROLL_RECOGNIZE_DIRECTION_DISTANCE = 30;

    public OnScrollDirectionListener mOnScrollDirectionListener;

    public void enableForceLoadRemoteContent (final boolean enabled) {
        forceLoadRemoteContent = enabled;
    }

    @JavascriptInterface
    public void setStyleTitles (String[] titles) {
        Plogger.logD (String.format ("Got %d style titles", titles.length));
        if (titles.length == 0) {
            return;
        }
        SortedSet newStyleTitlesSet = new TreeSet<String> (Arrays.asList (titles));
        if (!this.styleTitles.equals (newStyleTitlesSet)) {
            this.styleTitles = newStyleTitlesSet;
            saveAvailableStylesPref (this.styleTitles);
        }

        if (Plogger.LOGGABLE) {
            for (String title : titles) {
                Plogger.logD (title);
            }
        }
    }

    public boolean allowRemoteContent() {
        if (forceLoadRemoteContent) {
            return true;
        }

        final RemoteContentMode mode = SettingsManager.getInstance ().retrieveLoadRemoteContentMode ();
        if (RemoteContentMode.ALWAYS == mode) {
            return true;
        }

        if (RemoteContentMode.NEVER == mode) {
            return false;
        }

        if (RemoteContentMode.WIFI == mode) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int networkType = networkInfo.getType();
                if (networkType == ConnectivityManager.TYPE_WIFI
                        || networkType == ConnectivityManager.TYPE_ETHERNET) {
                    return true;
                }
            }
        }

        return false;
    }

    public ArticleWebView (Context context) {
        this (context, null);
    }

    public ArticleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        styleSwitcherJs = Application.jsStyleSwitcher;

        WebSettings settings = this.getSettings ();
        settings.setJavaScriptEnabled (true);
        settings.setBuiltInZoomControls (true);
        settings.setDisplayZoomControls (false);

        Resources r = getResources ();
        defaultStyleTitle = r.getString (R.string.default_style_title);
        autoStyleTitle = r.getString (R.string.auto_style_title);

        this.addJavascriptInterface (this, "$SLOB");

        timer = new Timer ();

        final Runnable applyStyleRunnable = new Runnable () {
            @Override
            public void run () {
                applyStylePref ();
            }
        };

        applyStylePref = new TimerTask () {
            @Override
            public void run () {
                android.os.Handler handler = getHandler ();
                if (handler != null) {
                    handler.post (applyStyleRunnable);
                }
            }
        };

        this.setWebViewClient (new WebViewClient () {

            byte[] noBytes = new byte[0];

            Map<String, List<Long>> times = new HashMap<String, List<Long>> ();

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                Plogger.logD ("onPageStarted: " + url);
                if (url.startsWith ("about:")) {
                    return;
                }
                if (times.containsKey (url)) {
                    Plogger.logD ("onPageStarted: already ready seen " + url);
                    times.get (url).add (System.currentTimeMillis ());
                    return;
                } else {
                    List<Long> tsList = new ArrayList<Long> ();
                    tsList.add (System.currentTimeMillis ());
                    times.put (url, tsList);
                    view.loadUrl ("javascript:" + styleSwitcherJs);
                    try {
                        timer.schedule (applyStylePref, 250, 200);
                    } catch (IllegalStateException ex) {
                        Plogger.logW (ex, "Failed to schedule applyStylePref in view " + view.getId ());
                    }
                }
            }

            @Override
            public void onPageFinished (WebView view, String url) {
                Plogger.logD ("onPageFinished: " + url);
                if (url.startsWith ("about:")) {
                    return;
                }
                if (times.containsKey (url)) {
                    List<Long> tsList = times.get (url);
                    long ts = tsList.remove (tsList.size () - 1);
                    Plogger.logD ("onPageFinished: finished: " + url + " in " + (System.currentTimeMillis () - ts));
                    if (tsList.isEmpty ()) {
                        Plogger.logD ("onPageFinished: really done with " + url);
                        times.remove (url);
                        applyStylePref.cancel ();
                    }
                } else {
                    Plogger.logW ("onPageFinished: Unexpected page finished event for " + url);
                }
                view.loadUrl ("javascript:" + styleSwitcherJs + ";$SLOB.setStyleTitles($styleSwitcher.getTitles())");
                applyStylePref ();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
                Uri parsed;
                try {
                    parsed = Uri.parse (url);
                } catch (Exception e) {
                    Plogger.logW (e, "Failed to parse url: " + url);
                    return super.shouldInterceptRequest (view, url);
                }
                if (parsed.isRelative ()) {
                    return null;
                }
                String host = parsed.getHost ();
                if (host == null || host.toLowerCase ().equals (Application.LOCALHOST)) {
                    return null;
                }
                if (allowRemoteContent()) {
                    return null;
                }
                return new WebResourceResponse("text/plain", "UTF-8",
                        new ByteArrayInputStream(noBytes));
            }

            @Override
            public boolean shouldOverrideUrlLoading (WebView view, final String url) {
                Plogger.logD (String.format ("shouldOverrideUrlLoading: %s (current %s)", url, view.getUrl ()));

                Uri uri = Uri.parse (url);
                String scheme = uri.getScheme ();
                String host = uri.getHost ();

                if (externalSchemes.contains (scheme) ||
                        (scheme.equals ("http") && !host.equals (Application.LOCALHOST))) {

                    openUrl (url);
                    return true;
                }

                if (scheme.equals ("http")
                        && host.equals (Application.LOCALHOST)
                        && uri.getQueryParameter ("blob") == null) {

                    final Intent intent = new Intent (getContext (), ArticleCollectionActivity.class);
                    intent.setData (uri);
                    getContext ().startActivity (intent);

                    Plogger.logD ("Overriding loading of " + url);
                    return true;
                }

                Plogger.logD ("NOT overriding loading of " + url);
                return false;
            }
        });

        applyTextZoomPref ();
    }

    private void openUrl (final String url) {
        final Context context = hostContext ();
        if (context instanceof BaseActivity) {
            final BaseActivity urlOpener = (BaseActivity) context;
            urlOpener.openUrl (url);
        } else {
            final Uri uri = Uri.parse (url);
            final Intent browserIntent = new Intent (Intent.ACTION_VIEW, uri);
            getContext ().startActivity (browserIntent);
        }
    }

    private Context hostContext () {
        Context context = getContext ();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                context = ((ContextWrapper) context).getBaseContext ();
            }
        }

        return context;
    }

    public String[] getAvailableStyles () {
        final SharedPreferences prefs = getContext ().getSharedPreferences (
                "userStyles", AppCompatActivity.MODE_PRIVATE);
        Map<String, ?> data = prefs.getAll ();
        List<String> names = new ArrayList<String> (data.keySet ());
        Util.sort (names);
        names.addAll (styleTitles);
        names.add (defaultStyleTitle);
        names.add (autoStyleTitle);
        return names.toArray (new String[names.size ()]);
    }

    private String getAutoStyle () {
        Plogger.logD ("Auto style will return " + defaultStyleTitle);
        return defaultStyleTitle;
    }

    private void setStyle (String styleTitle) {
        String js;
        final SharedPreferences prefs = getContext ().getSharedPreferences (
                "userStyles", AppCompatActivity.MODE_PRIVATE);
        if (prefs.contains (styleTitle)) {
            String css = prefs.getString (styleTitle, "");
            String elementId = getCurrentSlobId ();
            js = String.format (
                    "javascript:" + Application.jsUserStyle, elementId, css);
        } else {
            js = String.format (
                    "javascript:" + Application.jsClearUserStyle + Application.jsSetCannedStyle,
                    getCurrentSlobId (), styleTitle);
        }
        Plogger.logD (js);
        this.loadUrl (js);
    }

    private SharedPreferences prefs () {
        return getContext ().getSharedPreferences (PREF, AppCompatActivity.MODE_PRIVATE);
    }

    public void applyTextZoomPref () {
        SharedPreferences prefs = prefs ();
        int textZoom = prefs.getInt (PREF_TEXT_ZOOM, 100);
        WebSettings settings = getSettings ();
        settings.setTextZoom (textZoom);
    }

    private void saveTextZoomPref () {
        SharedPreferences prefs = prefs ();
        int textZoom = getSettings ().getTextZoom ();
        SharedPreferences.Editor e = prefs.edit ();
        e.putInt (PREF_TEXT_ZOOM, textZoom);
        boolean success = e.commit ();
        if (!success) {
            Plogger.logW ("Failed to save article view text zoom pref");
        }
    }

    private String getCurrentSlobId () {
        return currentSlobId;
    }

    private void saveAvailableStylesPref (Set<String> styleTitles) {
        SharedPreferences prefs = prefs ();
        SharedPreferences.Editor editor = prefs.edit ();
        editor.putStringSet (PREF_STYLE_AVAILABLE + currentSlobUri, styleTitles);
        boolean success = editor.commit ();
        if (!success) {
            Plogger.logW ("Failed to save article view available styles pref");
        }
    }

    private void loadAvailableStylesPref () {
        if (currentSlobUri == null) {
            Plogger.logW ("Can't load article view available styles pref - slob uri is null");
            return;
        }
        SharedPreferences prefs = prefs ();
        Plogger.logD ("Available styles before pref load: " + styleTitles.size ());
        styleTitles = new TreeSet (prefs.getStringSet (PREF_STYLE_AVAILABLE + currentSlobUri, Collections.EMPTY_SET));
        Plogger.logD ("Loaded available styles: " + styleTitles.size ());
    }

    public void saveStylePref (String styleTitle) {
        if (currentSlobUri == null) {
            Plogger.logW ("Can't save article view style pref - slob uri is null");
            return;
        }
        SharedPreferences prefs = prefs ();
        String prefName = PREF_STYLE + currentSlobUri;
        SharedPreferences.Editor editor = prefs.edit ();
        editor.putString (prefName, styleTitle);
        boolean success = editor.commit ();
        if (!success) {
            Plogger.logW ("Failed to save article view style pref");
        }
    }

    private String getStylePreferenceValue () {
        return prefs ().getString (PREF_STYLE + currentSlobUri, autoStyleTitle);
    }

    private boolean isAutoStyle (String title) {
        return title.equals (autoStyleTitle);
    }

    @JavascriptInterface
    public String getPreferredStyle () {
        if (currentSlobUri == null) {
            return "";
        }
        String styleTitle = getStylePreferenceValue ();
        String result = isAutoStyle (styleTitle) ? getAutoStyle () : styleTitle;
        Plogger.logD ("getPreferredStyle() will return " + result);
        return result;
    }

    @JavascriptInterface
    public String exportStyleSwitcherAs () {
        return "$styleSwitcher";
    }

    @JavascriptInterface
    public void onStyleSet (String title) {
        Plogger.logD ("Style set! " + title);
        applyStylePref.cancel ();
    }

    public void applyStylePref () {
        String styleTitle = getPreferredStyle ();
        this.setStyle (styleTitle);
    }

    public boolean textZoomIn () {
        WebSettings settings = getSettings ();
        int newZoom = settings.getTextZoom () + 20;
        if (newZoom <= 200) {
            settings.setTextZoom (newZoom);
            saveTextZoomPref ();
            return true;
        } else {
            return false;
        }
    }

    public boolean textZoomOut () {
        WebSettings settings = getSettings ();
        int newZoom = settings.getTextZoom () - 20;
        if (newZoom >= 40) {
            settings.setTextZoom (newZoom);
            saveTextZoomPref ();
            return true;
        } else {
            return false;
        }
    }

    public void resetTextZoom () {
        getSettings ().setTextZoom (100);
        saveTextZoomPref ();
    }


    @Override
    public void loadUrl (String url, Map<String, String> additionalHttpHeaders) {
        beforeLoadUrl (url);
        super.loadUrl (url, additionalHttpHeaders);
    }

    @Override
    public void loadUrl (String url) {
        beforeLoadUrl (url);
        super.loadUrl (url);
    }

    private void beforeLoadUrl (String url) {
        setCurrentSlobIdFromUrl (url);
        if (!url.startsWith ("javascript:")) {
            updateBackgrounColor ();
        }
    }

    private void updateBackgrounColor () {
        int color = Color.WHITE;
        String preferredStyle = getPreferredStyle ().toLowerCase ();
        // webview's default background may "show through" before page
        // load started and/or before page's style applies (and even after that if
        // style doesn't explicitly set background).
        // this is a hack to preemptively set "right" background and prevent
        // extra flash
        //
        // TODO Hack it even more - allow style title to include background color spec
        // so that this can work with "strategically" named user css
        if (preferredStyle.contains ("night") || preferredStyle.contains ("dark")) {
            color = Color.BLACK;
        }
        setBackgroundColor (color);
    }

    private void setCurrentSlobIdFromUrl (String url) {
        if (!url.startsWith ("javascript:")) {
            Uri uri = Uri.parse (url);
            BlobDescriptor bd = BlobDescriptor.fromUri (uri);
            if (bd != null) {
                currentSlobId = bd.slobId;
                currentSlobUri = Application.app ().getSlobURI (currentSlobId);
                loadAvailableStylesPref ();
            } else {
                currentSlobId = null;
                currentSlobUri = null;
            }
            Plogger.logD (String.format ("currentSlobId set from url %s to %s, uri %s", url, currentSlobId, currentSlobUri));
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (event.getAction () == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (this.canGoBack ()) {
                        this.goBack ();
                        return true;
                    } else {
                        return false;
                    }
            }
        }
        return super.onKeyDown (keyCode, event);
    }

    @Override
    public void destroy () {
        super.destroy ();
        timer.cancel ();
    }

    public void setOnScrollDirectionListener (final OnScrollDirectionListener listener) {
        mOnScrollDirectionListener = listener;
    }

    @Override
    protected void onScrollChanged (int l, int t, int oldl, int oldt) {
        super.onScrollChanged (l, t, oldl, oldt);

        if (null != mOnScrollDirectionListener) {
            if (t - oldt > SCROLL_RECOGNIZE_DIRECTION_DISTANCE) {
                mOnScrollDirectionListener.onScrollDown ();
            } else if (t - oldt < -SCROLL_RECOGNIZE_DIRECTION_DISTANCE) {
                mOnScrollDirectionListener.onScrollUp ();
            }
        }
    }

}
