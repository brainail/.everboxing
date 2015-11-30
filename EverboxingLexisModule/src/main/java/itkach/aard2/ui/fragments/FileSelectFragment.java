package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;

import java.io.File;

import itkach.aard2.ui.activities.FileSelectActivity;
import itkach.aard2.ui.adapters.FileSelectListAdapter;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class FileSelectFragment extends ListFragment {

    public static final String MANAGER_TAG = "org.brainail.Everboxing.tag#files.fragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu (true);

        final FileSelectListAdapter adapter = new FileSelectListAdapter();
        adapter.registerDataSetObserver(new DataSetObserver () {
            @Override
            public void onChanged() {
                File root = adapter.getRoot();
                String path;
                if (root != null) {
                    path = root.getAbsolutePath();
                } else {
                    path = "";
                }
                ((BaseActivity) getActivity ()).getPrimaryToolbar ().setSubtitle(path);
                savePath(path);
            }
        });
        setListAdapter(adapter);
        setPath (savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_file_manager, menu);
        super.onCreateOptionsMenu (menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        MenuItem miParentDir = menu.findItem(R.id.action_goto_parent_dir);
        miParentDir.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_chevron_left));
        MenuItem miReloadDir = menu.findItem(R.id.action_reload_directory);
        miReloadDir.setIcon(BaseIcon.barIcon (getActivity (), Iconify.IconValue.zmdi_refresh));
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        File root = adapter.getRoot();
        File parent = root.getParentFile();
        miParentDir.setEnabled (parent != null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
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
            getActivity ().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity ().getMenuInflater().inflate(R.menu.menu_file_manager, menu);
//        return true;
//    }

    private SharedPreferences prefs() {
        return getActivity ().getSharedPreferences (FileSelectActivity.PREF, AppCompatActivity.MODE_PRIVATE);
    }

    private void savePath(String path) {
        SharedPreferences p = prefs();
        SharedPreferences.Editor edit = p.edit();
        edit.putString(FileSelectActivity.KEY_SELECTED_FILE_PATH, path);
        edit.apply();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        if (adapter != null) {
            File root = adapter.getRoot();
            if (root != null) {
                outState.putString(FileSelectActivity.KEY_SELECTED_FILE_PATH, root.getAbsolutePath());
            }
        }
    }

    private void setPath(Bundle state) {
        FileSelectListAdapter adapter = (FileSelectListAdapter) getListAdapter();
        if (adapter != null) {
            String path = state == null ? null : state.getString(FileSelectActivity.KEY_SELECTED_FILE_PATH);
            File root = null;
            if (path != null && path.length() > 0) {
                root = new File(path);
            } else {
                SharedPreferences p = prefs();
                path = p.getString(FileSelectActivity.KEY_SELECTED_FILE_PATH, null);
                if (path != null) {
                    root = new File(path);
                }
            }
            if (root == null || !root.exists()) {
                root = Environment.getExternalStorageDirectory ();
            }
            adapter.setRoot(root);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileSelectListAdapter adapter = (FileSelectListAdapter) l.getAdapter();
        File f = (File) adapter.getItem (position);
        if (f.isDirectory()) {
            adapter.setRoot(f);
        } else {
            Intent data = new Intent();
            data.putExtra (FileSelectActivity.KEY_SELECTED_FILE_PATH, f.getAbsolutePath ());
            getActivity ().setResult (Activity.RESULT_OK, data);
            getActivity ().finish ();
        }
    }

}
