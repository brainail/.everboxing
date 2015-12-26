package itkach.aard2.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.callable.Tagable;

public abstract class BaseListFragment extends ListFragment implements Tagable {

    View mEmptyPlaceholderView;
    ActionMode mActionMode;

    public Drawable getEmptyStateIcon () {
        return null;
    }

    public abstract CharSequence getEmptyText ();

    @Override
    public String tag () {
        return "Everboxing#" + getClass ().getSimpleName ();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setHasOptionsMenu (true);
        setRetainInstance (true);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEmptyPlaceholderView = inflater.inflate (R.layout.view_empty_page, container, false);
        return super.onCreateView (inflater, container, savedInstanceState);
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
