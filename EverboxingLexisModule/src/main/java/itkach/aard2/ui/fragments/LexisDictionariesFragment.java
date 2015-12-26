package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;
import org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogs;
import org.brainail.EverboxingLexis.utils.callable.Tagable;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.io.File;

import itkach.aard2.Application;
import itkach.aard2.callbacks.DictionaryDiscoveryCallback;
import itkach.aard2.ui.activities.FileSelectActivity;
import itkach.aard2.ui.adapters.DictionaryListAdapter;

import static org.brainail.EverboxingLexis.ui.views.dialogs.hardy.LexisPaperHardyDialogsCode.D_DICTIONARY_SCANNING_PROGRESS;

public class LexisDictionariesFragment extends BaseListFragment implements Tagable {

    final static int FILE_SELECT_REQUEST = 17;

    private boolean mShouldScanDictionariesOnAttach = false;

    @Override
    public Drawable getEmptyStateIcon () {
        return BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_collection_text);
    }

    public CharSequence getEmptyText() {
        return Html.fromHtml(getString(R.string.main_empty_dictionaries));
    }

    @Override
    protected boolean supportsSelection() {
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new DictionaryListAdapter (Application.app ().dictionaries, getActivity ()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dictionaries, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        MenuItem miFindDictionaries = menu.findItem(R.id.action_find_dictionaries);
        miFindDictionaries.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_refresh));

        MenuItem miAddDictionaries = menu.findItem(R.id.action_add_dictionaries);
        miAddDictionaries.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_file_plus));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_find_dictionaries) {
            findDictionaries();
            return true;
        } else if (item.getItemId() == R.id.action_add_dictionaries) {
            Intent intent = new Intent(getActivity(), FileSelectActivity.class);
            startActivityForResult(intent, FILE_SELECT_REQUEST);
            return true;
        } else if (item.getItemId () == R.id.action_dic_store_mirror_one) {
            String url = "https://cloud.mail.ru/public/GZha/fW3MRhSCf";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse (url));
            getActivity ().startActivity(intent);
        } else if (item.getItemId () == R.id.action_dic_store_mirror_two) {
            String url = "https://github.com/itkach/slob/wiki/Dictionaries";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse (url));
            getActivity ().startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void findDictionaries() {
        final Activity activity = getActivity();
        if (activity == null) {
            mShouldScanDictionariesOnAttach = true;
            return;
        }

        mShouldScanDictionariesOnAttach = false;
        Application.app ().findDictionaries(new DictionaryDiscoveryCallback() {
            @Override
            public void onDiscoveryFinished() {
                HardyDialogsHelper.dismissDialog (LexisDictionariesFragment.this, D_DICTIONARY_SCANNING_PROGRESS);
            }
        });

        LexisPaperHardyDialogs.dictionaryScanningDialog ().show (this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mShouldScanDictionariesOnAttach) {
            findDictionaries();
        }
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        getListView().setDivider(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILE_SELECT_REQUEST) {
            return;
        }

        String selectedPath = data == null ? null : data.getStringExtra(FileSelectActivity.KEY_SELECTED_FILE_PATH);
        if (resultCode == AppCompatActivity.RESULT_OK && selectedPath != null && selectedPath.length() > 0) {
            boolean alreadyExists = Application.app ().addDictionary(new File(selectedPath));

            String toastMessage;
            if (alreadyExists) {
                toastMessage = getString(R.string.msg_dictionary_already_open);
            } else {
                toastMessage = getString(R.string.msg_dictionary_added, selectedPath);
            }

            ToolUI.showToast (LexisDictionariesFragment.this, toastMessage);
        }
    }

}
