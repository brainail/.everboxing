package itkach.aard2.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.BaseActivity;

import itkach.aard2.ui.fragments.FileSelectFragment;

public class FileSelectActivity extends BaseActivity {

    public final static String KEY_SELECTED_FILE_PATH = "selectedFilePath";
    public static final String PREF = "fileSelect";

    @Override
    protected Integer getLayoutResourceId () {
        return R.layout.activity_files;
    }

    @Override
    protected Integer getPrimaryToolbarLayoutResourceId () {
        return R.id.toolbar_primary;
    }

    private void initFilesBox () {
        if (null == getSupportFragmentManager ().findFragmentByTag (FileSelectFragment.MANAGER_TAG)) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
            fragmentTransaction.replace (R.id.base_fragment_container, new FileSelectFragment (), FileSelectFragment.MANAGER_TAG).commit ();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        initFilesBox ();
    }

}
