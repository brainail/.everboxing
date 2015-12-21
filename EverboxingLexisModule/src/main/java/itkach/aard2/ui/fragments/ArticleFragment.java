package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
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
import org.brainail.EverboxingLexis.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import co.mobiwise.library.ProgressLayout;
import itkach.aard2.Application;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.views.ArticleWebView;

public class ArticleFragment extends Fragment implements CustomTabsSceneHelper.ConnectionCallback {

    public static final String ARG_URL = "articleUrl";

    private ArticleWebView mArticleWebView;

    private String mArticleUrl;
    private String mArticleTitle;

    private MenuItem mMenuItemBookmark;

    private CustomTabsSceneHelper mCustomTabsSceneHelper;
    private String mCurrentAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.setConnectionCallback (this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Looks like this may be called multiple times with the same menu
        // for some reason when activity is restored, so need to clear
        // to avoid duplicates
        menu.clear();
        inflater.inflate(R.menu.menu_articles, menu);

        if (null == mArticleWebView) {
            menu.findItem(R.id.action_bookmark_article).setVisible (false);
            menu.findItem(R.id.action_find_in_page).setVisible (false);
            menu.findItem(R.id.action_zoom_in).setVisible (false);
            menu.findItem(R.id.action_zoom_out).setVisible (false);
            menu.findItem(R.id.action_zoom_reset).setVisible (false);
            menu.findItem(R.id.action_load_remote_content).setVisible (false);
            menu.findItem(R.id.action_select_style).setVisible (false);
        }

        // To change state later
        mMenuItemBookmark = menu.findItem(R.id.action_bookmark_article);
    }

