package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
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
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;
import org.brainail.EverboxingLexis.utils.tool.ToolPrint;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.util.Locale;

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
    private MenuItem mMenuItemTts;

    private TextToSpeech mTts;
    private boolean mIsTtsAvailable = false;

    private Handler mHandler;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setHasOptionsMenu (true);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        // ...
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);

        mHandler = new Handler ();

        if (null == mTts) {
            mTts = new TextToSpeech (getActivity (), mOnInitTtsListener);
        }
    }

    private TextToSpeech.OnInitListener mOnInitTtsListener = new TextToSpeech.OnInitListener () {
        @Override
        public void onInit (int status) {
            if (null != mTts && TextToSpeech.SUCCESS == status && null != mArticleTitle) {
                final int ttsLanguageResult = mTts.setLanguage (ttsLocale ());
                // final int ttsSpeechRateResult = mTts.setSpeechRate (0.9f);
                final int ttsSpeechRateResult = TextToSpeech.SUCCESS;

                if (TextToSpeech.LANG_MISSING_DATA == ttsLanguageResult
                        || TextToSpeech.LANG_NOT_SUPPORTED == ttsLanguageResult) {
                    // We don't nullify TTS to know that lang isn't supported/installed
                    mIsTtsAvailable = false;
                } else if (TextToSpeech.SUCCESS == ttsSpeechRateResult) {
                    // Let's speak later!
                    mIsTtsAvailable = true;
                } else {
                    mTts = null;
                    mIsTtsAvailable = false;
                }
            } else {
                mTts = null;
                mIsTtsAvailable = false;
            }

            enableTtsMenuItem (null != mTts);
        }
    };

    private Locale ttsLocale () {
        // return Locale.getDefault ();
        return SettingsManager.getInstance ().retrieveSpeechLanguage ();
    }

    private void enableTtsMenuItem (boolean enabled) {
        if (enabled) {
            if (null != mMenuItemTts) {
                mMenuItemTts.setVisible (true);
                final String ttsLocaleExtra = " (" + ttsLocale ().getDisplayLanguage () + ")";
                mMenuItemTts.setTitle (ToolResources.string (R.string.action_tts) + ttsLocaleExtra);
            }
        } else {
            if (null != mMenuItemTts) {
                mMenuItemTts.setVisible (false);
            }
        }
    }

    private void finishTts () {
        if (null != mTts) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }
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
    public void onDetach () {
        super.onDetach ();
        finishTts ();
        mHandler.removeCallbacks (mLiftUpFabAction);
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
        mMenuItemTts = menu.findItem (R.id.action_tts);
        enableTtsMenuItem (null != mTts);

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
        } else if (itemId == R.id.action_tts) {
            if (null != mTts && null != mArticleTitle) {
                if (mIsTtsAvailable) {
                    mTts.speak (mArticleTitle, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    startActivity(new Intent (TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA));
                }
            }
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String item, String itemTag) {
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
                    mHandler.removeCallbacks (mLiftUpFabAction);
                    mHandler.postDelayed (mLiftUpFabAction, 3_000);
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

    private Runnable mLiftUpFabAction = new Runnable () {
        @Override
        public void run () {
            mFabMenuRight.showMenu (true);
        }
    };

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
    };

    @Override
    public void onStart () {
        super.onStart ();

        // Check tts
        if (null != mTts && ! mIsTtsAvailable) {
            final int ttsLanguageResult = mTts.setLanguage(ttsLocale ());
            mIsTtsAvailable = ! (TextToSpeech.LANG_MISSING_DATA == ttsLanguageResult
                    || TextToSpeech.LANG_NOT_SUPPORTED == ttsLanguageResult);
        }
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