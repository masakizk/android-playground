package com.example.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class AdvancedPreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.advanced, rootKey)
    }
}