    private void displayBookmarked(final boolean isBookmarked) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_find_in_page) {
            mArticleWebView.showFindDialog(null, true);
            return true;
        } else if (itemId == R.id.action_bookmark_article) {
            Application app = (Application) getActivity().getApplication();
            if (this.mArticleUrl != null) {
                if (item.isChecked()) {
                    ((ArticleCollectionActivity) getActivity ()).unbookmarkCurrentTab ();
                    app.removeBookmark (this.mArticleUrl);
                    displayBookmarked (false);
                } else {
                    app.addBookmark(this.mArticleUrl);
                    displayBookmarked(true);
                }
            }
            return true;
        } else if (itemId == R.id.action_zoom_in) {
            mArticleWebView.textZoomIn();
            return true;
        } else if (itemId == R.id.action_zoom_out) {
            mArticleWebView.textZoomOut();
            return true;
        } else if (itemId == R.id.action_zoom_reset) {
            mArticleWebView.resetTextZoom();
            return true;
        } else if (itemId == R.id.action_load_remote_content) {
            mArticleWebView.forceLoadRemoteContent = true;
            mArticleWebView.reload();
            return true;
        } else if (itemId == R.id.action_select_style) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String[] styleTitles = mArticleWebView.getAvailableStyles();
            builder.setTitle(R.string.select_style)
                    .setItems(styleTitles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String title = styleTitles[which];
                            mArticleWebView.saveStylePref(title);
                            mArticleWebView.applyStylePref();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else if (itemId == R.id.action_search_externally_article) {
            openCustomTab (getActivity (), "https://www.google.com/search?q=" + mArticleTitle + "+definition");
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mArticleUrl = args == null ? null : args.getString(ARG_URL);
        mArticleTitle = args == null ? ToolResources.string (R.string.wtf_emo) : args.getString ("article_title");

        if (mArticleUrl == null) {
            View layout = inflater.inflate(R.layout.empty_view, container, false);
            TextView textView = (TextView) layout.findViewById(R.id.empty_text);
            textView.setText("");
            return layout;
        }

        View layout = inflater.inflate(R.layout.article_view, container, false);
        final ProgressLayout progressBar = (ProgressLayout) layout.findViewById(R.id.webViewProgress);
        mArticleWebView = (ArticleWebView) layout.findViewById(R.id.webView);
        mArticleWebView.restoreState(savedInstanceState);
        mArticleWebView.loadUrl(mArticleUrl);
        mArticleWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, final int newProgress) {
                final Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setCurrentProgress (newProgress);
                            if (newProgress >= 100) {
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

        if (mArticleUrl == null) {
            mMenuItemBookmark.setVisible(false);
        } else {
            try {
                boolean bookmarked = Application.app ().isBookmarked(mArticleUrl);
                displayBookmarked(bookmarked);
            } catch (Exception ex) {
                mMenuItemBookmark.setVisible(false);
            }
        }

        applyTextZoomPref();
        applyStylePref();
    }

    public void applyTextZoomPref() {
        if (mArticleWebView != null) {
            mArticleWebView.applyTextZoomPref();
        }
    }

    public void applyStylePref() {
        if (mArticleWebView != null) {
            mArticleWebView.applyStylePref();
        }
    }

    @Override public void onCustomTabsConnected () {
        // ToolUI.showSnack (getView (), "Custom Tabs > Connected");

        if (null != mCurrentAction) {
            mCustomTabsSceneHelper.mayLaunchUrl (Uri.parse (mCurrentAction), null, null);
        }
    }

    @Override public void onCustomTabsDisconnected () {
        // ToolUI.showSnack (getView (), "Custom Tabs > Disconnected");
    }

    @Override public void onStart () {
        super.onStart ();
        mCustomTabsSceneHelper.bindCustomTabsService (getActivity ());
    }

    @Override public void onStop () {
        mCustomTabsSceneHelper.unbindCustomTabsService (getActivity ());
        super.onStop ();
    }

    @Override public void onDestroy () {
        mCustomTabsSceneHelper.setConnectionCallback (null);

        if (mArticleWebView != null) {
            mArticleWebView.destroy();
            mArticleWebView = null;
        }

        super.onDestroy ();
    }

    public void openCustomTab (final Activity scene, final String url) {
        mCurrentAction = url;
        CustomTabsSceneHelper.openCustomTab (
                scene,
                getCustomTabIntent (scene, mCurrentAction, mCustomTabsSceneHelper.occupySession ()).build (),
                Uri.parse (mCurrentAction)
        );
    }

    public static CustomTabsIntent.Builder getCustomTabIntent(
            @NonNull Context context,
            @NonNull String url,
            @Nullable CustomTabsSession session) {

        // Construct our intent via builder
        final CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder (session);
        // Toolbar color
        intentBuilder.setToolbarColor(ToolResources.retrievePrimaryColor (context));
        // Show title
        intentBuilder.setShowTitle (true);
        // Custom menu item > Share
        intentBuilder.addMenuItem("Share", createPendingShareIntent (context, url));
        // Custom menu item > Email
        intentBuilder.addMenuItem("Email", createPendingEmailIntent (context, url));
        // Specify close button icon
        // final int mainCloseResId = android.support.design.R.drawable.abc_ic_ab_back_mtrl_am_alpha;
        // final Bitmap mainCloseBitmap = BitmapFactory.decodeResource (context.getResources (), mainCloseResId);
        // intentBuilder.setCloseButtonIcon (mainCloseBitmap);
        // Specify main action icon and doings
        // final int mainActionResId = android.support.design.R.drawable.abc_ic_commit_search_api_mtrl_alpha;
        // final Bitmap mainActionBitmap = BitmapFactory.decodeResource (context.getResources (), mainActionResId);
        // intentBuilder.setActionButton (mainActionBitmap, "Notify me!", createPendingMainActionNotifyIntent (context, action));
        // Custom animation (start + exit)
        // intentBuilder.setExitAnimations (context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // intentBuilder.setStartAnimations (
        // context,
        // android.support.design.R.anim.abc_slide_in_bottom,
        // android.support.design.R.anim.abc_slide_out_bottom
        // );
        // Allow hiding for toolbar
        intentBuilder.enableUrlBarHiding ();

        return intentBuilder;
    }

    private static PendingIntent createPendingEmailIntent(
            @NonNull final Context context,
            @NonNull final String action) {

        Intent actionIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        actionIntent.putExtra (Intent.EXTRA_SUBJECT, "Check this out");
        actionIntent.putExtra (Intent.EXTRA_TEXT, action);
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createPendingShareIntent(
            @NonNull final Context context,
            @NonNull final String action) {

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType ("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, "Check this out: " + action);
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}