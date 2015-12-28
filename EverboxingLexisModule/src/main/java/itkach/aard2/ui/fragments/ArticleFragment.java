package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogs;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogsCode;
import org.brainail.EverboxingLexis.utils.Sdk;
import org.brainail.EverboxingLexis.utils.tool.ToolPrint;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import co.mobiwise.library.ProgressLayout;
import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.views.ArticleWebView;

public class ArticleFragment
        extends BaseFragment
        implements HardyDialogFragment.OnDialogListActionCallback {

    public static class Args {
        public static final String ARTICLE_URL = "articleUrl";
        public static final String ARTICLE_TITLE = "article_title";
    }

    private ArticleWebView mArticleWebView;
    private ProgressLayout mArticleLoadingProgress;

    private String mArticleUrl;
    private String mArticleTitle;

    private FloatingActionMenu mFabMenuRight;
    private FloatingActionButton mFabZoomIn;
    private FloatingActionButton mFabZoomOut;

    private MenuItem mMenuItemBookmark;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setHasOptionsMenu (true);
    }

    @Override
    public void onDestroy () {
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

        // To change state later
        mMenuItemBookmark = menu.findItem (R.id.action_bookmark_article);

        if (null == mArticleWebView) {
            mMenuItemBookmark.setVisible (false);
            menu.findItem (R.id.action_find_in_page).setVisible (false);
            menu.findItem (R.id.action_zoom_reset).setVisible (false);
            menu.findItem (R.id.action_load_remote_content).setVisible (false);
            menu.findItem (R.id.action_select_style).setVisible (false);
            menu.findItem (R.id.action_print_article).setVisible (false);
        }

        if (! Sdk.isSdkSupported (Sdk.KITKAT)) {
            menu.findItem (R.id.action_print_article).setVisible (false);                                    
        }
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
        } else if (itemId == R.id.action_zoom_reset) {
            mArticleWebView.resetTextZoom ();
            return true;
        } else if (itemId == R.id.action_load_remote_content) {
            mArticleWebView.enableForceLoadRemoteContent (true);
            mArticleWebView.reload ();
            return true;
        } else if (itemId == R.id.action_bookmark_article) {
            if (mArticleUrl != null) {
                if (item.isChecked ()) {
                    ((ArticleCollectionActivity) getActivity ()).unbookmarkCurrentTab ();
                    Application.app ().removeBookmark (mArticleUrl);
                    displayBookmarked (false);
                } else {
                    Application.app ().addBookmark (mArticleUrl);
                    displayBookmarked (true);
                }
            }
            return true;
        } else if (itemId == R.id.action_select_style) {
            final String [] styles = mArticleWebView.getAvailableStyles ();
            LexisPaperHardyDialogs.articleDailyStyleDialog ()
                    .items (styles).tags (styles).setCallbacks (this).show(this);
        } else if (itemId == R.id.action_search_externally_article) {
            openUrl ("https://www.google.com/search?q=" + mArticleTitle + "+definition");
        } else if (itemId == R.id.action_print_article) {
            ToolPrint.print (getActivity (), mArticleWebView, mArticleTitle);
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String itemTag) {
        if (dialog.isDialogWithCode (LexisPaperHardyDialogsCode.D_ARTICLE_DAILY_STYLE)) {
            mArticleWebView.saveStylePref (itemTag);
            mArticleWebView.applyStylePref ();
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != getArguments ()) {
            mArticleUrl = getArguments ().getString (Args.ARTICLE_URL);
            mArticleTitle = getArguments ().getString (Args.ARTICLE_TITLE);
        } else {
            mArticleUrl = null;
            mArticleTitle = ToolResources.string (R.string.wtf_emo);
        }

        if (mArticleUrl == null) {
            View layout = inflater.inflate (R.layout.view_empty_page, container, false);
            TextView textView = (TextView) layout.findViewById (R.id.empty_text);
            textView.setText ("");
            ToolUI.updateVisibility (mFabMenuRight, View.GONE);
            return layout;
        }

        final View layout = inflater.inflate (R.layout.view_page_article, container, false);

        mFabMenuRight = (FloatingActionMenu) layout.findViewById (R.id.fab_menu);
        ToolUI.updateVisibility (mFabMenuRight, View.VISIBLE);
        mFabMenuRight.setClosedOnTouchOutside (true);

        mFabZoomIn = (FloatingActionButton) layout.findViewById (R.id.fab_menu_item_zoom_in);
        mFabZoomIn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                mArticleWebView.textZoomIn ();
            }
        });

        mFabZoomOut = (FloatingActionButton) layout.findViewById (R.id.fab_menu_item_zoom_out);
        mFabZoomOut.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                mArticleWebView.textZoomOut ();
            }
        });

        mArticleWebView = (ArticleWebView) layout.findViewById (R.id.webView);
        mArticleLoadingProgress = (ProgressLayout) layout.findViewById (R.id.webViewProgress);

        mArticleWebView.restoreState (savedInstanceState);
        mArticleWebView.loadUrl (mArticleUrl);
        mArticleWebView.setWebChromeClient (mArticleWebChromeClient);

        mArticleWebView.setOnScrollDirectionListener (new ArticleWebView.OnScrollDirectionListener () {
            @Override
            public void onScrollUp () {
                if (null != mFabMenuRight && ! mFabMenuRight.isOpened ()) {
                    mFabMenuRight.showMenu (true);
                }
            }

            @Override
            public void onScrollDown () {
                if (null != mFabMenuRight && ! mFabMenuRight.isOpened ()) {
                    mFabMenuRight.hideMenu (true);
                }
            }
        });

        mArticleWebView.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction ()) {
                    if (null != mFabMenuRight) {
                        mFabMenuRight.close (true);
                    }
                }

                return false;
            }
        });

        return layout;
    }

    private final WebChromeClient mArticleWebChromeClient = new WebChromeClient () {
        @Override
        public void onProgressChanged (final WebView view, final int progress) {
            final Activity activity = getActivity ();
            if (activity != null) {
                activity.runOnUiThread (new Runnable () {
                    @Override
                    public void run () {
                        mArticleLoadingProgress.setCurrentProgress (progress);
                        ToolUI.updateVisibility (mArticleLoadingProgress, progress >= 100 ? View.GONE : View.VISIBLE);
                    }
                });
            }
        }

        // @Override
        // https://bugzilla.wikimedia.org/show_bug.cgi?id=31484
        // If you DO NOT want to start selection by long click,
        // the remove this function
        // (All this is undocumented stuff...)
        public void onSelectionStart(WebView view) {
            // By default we cancel the selection again, thus disabling
            // text selection unless the chrome client supports it.
            // view.notifySelectDialogDismissed();
            ToolUI.showToast ((AppCompatActivity) getActivity (), "Ha-ha-ha!");
        }
    };

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

    public boolean onBackPressed () {
        if (null != mFabMenuRight && mFabMenuRight.isOpened ()) {
            mFabMenuRight.close (true);
            return true;
        }

        return false;
    }

    public boolean onActionModeStarted (ActionMode mode) {
        // Fix colors (custom temp hacky way)
        final View actionCustomView = mode.getCustomView ();
        if (actionCustomView instanceof ViewGroup) {
            final ViewGroup actionCustomViewGroup = (ViewGroup) actionCustomView;
            for (int viewIndex = 0; viewIndex < actionCustomViewGroup.getChildCount (); ++ viewIndex) {
                final View subView = actionCustomViewGroup.getChildAt (viewIndex);
                if (subView instanceof TextView) {
                    ((TextView) subView).setTextColor (getResources ().getColor (R.color.md_grey_600));
                    ((TextView) subView).setHintTextColor (getResources ().getColor (R.color.md_grey_600));
                }
            }
        }

        return false;
    }

}