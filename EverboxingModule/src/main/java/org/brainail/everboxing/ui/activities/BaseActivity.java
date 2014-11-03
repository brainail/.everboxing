package org.brainail.Everboxing.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.brainail.Everboxing.R;

public class BaseActivity extends ActionBarActivity {

    protected Toolbar mPrimaryToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initContent();
        initToolbar();
    }

    private void initContent() {
        final Integer resourceId = getLayoutResourceId();
        if (null != resourceId) {
            setContentView(resourceId);
        }
    }

    private void initToolbar() {
        mPrimaryToolbar = (Toolbar) findViewById(R.id.toolbar_primary);
        if (null != mPrimaryToolbar) {
            setSupportActionBar(mPrimaryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    protected Toolbar getPrimaryToolbar() {
        return mPrimaryToolbar;
    }

    protected Integer getLayoutResourceId() {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
