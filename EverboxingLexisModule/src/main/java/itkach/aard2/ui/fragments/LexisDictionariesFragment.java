package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.fragments.BaseListFragment;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogs;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.io.File;

import itkach.aard2.Application;
import itkach.aard2.callbacks.DictionaryDiscoveryCallback;
import itkach.aard2.ui.activities.FileSelectActivity;
import itkach.aard2.ui.adapters.DictionaryListAdapter;

import static org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogsCode.D_DICTIONARY_SCANNING_PROGRESS;

public class LexisDictionariesFragment extends BaseListFragment {

    final static int FILE_SELECT_REQUEST = 17;

    private boolean mShouldScanDictionariesOnAttach = false;

    @Override
    protected boolean supportsSelection () {
        return false;
    }

    private final DataSetObserver mDataObserver = new DataSetObserver () {
        @Override
        public void onChanged () {
            check ();
        }

        public void check () {
            final ListAdapter adapter = getListAdapter ();
            if (null != adapter && adapter.getCount () > 0) {
                detachFabStores ();
            } else {
                attachFabStores ();
            }
        }
    };

    private void attachFabStores () {
        final ViewGroup root = (ViewGroup) mEmptyPlaceholderView.getParent ();
        if (null == root.findViewById (R.id.stores_fab_menu)) {
            FloatingActionButton fabStore;
            LayoutInflater.from (getActivity ()).inflate (R.layout.view_empty_dict_page_fab, root);

            final FloatingActionMenu fabMenu = (FloatingActionMenu) root.findViewById (R.id.stores_fab_menu);
            fabMenu.setClosedOnTouchOutside (true);

            fabStore = (FloatingActionButton) fabMenu.findViewById (R.id.fab_menu_item_store_one);
            fabStore.setOnClickListener (mFabStoreOnClickListener);
            fabStore.setTag (1);

            fabStore = (FloatingActionButton) fabMenu.findViewById (R.id.fab_menu_item_store_two);
            fabStore.setOnClickListener (mFabStoreOnClickListener);
            fabStore.setTag (2);
        }
    }

    private final View.OnClickListener mFabStoreOnClickListener = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            openStore ((int) v.getTag ());

            final ViewGroup root = (ViewGroup) mEmptyPlaceholderView.getParent ();
            final FloatingActionMenu fabMenu = (FloatingActionMenu) root.findViewById (R.id.stores_fab_menu);
            if (null != fabMenu) {
                fabMenu.close (true);
            }
        }
    };

    private void detachFabStores () {
        final ViewGroup root = (ViewGroup) mEmptyPlaceholderView.getParent ();
        final View fabMenu = root.findViewById (R.id.stores_fab_menu);
        if (null != fabMenu) {
            root.removeView (fabMenu);
        }
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        // Dicts
        setListAdapter (new DictionaryListAdapter (Application.app ().dictionaries, getActivity ()));
        getListAdapter ().registerDataSetObserver (mDataObserver);

        // Stores
        mDataObserver.onChanged ();
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView ();
        getListAdapter ().unregisterDataSetObserver (mDataObserver);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_dictionaries, menu);
    }

    @Override
    public void onPrepareOptionsMenu (final Menu menu) {}

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == R.id.action_find_dictionaries) {
            findDictionaries ();
            return true;
        } else if (item.getItemId () == R.id.action_add_dictionaries) {
            Intent intent = new Intent (getActivity (), FileSelectActivity.class);
            startActivityForResult (intent, FILE_SELECT_REQUEST);
            return true;
        } else if (item.getItemId () == R.id.action_dic_store_mirror_one) {
            openStore (1);
        } else if (item.getItemId () == R.id.action_dic_store_mirror_two) {
            openStore (2);
        }

        return super.onOptionsItemSelected (item);
    }

    public void openStore (final int storeIndex) {
        switch (storeIndex) {
            case 1:
                openUrl ("https://cloud.mail.ru/public/8GsF/8RzDA9wBR");
                break;
            case 2:
                openUrl ("https://github.com/itkach/slob/wiki/Dictionaries");
                break;
            case 3:
                openUrl ("https://yadi.sk/d/M8uRyFsCne3cf");
                break;
            default:
                break;
        }
    }

    public void findDictionaries () {
        final Activity activity = getActivity ();
        if (activity == null) {
            mShouldScanDictionariesOnAttach = true;
            return;
        }

        mShouldScanDictionariesOnAttach = false;
        Application.app ().findDictionaries (new DictionaryDiscoveryCallback () {
            @Override
            public void onDiscoveryFinished () {
                HardyDialogsHelper.dismissDialog (LexisDictionariesFragment.this, D_DICTIONARY_SCANNING_PROGRESS);
            }
        });

        LexisPaperHardyDialogs.dictionaryScanningDialog ().show (this);
    }


    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);

        if (mShouldScanDictionariesOnAttach) {
            findDictionaries ();
        }
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        getListView ().setDivider (null);
    }

    @Override
    protected boolean shouldHideKeyboardOnScroll () {
        return false;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode != FILE_SELECT_REQUEST) {
            return;
        }

        String selectedPath = data == null ? null : data.getStringExtra (FileSelectActivity.KEY_SELECTED_FILE_PATH);
        if (resultCode == AppCompatActivity.RESULT_OK && !TextUtils.isEmpty (selectedPath)) {
            final File dicFile = new File (selectedPath);
            final boolean alreadyExists = Application.app ().addDictionary (dicFile);

            String toastMessage;
            if (alreadyExists) {
                toastMessage = getString (R.string.msg_dictionary_already_open);
            } else {
                toastMessage = getString (R.string.msg_dictionary_added, dicFile.getName ());
            }

            ToolUI.showToast (LexisDictionariesFragment.this, toastMessage);
        }
    }

}
