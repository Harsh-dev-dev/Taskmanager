package com.example.taskmanagerapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment


class SettingsFragment : Fragment() {

    private lateinit var themeSelection: RadioGroup
    private lateinit var notificationsSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val THEME_PREF = "theme_preference"
        const val LIGHT_THEME = "Light"
        const val DARK_THEME = "Dark"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        themeSelection = view.findViewById(R.id.themeSelection)
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)

        // Set default theme selection
        val selectedTheme = sharedPreferences.getString(THEME_PREF, LIGHT_THEME)
        if (selectedTheme == LIGHT_THEME) {
            themeSelection.check(R.id.lightTheme)
        } else {
            themeSelection.check(R.id.darkTheme)
        }

        // Handle theme selection
        themeSelection.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.lightTheme -> applyTheme(LIGHT_THEME)
                R.id.darkTheme -> applyTheme(DARK_THEME)
            }
        }

        // Handle notification switch
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle notification preferences
        }

        return view
    }

    private fun applyTheme(theme: String) {
        // Save selected theme to SharedPreferences
        sharedPreferences.edit().putString(THEME_PREF, theme).apply()

        // Apply theme changes
        when (theme) {
            LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}