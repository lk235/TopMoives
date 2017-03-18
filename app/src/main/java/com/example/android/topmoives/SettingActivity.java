package com.example.android.topmoives;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by lk235 on 2017/3/15.
 */

public class SettingActivity extends PreferenceActivity
implements Preference.OnPreferenceChangeListener{

    public static final String KEY_PREF_SORT_TYPE = "Sort Type";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(),""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference,Object value){
        String stringValue = value.toString();
        ListPreference listPreference = (ListPreference)preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);
        preference.setSummary(listPreference.getEntries()[prefIndex]);

        return true;
    }





}
