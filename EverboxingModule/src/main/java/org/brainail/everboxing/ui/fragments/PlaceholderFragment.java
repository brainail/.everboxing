package org.brainail.Everboxing.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlaceholderFragment extends Fragment {

    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=null;//inflater.inflate(R.layout.fragment_test, container, false);
        TextView textView = null;//(TextView) rootView.findViewById(R.id.section_label);
        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // ((HomeActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
