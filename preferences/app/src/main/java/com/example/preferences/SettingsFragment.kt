package com.example.preferences

import android.os.Bundle
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference

class SettingsFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val key = getString(R.string.saved_boolean_value)
        val checkbox = findPreference<CheckBoxPreference>(key)
        checkbox?.onPreferenceChangeListener = this
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

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        Toast.makeText(
            requireContext(),
            "[onPreferenceChange] \nkey:${preference.key} \nvalue:$newValue",
            Toast.LENGTH_LONG
        ).show()
        return true
    }
}