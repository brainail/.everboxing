package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.fragments.BaseFragment;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogs;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogsCode;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.Sdk;
import org.brainail.EverboxingLexis.utils.js.ProcessContentJsInterface;
import org.brainail.EverboxingLexis.utils.manager.SettingsManager;
import org.brainail.EverboxingLexis.utils.tool.ToolPrint;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;
import org.brainail.EverboxingLexis.utils.tool.ToolStrings;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.util.List;
import java.util.Locale;

import co.mobiwise.library.ProgressLayout;
import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.views.ArticleWebView;

public class ArticleFragment
        extends BaseFragment
        implements HardyDialogFragment.OnDialogListActionCallback, ProcessContentJsInterface.ISelectionHelper {

    public static class Args {
        public static final String ARTICLE_URL = "article.url";
        public static final String ARTICLE_TITLE = "article.title";
    }

    private ArticleWebView mArticleWebView;
    private ProgressLayout mArticleLoadingProgress;

    private String mArticleUrl;
    private String mArticleTitle;

    private FloatingActionMenu mFabMenuRight;
    private FloatingActionButton mFabZoomIn;
    private FloatingActionButton mFabZoomOut;

    private Menu mMenu;
    private MenuItem mMenuItemBookmark;
    private MenuItem mMenuItemTts;
    private MenuItem mMenuItemTtsAll;

    private TextToSpeech mTts;
    private boolean mIsTtsAvailable = false;

    private volatile List<String> mAllTextSelection;
    private volatile List<String> mPartialTextSelection;

    private Handler mHandler;

    private ActionMode mActionMode;

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
        return SettingsManager.getInstance ().retrieveSpeechLanguage ();
    }

    @Nullable public WebView webView () {
        return mArticleWebView;
    }

    public void scrollToTop () {
        if (null != mArticleWebView) {
            mArticleWebView.scrollTo (0, 0);
        }
    }

    public void scrollToBottom () {
        if (null != mArticleWebView) {
            mArticleWebView.scrollTo (0, (int) (mArticleWebView.getContentHeight () * mArticleWebView.getScale ()));
        }
    }

    private void enableTtsMenuItem (boolean enabled) {
        if (enabled) {
            if (null != mMenuItemTts) {
                mMenuItemTts.setVisible (true);
                mMenuItemTtsAll.setVisible (true);
                final String ttsLocaleExtra = " (" + ttsLocale ().getDisplayLanguage () + ")";
                mMenuItemTts.setTitle (ToolResources.string (R.string.action_tts) + ttsLocaleExtra);
                mMenuItemTtsAll.setTitle (ToolResources.string (R.string.action_tts_all) + ttsLocaleExtra);
            }
        } else {
            if (null != mMenuItemTts) {
                mMenuItemTts.setVisible (false);
                mMenuItemTtsAll.setVisible (false);
            }
        }
    }

    private void finishTts () {
        if (null != mTts) {
            mTts.stop ();
            mTts.shutdown ();
            mTts = null;
        }
    }

    @Override
    public void onDestroy () {
        if (mArticleWebView != null) {
            mArticleWebView.performOnDestroy ();
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
        mMenu = menu;

        // To change state later
        mMenuItemBookmark = menu.findItem (R.id.action_bookmark_article);
        mMenuItemTts = menu.findItem (R.id.action_tts);
        mMenuItemTtsAll = menu.findItem (R.id.action_tts_all);
        mMenuItemTtsAll.setEnabled (null != mAllTextSelection && ! mAllTextSelection.isEmpty ());
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

        if (! SettingsManager.getInstance ().retrieveShouldDisplayFabZoom ()) {
            menu.findItem (R.id.action_zoom_in).setVisible (true);
            menu.findItem (R.id.action_zoom_out).setVisible (true);
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
        } else if (itemId == R.id.action_zoom_in) {
            mArticleWebView.textZoomIn ();
            return true;
        } else if (itemId == R.id.action_zoom_out) {
            mArticleWebView.textZoomOut ();
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
            final String[] styles = mArticleWebView.getAvailableStyles ();
            LexisPaperHardyDialogs.articleDailyStyleDialog ()
                    .items (styles).tags (styles).setCallbacks (this).show (this);
        } else if (itemId == R.id.action_search_externally_article) {
            openUrl ("https://www.google.com/search?q=" + mArticleTitle + "+definition");
        } else if (itemId == R.id.action_print_article) {
            ToolPrint.print (getActivity (), mArticleWebView, mArticleTitle);
        } else if (itemId == R.id.action_tts) {
            if (null != mTts && null != mArticleTitle) {
                if (mIsTtsAvailable) {
                    mTts.speak (mArticleTitle, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    startActivity (new Intent (TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA));
                }
            }
        } else if (itemId == R.id.action_tts_all) {
            speakSelectionText (mAllTextSelection);
        }

        return super.onOptionsItemSelected (item);
    }

    public void speakSelectionText (final List<String> text) {
        if (null != mTts && null != text && !text.isEmpty ()) {
            if (mIsTtsAvailable) {
                mTts.stop ();
                for (int textIndex = 0; textIndex < text.size (); ++textIndex) {
                    mTts.speak (text.get (textIndex), TextToSpeech.QUEUE_ADD, null);
                }
            } else {
                startActivity (new Intent (TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA));
            }
        }
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

        if (!SettingsManager.getInstance ().retrieveShouldDisplayFabZoom ()) {
            ((ViewGroup) layout).removeView (mFabMenuRight);
            mFabMenuRight = null;
        } else {
            ToolUI.updateVisibility (mFabMenuRight, View.VISIBLE);
        }

        if (null != mFabMenuRight) {
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
        }

        mArticleWebView = (ArticleWebView) layout.findViewById (R.id.webView);
        mArticleWebView.setSelectionHelper (this);
        mArticleLoadingProgress = (ProgressLayout) layout.findViewById (R.id.webViewProgress);

        mArticleWebView.restoreState (savedInstanceState);
        mArticleWebView.loadUrl (mArticleUrl);
        mArticleWebView.setWebChromeClient (mArticleWebChromeClient);

        mArticleWebView.setOnScrollDirectionListener (new ArticleWebView.OnScrollDirectionListener () {
            @Override
            public void onScrollUp () {
                if (null != mFabMenuRight && !mFabMenuRight.isOpened ()) {
                    mFabMenuRight.showMenu (true);
                }
            }

            @Override
            public void onScrollDown () {
                if (null != mFabMenuRight && !mFabMenuRight.isOpened ()) {
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
            if (null != mFabMenuRight) {
                mFabMenuRight.showMenu (true);
            }
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

        public boolean onConsoleMessage (final ConsoleMessage cm) {
            Plogger.logW ("Console message: %s.\n-- From line: %d", cm.message (), cm.lineNumber ());
            return true;
        }
    };

    @Override
    public void onStart () {
        super.onStart ();

        // Check tts
        if (null != mTts && !mIsTtsAvailable) {
            final int ttsLanguageResult = mTts.setLanguage (ttsLocale ());
            mIsTtsAvailable = !(TextToSpeech.LANG_MISSING_DATA == ttsLanguageResult
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

        mMenuItemTtsAll.setEnabled (null != mAllTextSelection && !mAllTextSelection.isEmpty ());

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
        mActionMode = mode;

        // Fix colors (custom temp hacky way)
        final View actionCustomView = mode.getCustomView ();
        if (actionCustomView instanceof ViewGroup) {
            final ViewGroup actionCustomViewGroup = (ViewGroup) actionCustomView;
            for (int viewIndex = 0; viewIndex < actionCustomViewGroup.getChildCount (); ++viewIndex) {
                final View subView = actionCustomViewGroup.getChildAt (viewIndex);
                if (subView instanceof TextView) {
                    ((TextView) subView).setTextColor (getResources ().getColor (R.color.md_grey_600));
                    ((TextView) subView).setHintTextColor (getResources ().getColor (R.color.md_grey_600));
                }
            }
        }

        // Try to grab selection
        if (null != mArticleWebView) {
            mArticleWebView.processSelection ();
        }

        return false;
    }

    public boolean onActionModeFinished (ActionMode mode) {
        mActionMode = null;
        return false;
    }

    @Override
    public void onAllTextSelection (String selection) {
        mAllTextSelection = ToolStrings.graTtsWords (selection);

        if (! (! mAllTextSelection.isEmpty () && null != mMenuItemTtsAll && null != mArticleWebView)) {
            return;
        }

        mHandler.post (new Runnable () {
            @Override
            public void run () {
                mMenuItemTtsAll.setEnabled (true);
                final AppCompatActivity scene = (AppCompatActivity) getActivity ();
                if (null != scene && !scene.isFinishing ()) {
                    scene.supportInvalidateOptionsMenu ();
                }
            }
        });
    }

    @Override
    public void onPartialTextSelection (String selection) {
        mPartialTextSelection = ToolStrings.graTtsWords (selection);

        if (! (! TextUtils.isEmpty(selection) && null != mActionMode && null != mArticleWebView)) {
            return;
        }

        mHandler.post (new Runnable () {
            @Override
            public void run () {
                if (null == mActionMode.getMenu ().findItem (R.id.action_tts_selection)) {
                    final String ttsLocaleExtra
                            = " (" + ttsLocale ().getDisplayLanguage () + ")";
                    final String menuItemTitle
                            = ToolResources.string (R.string.action_tts_selection) + ttsLocaleExtra;
                    final MenuItem menuItem = mActionMode.getMenu ()
                            .add (Menu.NONE, R.id.action_tts_selection, Menu.NONE, menuItemTitle);

                    menuItem.setShowAsAction (MenuItem.SHOW_AS_ACTION_NEVER);
                    menuItem.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener () {
                        @Override
                        public boolean onMenuItemClick (MenuItem item) {
                            mPartialTextSelection = null;
                            if (null != mArticleWebView) {
                                mArticleWebView.processSelection ();
                            }

                            return true;
                        }
                    });

                    final AppCompatActivity scene = (AppCompatActivity) getActivity ();
                    if (null != scene && !scene.isFinishing ()) {
                        scene.supportInvalidateOptionsMenu ();
                    }
                } else {
                    speakSelectionText (mPartialTextSelection);
                }
            }
        });
    }

}