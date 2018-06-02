package com.example.pablovilas.reversi.activities.configuration;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.pablovilas.reversi.R;

public class ConfigurationFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
