package br.com.tonholosolutions.clattermapping.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import br.com.tonholosolutions.clattermapping.R;

/**
 * Created on 08/12/2015.
 *
 * @author rafaeltonholo
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
