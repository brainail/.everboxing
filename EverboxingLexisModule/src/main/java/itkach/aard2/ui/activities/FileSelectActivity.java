package itkach.aard2.ui.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;

import java.io.File;

import itkach.aard2.Application;
import itkach.aard2.ui.adapters.FileSelectListAdapter;

public class FileSelectActivity extends ListActivity {

    public final static String KEY_SELECTED_FILE_PATH = "selectedFilePath";
    public static final String PREF = "fileSelect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Application app = (Application) getApplication();
        // final ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);

        final FileSelectListAdapter adapter = new FileSelectListAdapter();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                File root = adapter.getRoot();
                String path;
                if (root != null) {
                    path = root.getAbsolutePath();
                } else {
                    path = "";
                }
                // actionBar.setSubtitle(path);
                savePath(path);
            }
        });
        setListAdapter(adapter);
        setPath(savedInstanceState);
    }

    private SharedPreferences prefs() {
        return getSharedPreferences(PREF, AppCompatActivity.MODE_PRIVATE);
    }

    private void savePath(String path) {
        SharedPreferences p = prefs();
        SharedPreferences.Editor edit = p.edit();
        edit.putString(KEY_SELECTED_FILE_PATH, path);
        edit.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        if (adapter != null) {
            File root = adapter.getRoot();
            if (root != null) {
                outState.putString(KEY_SELECTED_FILE_PATH, root.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        setPath(state);
    }

    private void setPath(Bundle state) {
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        if (adapter != null) {
            String path = state == null ? null : state.getString(KEY_SELECTED_FILE_PATH);
            File root = null;
            if (path != null && path.length() > 0) {
                root = new File(path);
            } else {
                SharedPreferences p = prefs();
                path = p.getString(KEY_SELECTED_FILE_PATH, null);
                if (path != null) {
                    root = new File(path);
                }
            }
            if (root == null || !root.exists()) {
                root = Environment.getExternalStorageDirectory();
            }
            adapter.setRoot(root);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_manager, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem miParentDir = menu.findItem(R.id.action_goto_parent_dir);
        miParentDir.setIcon(BaseIcon.barIcon (this, Iconify.IconValue.md_chevron_left));
        MenuItem miReloadDir = menu.findItem(R.id.action_reload_directory);
        miReloadDir.setIcon(BaseIcon.barIcon (this, Iconify.IconValue.md_refresh));
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        File root = adapter.getRoot();
        File parent = root.getParentFile();
        miParentDir.setEnabled(parent != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        if (id == R.id.action_goto_parent_dir) {
            File root = adapter.getRoot();
            File parent = root.getParentFile();
            if (parent != null) {
                adapter.setRoot(parent);
            }
            return true;
        }
        if (id == R.id.action_reload_directory) {
            adapter.reload();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        FileSelectListAdapter adapter = (FileSelectListAdapter) l.getAdapter();
        File f = (File) adapter.getItem(position);
        if (f.isDirectory()) {
            adapter.setRoot(f);
        } else {
            Intent data = new Intent();
            data.putExtra(KEY_SELECTED_FILE_PATH, f.getAbsolutePath());
            setResult(RESULT_OK, data);
            finish();
        }
    }

}
