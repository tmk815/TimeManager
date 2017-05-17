package com.example.tomoki.timemanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by tomoki on 2017/05/17.
 */

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content,
                new SettingsPreferenceFragment());
        fragmentTransaction.commit();
    }
}