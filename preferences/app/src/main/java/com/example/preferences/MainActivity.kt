package com.example.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.preferences.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, SettingsFragment())
            .commit()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        loadPreferences()
    }

    /**
     * ライフサイクルを適切に管理するために、リスナーの登録と解除を
     * onResume(), onPause()コールバックで行う
     */
    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun loadPreferences() {
        val key = getString(R.string.saved_boolean_value)
        val savedValue = sharedPreferences.getBoolean(key, true)

        Toast.makeText(this, "key: $key, value:$savedValue", Toast.LENGTH_LONG)
            .show()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val savedKey = getString(R.string.saved_boolean_value)
        val savedValue = sharedPreferences.getBoolean(savedKey, true)
        Toast.makeText(
            this,
            "[onSharedPreferenceChanged]\n key: saved_boolean_value \nvalue:${savedValue}",
            Toast.LENGTH_LONG
        ).show()
    }
}