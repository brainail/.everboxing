package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import co.mobiwise.library.ProgressLayout;
import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.views.ArticleWebView;

public class ArticleFragment extends Fragment {

    public static class Args {
        public static final String ARTICLE_URL = "articleUrl";
        public static final String ARTICLE_TITLE = "article_title";
    }

    private ArticleWebView mArticleWebView;

    private String mArticleUrl;
    private String mArticleTitle;

    private MenuItem mMenuItemBookmark;

    private CustomTabsSceneHelper mCustomTabsSceneHelper;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setHasOptionsMenu (true);

        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.onCreateScene (getActivity ());
    }

    @Override
    public void onStart () {
        super.onStart ();

        mCustomTabsSceneHelper.onStartScene (getActivity ());
    }

    @Override
    public void onStop () {
        super.onStop ();

        mCustomTabsSceneHelper.onStopScene (getActivity ());
    }

    @Override
    public void onDestroy () {
        mCustomTabsSceneHelper.onDestroyScene (getActivity ());

        if (mArticleWebView != null) {
            mArticleWebView.destroy ();
            mArticleWebView = null;
        }

        super.onDestroy ();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Looks like this may be called multiple times with the same menu
        // for some reason when activity is restored, so need to clear
        // to avoid duplicates
        menu.clear ();
        inflater.inflate (R.menu.menu_articles, menu);

        if (null == mArticleWebView) {
            menu.findItem (R.id.action_bookmark_article).setVisible (false);
            menu.findItem (R.id.action_find_in_page).setVisible (false);
            menu.findItem (R.id.action_zoom_in).setVisible (false);
            menu.findItem (R.id.action_zoom_out).setVisible (false);
            menu.findItem (R.id.action_zoom_reset).setVisible (false);
            menu.findItem (R.id.action_load_remote_content).setVisible (false);
            menu.findItem (R.id.action_select_style).setVisible (false);
        }

        // To change state later
        mMenuItemBookmark = menu.findItem (R.id.action_bookmark_article);
    }

    private void displayBookmarked (final boolean isBookmarked) {
        if (null != mMenuItemBookmark) {
            if (isBookmarked) {
                mMenuItemBookmark.setChecked (true);
                mMenuItemBookmark.setIcon (R.drawable.ic_bookmark_white_24dp);
            } else {
                mMenuItemBookmark.setChecked (false);
                mMenuItemBookmark.setIcon (R.drawable.ic_bookmark_border_white_24dp);
            }
        }
    }

    @SuppressWarnings ("deprecation")
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemId = item.getItemId ();

        if (itemId == R.id.action_find_in_page) {
            mArticleWebView.showFindDialog (null, true);
            return true;
        } else if (itemId == R.id.action_bookmark_article) {
            Application app = (Application) getActivity ().getApplication ();
            if (mArticleUrl != null) {
                if (item.isChecked ()) {
                    ((ArticleCollectionActivity) getActivity ()).unbookmarkCurrentTab ();
                    app.removeBookmark (mArticleUrl);
                    displayBookmarked (false);
                } else {
                    app.addBookmark (mArticleUrl);
                    displayBookmarked (true);
                }
            }
            return true;
        } else if (itemId == R.id.action_zoom_in) {
            mArticleWebView.textZoomIn ();
            return true;
        } else if (itemId == R.id.action_zoom_out) {
            mArticleWebView.textZoomOut ();
            return true;
        } else if (itemId == R.id.action_zoom_reset) {
            mArticleWebView.resetTextZoom ();
            return true;
        } else if (itemId == R.id.action_load_remote_content) {
            mArticleWebView.forceLoadRemoteContent = true;
            mArticleWebView.reload ();
            return true;
        } else if (itemId == R.id.action_select_style) {
            AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
            final String[] styleTitles = mArticleWebView.getAvailableStyles ();
            builder.setTitle (R.string.select_style)
                    .setItems (styleTitles, new DialogInterface.OnClickListener () {
                        public void onClick (DialogInterface dialog, int which) {
                            String title = styleTitles[which];
                            mArticleWebView.saveStylePref (title);
                            mArticleWebView.applyStylePref ();
                        }
                    });
            AlertDialog dialog = builder.create ();
            dialog.show ();
            return true;
        } else if (itemId == R.id.action_search_externally_article) {
            final String url = "https://www.google.com/search?q=" + mArticleTitle + "+definition";
            mCustomTabsSceneHelper.openCustomTab (getActivity (), url);
        }

        return super.onOptionsItemSelected (item);
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments ();
        mArticleUrl = args == null ? null : args.getString (Args.ARTICLE_URL);
        mArticleTitle = args == null ? ToolResources.string (R.string.wtf_emo) : args.getString (Args.ARTICLE_TITLE);

        if (mArticleUrl == null) {
            View layout = inflater.inflate (R.layout.empty_view, container, false);
            TextView textView = (TextView) layout.findViewById (R.id.empty_text);
            textView.setText ("");
            return layout;
        }

        View layout = inflater.inflate (R.layout.article_view, container, false);
        final ProgressLayout progressBar = (ProgressLayout) layout.findViewById (R.id.webViewProgress);

        mArticleWebView = (ArticleWebView) layout.findViewById (R.id.webView);
        mArticleWebView.restoreState (savedInstanceState);
        mArticleWebView.loadUrl (mArticleUrl);

        mArticleWebView.setWebChromeClient (new WebChromeClient () {
            public void onProgressChanged (WebView view, final int newProgress) {
                final Activity activity = getActivity ();
                if (activity != null) {
                    activity.runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            progressBar.setCurrentProgress (newProgress);
                            if (newProgress >= 100) {
                                progressBar.setVisibility (ViewGroup.GONE);
                            }
                        }
                    });
                }
            }
        });

        return layout;
    }

    @Override
    public void onResume () {
        super.onResume ();

        applyTextZoomPref ();
        applyStylePref ();
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu (menu);

        if (mArticleUrl == null) {
            mMenuItemBookmark.setVisible (false);
        } else {
            try {
                displayBookmarked (Application.app ().isBookmarked (mArticleUrl));
            } catch (final Exception exception) {
                mMenuItemBookmark.setVisible (false);
            }
        }

        applyTextZoomPref ();
        applyStylePref ();
    }

    public void applyTextZoomPref () {
        if (mArticleWebView != null) {
            mArticleWebView.applyTextZoomPref ();
        }
    }

    public void applyStylePref () {
        if (mArticleWebView != null) {
            mArticleWebView.applyStylePref ();
        }
    }

    public boolean onKeyUp (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                final Activity scene = getActivity ();
                if (scene instanceof BaseActivity) {
                    final Toolbar toolbar = ((BaseActivity) scene).getPrimaryToolbar ();

                    if (null != toolbar && toolbar.isOverflowMenuShowing ()) {
                        // toolbar.hideOverflowMenu();
                        toolbar.dismissPopupMenus ();
                        return true;
                    } else if (null != toolbar) {
                        toolbar.showOverflowMenu ();
                        return true;
                    }
                }

                break;
        }

        return false;
    }

}