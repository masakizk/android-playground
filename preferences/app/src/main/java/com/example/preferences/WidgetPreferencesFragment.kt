package com.example.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class WidgetPreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.widgets, rootKey)
    }
}