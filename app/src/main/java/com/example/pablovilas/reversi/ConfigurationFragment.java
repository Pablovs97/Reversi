package com.example.pablovilas.reversi;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ConfigurationFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
