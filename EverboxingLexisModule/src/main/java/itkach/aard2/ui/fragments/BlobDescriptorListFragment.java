package itkach.aard2.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogs;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogsCode;

import itkach.aard2.slob.BlobDescriptorList;
import itkach.aard2.ui.activities.ArticleCollectionActivity;
import itkach.aard2.ui.adapters.BlobDescriptorListAdapter;

public abstract class BlobDescriptorListFragment
        extends BaseListFragment
        implements HardyDialogFragment.OnDialogActionCallback {

    private BlobDescriptorListAdapter listAdapter;

    private ActionMode mLastActionMode;

    private final static String PREF_SORT_ORDER = "sortOrder";
    private final static String PREF_SORT_DIRECTION = "sortDir";

    public abstract BlobDescriptorList getDescriptorList();

    public abstract String getItemClickAction();

    protected void setSelectionMode(boolean selectionMode) {
        listAdapter.setSelectionMode(selectionMode);
    }

    protected int getSelectionMenuId() {
        return R.menu.menu_list_selection;
    }

    public abstract int getDeleteConfirmationItemCountResId();

    public abstract String getPreferencesNS();

    private SharedPreferences prefs() {
        return getActivity().getSharedPreferences(getPreferencesNS(), AppCompatActivity.MODE_PRIVATE);
    }

    protected boolean onSelectionActionItemClicked(final ActionMode mode, MenuItem item) {
        final ListView listView = getListView();
        mLastActionMode = mode;

        switch (item.getItemId()) {
            case R.id.blob_descriptor_delete:
                final int count = listView.getCheckedItemCount ();
                final int countResId = getDeleteConfirmationItemCountResId ();
                final String sCount = getResources().getQuantityString(countResId, count, count);
                LexisPaperHardyDialogs.listItemsRemovingConfirmationDialog (sCount).setCallbacks (this).show (this);
                return true;
            case R.id.blob_descriptor_select_all:
                int itemCount = listView.getCount();
                for (int i = itemCount - 1; i > -1; --i) {
                    listView.setItemChecked(i, true);
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDialogAction (HardyDialogFragment dialog, int actionRequestCode) {
        if (dialog.isDialogWithCode (LexisPaperHardyDialogsCode.D_LIST_ITEMS_REMOVING_CONFIRMATION)) {
            if (HardyDialogFragment.ActionRequestCode.POSITIVE == actionRequestCode) {
                deleteSelectedItems();
                finishLastActionMode ();
            }
        }
    }

    public final void finishLastActionMode () {
        if (null != mLastActionMode) {
            mLastActionMode.finish ();
            mLastActionMode = null;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        BlobDescriptorList descriptorList = getDescriptorList();

        SharedPreferences p = this.prefs();

        String sortOrderStr = p.getString(PREF_SORT_ORDER, BlobDescriptorList.SortOrder.TIME.name());
        BlobDescriptorList.SortOrder sortOrder = BlobDescriptorList.SortOrder.valueOf(sortOrderStr);

        boolean sortDir = p.getBoolean(PREF_SORT_DIRECTION, false);

        descriptorList.setSort(sortOrder, sortDir);

        listAdapter = new BlobDescriptorListAdapter(descriptorList);

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity (), ArticleCollectionActivity.class);
                intent.setAction(getItemClickAction());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        setListAdapter(listAdapter);
    }

    protected void deleteSelectedItems() {
        SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
        for (int i = checkedItems.size() - 1; i > -1; --i) {
            int position = checkedItems.keyAt(i);
            boolean checked = checkedItems.get(position);
            if (checked) {
                getDescriptorList().remove(position);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_list_filter, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final BlobDescriptorList list = getDescriptorList();

        final MenuItem menuItemFilter = menu.findItem (R.id.action_filter);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItemFilter);
        if (! TextUtils.isEmpty (list.getFilter())) {
            searchView.setIconified (false);
            searchView.setQuery (list.getFilter (), true);
            MenuItemCompat.expandActionView (menuItemFilter);
            searchView.clearFocus ();
        } else {
            menuItemFilter.setIcon (R.drawable.ic_filter_list_white_24dp);
        }

        searchView.setImeOptions(searchView.getImeOptions()
                | EditorInfo.IME_ACTION_SEARCH
                | EditorInfo.IME_FLAG_NO_EXTRACT_UI
                | EditorInfo.IME_FLAG_NO_FULLSCREEN
        );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BlobDescriptorList list = getDescriptorList();

                if (!newText.equals(list.getFilter())) {
                    getDescriptorList().setFilter(newText);
                }

                return true;
            }
        });

        setSortOrder(menu.findItem(R.id.action_sort_order), list.getSortOrder());
        setAscending(menu.findItem(R.id.action_sort_asc), list.isAscending());

        super.onPrepareOptionsMenu(menu);
    }

    private void setSortOrder(MenuItem menuItem, BlobDescriptorList.SortOrder order) {
        if (order != BlobDescriptorList.SortOrder.TIME) {
            menuItem.setIcon(R.drawable.ic_access_time_white_24dp);
            menuItem.setTitle(R.string.action_sort_by_time);
        } else {
            menuItem.setIcon(R.drawable.ic_sort_white_24dp);
            menuItem.setTitle(R.string.action_sort_by_title);
        }

        prefs().edit ().putString (PREF_SORT_ORDER, order.name()).apply ();
    }

    private void setAscending(MenuItem menuItem, boolean isAscending) {
        if (! isAscending) {
            menuItem.setIcon(R.drawable.ic_trending_up_white_24dp);
            menuItem.setTitle(R.string.action_ascending);
        } else {
            menuItem.setIcon(R.drawable.ic_trending_down_white_24dp);
            menuItem.setTitle(R.string.action_descending);
        }

        prefs().edit ().putBoolean (PREF_SORT_DIRECTION, isAscending).apply ();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        BlobDescriptorList list = getDescriptorList();
        int itemId = mi.getItemId();

        if (itemId == R.id.action_sort_asc) {
            list.setSort(!list.isAscending());
            setAscending(mi, list.isAscending());
            return true;
        } else if (itemId == R.id.action_sort_order) {
            if (list.getSortOrder() == BlobDescriptorList.SortOrder.TIME) {
                list.setSort(BlobDescriptorList.SortOrder.NAME);
            } else {
                list.setSort(BlobDescriptorList.SortOrder.TIME);
            }
            setSortOrder(mi, list.getSortOrder());
            return true;
        }

        return super.onOptionsItemSelected(mi);
    }


    @Override
    public void onPause() {
        super.onPause();
        HardyDialogsHelper.dismissDialog (this, LexisPaperHardyDialogsCode.D_LIST_ITEMS_REMOVING_CONFIRMATION);
    }

}
