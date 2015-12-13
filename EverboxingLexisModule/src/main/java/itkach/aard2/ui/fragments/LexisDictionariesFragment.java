package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;
import org.brainail.EverboxingLexis.utils.callable.Tagable;
import org.brainail.EverboxingLexis.utils.tool.ToolUI;

import java.io.File;

import itkach.aard2.Application;
import itkach.aard2.callbacks.DictionaryDiscoveryCallback;
import itkach.aard2.ui.activities.FileSelectActivity;
import itkach.aard2.ui.adapters.DictionaryListAdapter;

import static android.view.View.OnClickListener;

public class LexisDictionariesFragment extends BaseListFragment implements Tagable {

    private final static String TAG = LexisDictionariesFragment.class.getSimpleName();

    final static int FILE_SELECT_REQUEST = 17;

    private DictionaryListAdapter listAdapter;
    private boolean findDictionariesOnAttach = false;

    private class DiscoveryProgressDialog extends ProgressDialog {

        public DiscoveryProgressDialog(Context context) {
            super(context);
            setIndeterminate(true);
            setCancelable(false);
            setTitle(getString(R.string.dictionaries_please_wait));
            setMessage(getString(R.string.dictionaries_scanning_device));
        }

        @Override
        public void onBackPressed() {
            final Application app = (Application) getActivity().getApplication();
            app.cancelFindDictionaries();
        }
    }


    public int getEmptyIcon() {
        return android.R.drawable.ic_menu_help;
    }

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
        final Application app = (Application) getActivity().getApplication();
        listAdapter = new DictionaryListAdapter(app.dictionaries, getActivity());
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        View extraEmptyView = inflater.inflate(R.layout.dictionaries_empty_view_extra, container, false);
        Button btn = (Button) extraEmptyView.findViewById(R.id.dictionaries_empty_btn_find_online);
        btn.setCompoundDrawablesWithIntrinsicBounds(
                BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_smartphone_download),
                null, null, null);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // findDictionaries();
                String url = "https://github.com/itkach/slob/wiki/Dictionaries";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse (url));
                // intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity ().startActivity(intent);
            }
        });
        LinearLayout emptyViewLayout = (LinearLayout) emptyView;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emptyViewLayout.addView(extraEmptyView, layoutParams);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dictionaries, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        MenuItem miFindDictionaries = menu.findItem(R.id.action_find_dictionaries);
        FragmentActivity activity = getActivity();
        miFindDictionaries.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_refresh));
        MenuItem miAddDictionaries = menu.findItem(R.id.action_add_dictionaries);
        miAddDictionaries.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_file_plus));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_find_dictionaries) {
            findDictionaries();
            return true;
        }
        if (item.getItemId() == R.id.action_add_dictionaries) {
            Intent intent = new Intent(getActivity(), FileSelectActivity.class);
            startActivityForResult(intent, FILE_SELECT_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void findDictionaries() {
        Activity activity = getActivity();
        if (activity == null) {
            this.findDictionariesOnAttach = true;
            return;
        }
        this.findDictionariesOnAttach = false;
        final Application app = ((Application) activity.getApplication());
        final ProgressDialog p = new DiscoveryProgressDialog(getActivity());
        app.findDictionaries(new DictionaryDiscoveryCallback() {
            @Override
            public void onDiscoveryFinished() {
                p.dismiss();
            }
        });
        p.show();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (findDictionariesOnAttach) {
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
            Log.d(TAG, "Unknown request code: " + requestCode);
            return;
        }
        String selectedPath = data == null ? null : data.getStringExtra(FileSelectActivity.KEY_SELECTED_FILE_PATH);
        Log.d(TAG, String.format("req code %s, result code: %s, selected: %s", requestCode, resultCode, selectedPath));
        if (resultCode == AppCompatActivity.RESULT_OK && selectedPath != null && selectedPath.length() > 0) {
            final Application app = ((Application) getActivity().getApplication());
            boolean alreadyExists = app.addDictionary(new File(selectedPath));
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
