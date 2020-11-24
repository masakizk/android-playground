package com.example.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class DialogPreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.dialog, rootKey)
    }
}