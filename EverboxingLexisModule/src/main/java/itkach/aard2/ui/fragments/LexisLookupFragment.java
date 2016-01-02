package itkach.aard2.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.tool.ToolKeyboard;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import itkach.aard2.Application;
import itkach.aard2.callbacks.LookupListener;
import itkach.aard2.ui.activities.ArticleCollectionActivity;

public class LexisLookupFragment extends BaseListFragment implements LookupListener {

    private Timer mTimer;
    private TimerTask mScheduledLookup = null;

    private SearchView mSearchView;
    private String mInitialQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.app ().addLookupListener(this);
    }

    @Override
    protected boolean supportsSelection() {
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setBusy(false);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plogger.logI("Item clicked: " + position);
                Intent intent = new Intent(getActivity(), ArticleCollectionActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        getListView().setAdapter(Application.app ().lastResult);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        final InputMethodManager inputManager
                = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow (getView ().getWindowToken (), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lookup, menu);
        MenuItem miFilter = menu.findItem (R.id.action_lookup);

        mTimer = new Timer();

        mSearchView = (SearchView) MenuItemCompat.getActionView(miFilter);
        mSearchView.setIconified(false);

        mSearchView.setImeOptions(mSearchView.getImeOptions ()
                        | EditorInfo.IME_ACTION_SEARCH
                        | EditorInfo.IME_FLAG_NO_EXTRACT_UI
                        | EditorInfo.IME_FLAG_NO_FULLSCREEN
        );

        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });

        mSearchView.setSubmitButtonEnabled(false);
        if (mInitialQuery != null) {
            mSearchView.setQuery(mInitialQuery, true);
            mInitialQuery = null;
        }

        mSearchView.clearFocus();
    }

    private final SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener () {
        @Override
        public boolean onQueryTextSubmit (String query) {
            Plogger.logI ("SUBMIT -> " + query);
            onQueryTextChange (query);
            ToolKeyboard.hide ((AppCompatActivity) getActivity ());
            return true;
        }

        @Override
        public boolean onQueryTextChange (String newText) {
            Plogger.logI ("CHANGE -> New text: " + newText);
            TimerTask doLookup = new TimerTask () {
                @Override
                public void run () {
                    final String query = mSearchView.getQuery ().toString ();
                    if (Application.app ().getLookupQuery ().equals (query) || TextUtils.isEmpty (query)) {
                        return;
                    }
                    getActivity ().runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            Application.app ().lookup (query);
                        }
                    });
                    mScheduledLookup = null;
                }
            };

            final String query = mSearchView.getQuery ().toString ();
            if (!Application.app ().getLookupQuery ().equals (query)) {
                if (mScheduledLookup != null) {
                    mScheduledLookup.cancel ();
                }
                mScheduledLookup = doLookup;
                mTimer.schedule (doLookup, 600);
            }
            return true;
        }

    };

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra (RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList.isEmpty ()) {
                    final String searchQuery = textMatchList.get (0);
                    mOnQueryTextListener.onQueryTextSubmit (searchQuery);
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchView.setQuery(Application.app ().getLookupQuery(), true);
        if (Application.app ().lastResult.getCount() > 0) {
            mSearchView.clearFocus();
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSearchView != null) {
            String query = mSearchView.getQuery().toString();
            outState.putString("lookupQuery", query);
        }
    }

    private void setBusy(boolean busy) {
        setListShown(! busy);
        if (! busy) {
            TextView emptyText = ((TextView) mEmptyPlaceholderView.findViewById(R.id.empty_text));
            String msg = "";
            String query = Application.app ().getLookupQuery();
            if (query != null && ! query.equals("")) {
                msg = getString(R.string.lookup_nothing_found);
            }
            emptyText.setText(msg);
        }
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) mTimer.cancel();
        Application.app ().removeLookupListener (this);
        super.onDestroy();
    }

    @Override
    public void onLookupStarted(String query) {
        setBusy(true);
    }

    @Override
    public void onLookupFinished(String query) {
        setBusy(false);
    }

    @Override
    public void onLookupCanceled(String query) {
        setBusy(false);
    }

    void setQuery(String query) {
        if (mSearchView != null) {
            mSearchView.setQuery(query, true);
        } else {
            this.mInitialQuery = query;
        }
    }

}