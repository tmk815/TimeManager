package com.example.tomoki.timemanager;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by tomoki on 2017/05/17.
 */

public class SettingsPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Setting");
    }*/

}