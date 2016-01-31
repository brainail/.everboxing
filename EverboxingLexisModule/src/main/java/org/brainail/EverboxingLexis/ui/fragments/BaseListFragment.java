package org.brainail.EverboxingLexis.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.squareup.leakcanary.RefWatcher;

import org.brainail.EverboxingLexis.JApplication;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.utils.callable.Tagable;
import org.brainail.EverboxingLexis.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingLexis.utils.tool.ToolFragments;

public abstract class BaseListFragment extends ListFragment implements Tagable {

    protected ViewGroup mEmptyPlaceholderView;
    protected ActionMode mActionMode;

    // Chrome tabs stuff
    private CustomTabsSceneHelper mCustomTabsSceneHelper;

    @Override
    public String tag () {
        return ToolFragments.getTag (getClass());
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setHasOptionsMenu (true);
        setRetainInstance (true);

        // Chrome tabs stuff
        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.onCreateScene (getActivity ());
    }

    @Override
    public void onStart () {
        super.onStart ();

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStartScene (getActivity ());
    }

    @Override
    public void onStop () {
        super.onStop ();

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStopScene (getActivity ());
    }

    @Override
    public void onDestroy () {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.onDestroyScene (getActivity ());

        super.onDestroy ();

        final RefWatcher refWatcher = JApplication.refWatcher (getActivity());
        if (null != refWatcher) refWatcher.watch (this);
    }

    public void openUrl (final String url) {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.openCustomTab (getActivity (), url, null, null);
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

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEmptyPlaceholderView = (ViewGroup) inflater.inflate (R.layout.view_empty_page, container, false);
        return super.onCreateView (inflater, container, savedInstanceState);
    }

    public final void finishCurrentActionMode () {
        if (null != mActionMode) {
            mActionMode.finish ();
            mActionMode = null;
        }
    }

    protected void setSelectionMode (boolean selectionMode) {}

    protected int getSelectionMenuId () {
        return 0;
    }

    protected boolean onSelectionActionItemClicked (final ActionMode mode, MenuItem item) {
        return false;
    }

    protected boolean supportsSelection () {
        return true;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        final ListView items = getListView ();
        getListView ().setEmptyView (mEmptyPlaceholderView);
        ((ViewGroup) getListView ().getParent ()).addView (mEmptyPlaceholderView, 0);

        if (supportsSelection ()) {
            items.setItemsCanFocus (false);
            items.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE_MODAL);

            items.setMultiChoiceModeListener (new AbsListView.MultiChoiceModeListener () {
                @Override
                public boolean onCreateActionMode (ActionMode mode, Menu menu) {
                    mActionMode = mode;
                    mode.getMenuInflater ().inflate (getSelectionMenuId (), menu);
                    setSelectionMode (true);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode (ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked (final ActionMode mode, MenuItem item) {
                    return onSelectionActionItemClicked (mode, item);
                }

                @Override
                public void onDestroyActionMode (ActionMode mode) {
                    setSelectionMode (false);
                    mActionMode = null;
                }

                @Override
                public void onItemCheckedStateChanged (ActionMode mode, int position, long id, boolean checked) {}
            });
        }
    }

}
