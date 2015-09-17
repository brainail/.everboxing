package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;

import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.views.ArticleWebView;

public class ArticleFragment extends Fragment {

    public static final String ARG_URL = "articleUrl";

    private ArticleWebView view;
    private MenuItem miBookmark;
    private Drawable icBookmark;
    private Drawable icBookmarkOutline;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        icBookmark = BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_bookmark);
        icBookmarkOutline = BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_bookmark_outline);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Looks like this may be called multiple times with the same menu
        //for some reason when activity is restored, so need to clear
        //to avoid duplicates
        menu.clear();
        inflater.inflate(R.menu.menu_articles, menu);
        miBookmark = menu.findItem(R.id.action_bookmark_article);
    }

    private void displayBookmarked(boolean value) {
        if (miBookmark == null) {
            return;
        }
        if (value) {
            miBookmark.setChecked(true);
            miBookmark.setIcon(icBookmark);
        } else {
            miBookmark.setChecked(false);
            miBookmark.setIcon(icBookmarkOutline);
        }
    }

    @SuppressWarnings ("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_find_in_page) {
            view.showFindDialog(null, false);
            return true;
        }
        if (itemId == R.id.action_bookmark_article) {
            Application app = (Application) getActivity().getApplication();
            if (this.url != null) {
                if (item.isChecked()) {
                    ((ArticleCollectionActivity) getActivity ()).unbookmarkCurrentTab ();
                    app.removeBookmark (this.url);
                    displayBookmarked (false);
                } else {
                    app.addBookmark(this.url);
                    displayBookmarked(true);
                }
            }
            return true;
        }
        if (itemId == R.id.action_zoom_in) {
            view.textZoomIn();
            return true;
        }
        if (itemId == R.id.action_zoom_out) {
            view.textZoomOut();
            return true;
        }
        if (itemId == R.id.action_zoom_reset) {
            view.resetTextZoom();
            return true;
        }
        if (itemId == R.id.action_load_remote_content) {
            view.forceLoadRemoteContent = true;
            view.reload();
            return true;
        }
        if (itemId == R.id.action_select_style) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String[] styleTitles = view.getAvailableStyles();
            builder.setTitle(R.string.select_style)
                    .setItems(styleTitles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String title = styleTitles[which];
                            view.saveStylePref(title);
                            view.applyStylePref();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        this.url = args == null ? null : args.getString(ARG_URL);
        if (url == null) {
            View layout = inflater.inflate(R.layout.empty_view, container, false);
            TextView textView = (TextView) layout.findViewById(R.id.empty_text);
            textView.setText("");
            ImageView icon = (ImageView) layout.findViewById(R.id.empty_icon);
            icon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_help));
            return layout;
        }

        View layout = inflater.inflate(R.layout.article_view, container, false);
        final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.webViewPogress);
        view = (ArticleWebView) layout.findViewById(R.id.webView);
        view.restoreState(savedInstanceState);
        view.loadUrl(url);
        view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, final int newProgress) {
                final Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(newProgress);
                            if (newProgress >= progressBar.getMax()) {
                                progressBar.setVisibility(ViewGroup.GONE);
                            }
                        }
                    });
                }
            }
        });
        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        applyTextZoomPref();
        applyStylePref();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.url == null) {
            miBookmark.setVisible(false);
        } else {
            Application app = (Application) getActivity().getApplication();
            try {
                boolean bookmarked = app.isBookmarked(this.url);
                Log.d(getTag(), String.format("Is %s bookmarked? %s", this.url, bookmarked));
                displayBookmarked(bookmarked);
            } catch (Exception ex) {
                miBookmark.setVisible(false);
                Log.d(getTag(), "Not bookmarkable: " + this.url, ex);
            }
        }
        applyTextZoomPref();
        applyStylePref();
    }

    public void applyTextZoomPref() {
        if (view != null) {
            view.applyTextZoomPref();
        }
    }

    public void applyStylePref() {
        if (view != null) {
            view.applyStylePref();
        }
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            view.destroy();
            view = null;
        }
        super.onDestroy();
    }
}