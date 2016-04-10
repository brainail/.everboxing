package itkach.aard2.ui.views;

import android.annotation.SuppressLint;
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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.Sdk;
import org.brainail.EverboxingLexis.utils.js.ContentStyleJsInterface;
import org.brainail.EverboxingLexis.utils.js.ContentStyleJsInterface.IStyleHelper;
import org.brainail.EverboxingLexis.utils.js.ProcessContentJsInterface;
import org.brainail.EverboxingLexis.utils.js.ProcessContentJsInterface.ISelectionHelper;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

import static android.net.ConnectivityManager.TYPE_ETHERNET;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class ArticleWebView
        extends BaseArticleWebView
        implements ISelectionHelper, IStyleHelper {

    // Styles
    private final String mDefaultStyleTitle;
    private final String mAutoStyleTitle;

    // Preferences
    public static final String PREF = "articlePreferences";
    private static final String PREF_TEXT_ZOOM = "textZoom";
    private static final String PREF_STYLE = "style.";
    private static final String PREF_STYLE_AVAILABLE = "style.available.";

    public Set<String> mExternalSchemes = new HashSet<String> () {{
        add ("https");
        add ("ftp");
        add ("sftp");
        add ("mailto");
        add ("geo");
    }};

    private SortedSet<String> mStyleTitles = new TreeSet<String> ();

    private String mCurrentSlobId;
    private String mCurrentSlobUri;

    private ConnectivityManager mConnectivityManager;

    private Timer mTimer;
    private TimerTask mApplyStylePrefTask;

    private boolean mForceLoadRemoteContent;

    public static interface OnScrollDirectionListener {
        public void onScrollUp ();
        public void onScrollDown ();
    }

    public static final int SCROLL_RECOGNIZE_DIRECTION_DISTANCE = 30;

    public OnScrollDirectionListener mOnScrollDirectionListener;

    public ISelectionHelper mSelectionHelper;

    public void setSelectionHelper (final ISelectionHelper selectionHelper) {
        mSelectionHelper = selectionHelper;
    }

    public void enableForceLoadRemoteContent (final boolean enabled) {
        mForceLoadRemoteContent = enabled;
    }

    public boolean allowRemoteContent () {
        if (mForceLoadRemoteContent) {
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
            final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo ();
            if (networkInfo != null) {
                int networkType = networkInfo.getType ();
                if (networkType == TYPE_WIFI || networkType == TYPE_ETHERNET) return true;
            }
        }

        return false;
    }

    public ArticleWebView (Context context) {
        this (context, null);
    }

    @SuppressLint ("AddJavascriptInterface")
    @Override
    protected void addJavascriptInterfaces (Context context) {
        super.addJavascriptInterfaces (context);

        addJavascriptInterface (new ContentStyleJsInterface (this), ContentStyleJsInterface.JS_INTERFACE_NAME);
        addJavascriptInterface (new ProcessContentJsInterface (this), ProcessContentJsInterface.JS_INTERFACE_NAME);
    }

    @SuppressLint ({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public ArticleWebView (Context context, AttributeSet attrs) {
        super (context, attrs);

        mConnectivityManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);

        initWebSetting (context);

        final Resources resources = getResources ();
        mDefaultStyleTitle = resources.getString (R.string.default_style_title);
        mAutoStyleTitle = resources.getString (R.string.auto_style_title);

        mTimer = new Timer ();
        final Runnable applyStyleRunnable = new Runnable () {
            @Override
            public void run () {
                applyStylePref ();
            }
        };

        mApplyStylePrefTask = new TimerTask () {
            @Override
            public void run () {
                android.os.Handler handler = getHandler ();
                if (handler != null) {
                    handler.post (applyStyleRunnable);
                }
            }
        };

        setWebViewClient (mWebViewClient);
        // setWebChromeClient (new LoggingWebChromeClient ());

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
        final SharedPreferences prefs = getContext ().getSharedPreferences ("userStyles", Activity.MODE_PRIVATE);
        Map<String, ?> data = prefs.getAll ();
        List<String> names = new ArrayList<String> (data.keySet ());
        Util.sort (names);
        names.addAll (mStyleTitles);
        names.add (mDefaultStyleTitle);
        names.add (mAutoStyleTitle);
        return names.toArray (new String[names.size ()]);
    }

    private String getAutoStyle () {
        Plogger.logD ("Auto style will return " + mDefaultStyleTitle);
        return mDefaultStyleTitle;
    }

    private void setStyle (String styleTitle) {
        String js;
        final SharedPreferences prefs = getContext ().getSharedPreferences ("userStyles", Activity.MODE_PRIVATE);

        // Global light, we just use default in this case
        if (styleTitle.toLowerCase (Locale.US).contains ("global")
                && styleTitle.toLowerCase (Locale.US).contains ("light")) {
            styleTitle = mDefaultStyleTitle;
        }

        if (prefs.contains (styleTitle)) {
            String css = prefs.getString (styleTitle, "");
            String elementId = getCurrentSlobId ();
            js = String.format ("javascript:" + Application.JS_USER_STYLE, elementId, css);
        } else {
            js = String.format (
                    "javascript:" + Application.JS_CLEAR_USER_STYLE + Application.JS_SET_CANNED_STYLE,
                    getCurrentSlobId (), styleTitle
            );
        }

        // Plogger.logD (js);
        loadUrl (js);
    }

    public SharedPreferences prefs () {
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
        return mCurrentSlobId;
    }

    private void saveAvailableStylesPref (Set<String> styleTitles) {
        SharedPreferences prefs = prefs ();
        SharedPreferences.Editor editor = prefs.edit ();
        editor.putStringSet (PREF_STYLE_AVAILABLE + mCurrentSlobUri, styleTitles);
        boolean success = editor.commit ();
        if (!success) {
            Plogger.logW ("Failed to save article view available styles pref");
        }
    }

    @SuppressWarnings ("unchecked")
    private void loadAvailableStylesPref () {
        if (mCurrentSlobUri == null) {
            Plogger.logW ("Can't load article view available styles pref - slob uri is null");
            return;
        }
        SharedPreferences prefs = prefs ();
        Plogger.logD ("Available styles before pref load: " + mStyleTitles);
        mStyleTitles = new TreeSet<> (prefs.getStringSet (PREF_STYLE_AVAILABLE + mCurrentSlobUri, Collections.EMPTY_SET));
        Plogger.logD ("Loaded available styles: " + mStyleTitles);
    }

    public void saveStylePref (String styleTitle) {
        if (mCurrentSlobUri == null) {
            Plogger.logW ("Can't save article view style pref - slob uri is null");
            return;
        }
        SharedPreferences prefs = prefs ();
        String prefName = PREF_STYLE + mCurrentSlobUri;
        SharedPreferences.Editor editor = prefs.edit ();
        editor.putString (prefName, styleTitle);
        boolean success = editor.commit ();
        if (!success) {
            Plogger.logW ("Failed to save article view style pref");
        }
    }

    private String getStylePreferenceValue () {
        return prefs ().getString (PREF_STYLE + mCurrentSlobUri, mAutoStyleTitle);
    }

    private boolean isAutoStyle (String title) {
        return title.equals (mAutoStyleTitle);
    }

    @Override
    public String getPreferredStyle () {
        if (mCurrentSlobUri == null) {
            return "";
        }

        String styleTitle = getStylePreferenceValue ();
        if (prefs ().contains ("globalArticleTheme")) {
            styleTitle = prefs ().getString ("globalArticleTheme", styleTitle);
        }

        String result = isAutoStyle (styleTitle) ? getAutoStyle () : styleTitle;
        Plogger.logD ("getPreferredStyle() will return " + result);

        return result;
    }

    @Override
    public String exportStyleSwitcherAs () {
        return "$styleSwitcher";
    }

    @Override
    public void onStyleSet (String title) {
        Plogger.logD ("Style set! " + title);
        mApplyStylePrefTask.cancel ();
    }

    @Override
    public void setStyleTitles (String[] titles) {
        Plogger.logD (String.format ("Got %d style titles", titles.length));
        if (titles.length == 0) return;

        final SortedSet<String> newStyleTitlesSet = new TreeSet<> (Arrays.asList (titles));
        if (!mStyleTitles.equals (newStyleTitlesSet)) {
            mStyleTitles = newStyleTitlesSet;
            saveAvailableStylesPref (mStyleTitles);
        }
    }

    public void applyStylePref () {
        String styleTitle = getPreferredStyle ();
        setStyle (styleTitle);
    }

    public boolean textZoomIn () {
        WebSettings settings = getSettings ();
        int newZoom = settings.getTextZoom () + 10;
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
        int newZoom = settings.getTextZoom () - 10;
        if (newZoom >= 5) {
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
                mCurrentSlobId = bd.slobId;
                mCurrentSlobUri = Application.app ().getSlobURI (mCurrentSlobId);
                loadAvailableStylesPref ();
            } else {
                mCurrentSlobId = null;
                mCurrentSlobUri = null;
            }
            Plogger.logD (String.format ("currentSlobId set from url %s to %s, uri %s", url, mCurrentSlobId, mCurrentSlobUri));
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (event.getAction () == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (canGoBack ()) {
                        goBack ();
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
        mTimer.cancel ();
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

    private final WebViewClient mWebViewClient = new WebViewClient () {

        private final byte[] NO_BYTES = new byte[0];

        private final Map<String, List<Long>> mTimes = new HashMap<String, List<Long>> ();

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            Plogger.logD ("onPageStarted: " + url);
            if (url.startsWith ("about:")) {
                return;
            }

            if (mTimes.containsKey (url)) {
                Plogger.logD ("onPageStarted: already ready seen " + url);
                mTimes.get (url).add (System.currentTimeMillis ());
            } else {
                List<Long> tsList = new ArrayList<Long> ();
                tsList.add (System.currentTimeMillis ());
                mTimes.put (url, tsList);
                view.loadUrl ("javascript:" + Application.JS_STYLE_SWITCHER);
                try {
                    mTimer.schedule (mApplyStylePrefTask, 250, 200);
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

            if (mTimes.containsKey (url)) {
                List<Long> tsList = mTimes.get (url);
                long ts = tsList.remove (tsList.size () - 1);
                Plogger.logD ("onPageFinished: finished: " + url + " in " + (System.currentTimeMillis () - ts));
                if (tsList.isEmpty ()) {
                    Plogger.logD ("onPageFinished: really done with " + url);
                    mTimes.remove (url);
                    mApplyStylePrefTask.cancel ();
                }
            } else {
                Plogger.logW ("onPageFinished: Unexpected page finished event for " + url);
            }

            view.loadUrl ("javascript:"
                    + Application.JS_STYLE_SWITCHER + ";"
                    + ContentStyleJsInterface.JS_INTERFACE_NAME
                    + ".setStyleTitles($styleSwitcher.getTitles())"
            );

            applyStylePref ();
            processAllTextSelection ();
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
            if (allowRemoteContent ()) {
                return null;
            }
            return new WebResourceResponse ("text/plain", "UTF-8", new ByteArrayInputStream (NO_BYTES));
        }

        @Override
        public boolean shouldOverrideUrlLoading (WebView view, final String url) {
            Plogger.logD (String.format ("shouldOverrideUrlLoading: %s (current %s)", url, view.getUrl ()));

            Uri uri = Uri.parse (url);
            String scheme = uri.getScheme ();
            String host = uri.getHost ();

            if (mExternalSchemes.contains (scheme) ||
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
    };

    public void processAllTextSelection () {
        if (Sdk.isSdkSupported (Sdk.KITKAT)) {
            evaluateJavascript (ProcessContentJsInterface.JS_ALL_TEXT_SELECTION, null);
        } else {
            loadUrl (ProcessContentJsInterface.JS_ALL_TEXT_SELECTION);
        }
    }

    public void processSelection () {
        if (Sdk.isSdkSupported (Sdk.KITKAT)) {
            evaluateJavascript (ProcessContentJsInterface.JS_PARTIAL_TEXT_SELECTION, null);
        } else {
            // loadUrl (ProcessContentJsInterface.JS_PARTIAL_TEXT_SELECTION);
            // Unfortunately we don't want to do this here via loadUrl due to
            // the fact that WebViewClassic will close action mode in this case ... ;(
        }
    }

    @Override
    public void onAllTextSelection (String selection) {
        if (null != mSelectionHelper) {
            mSelectionHelper.onAllTextSelection (selection);
        }
    }

    @Override
    public void onPartialTextSelection (String selection) {
        if (null != mSelectionHelper) {
            mSelectionHelper.onPartialTextSelection (selection);
        }
    }

}
