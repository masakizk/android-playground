package com.example.preferences

import android.os.Bundle
import androidx.preference.Preference

class SettingsFragment : BasePreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.dialogs) -> loadFragment(DialogPreferencesFragment())
            getString(R.string.widgets) -> loadFragment(WidgetPreferencesFragment())
            getString(R.string.advanced_attributes) -> loadFragment(AdvancedPreferencesFragment())
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }
}