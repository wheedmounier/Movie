package com.example.waheed.movie_app;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by waheed on 4/12/2016.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);

    }
}
