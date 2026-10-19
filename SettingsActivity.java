package com.lebogangramabu.smartpantry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lebogangramabu.smartpantry.util.MessageUtils;

/**
 * SettingsActivity stores simple user preferences using SharedPreferences.
 *
 * SharedPreferences are suitable for small settings such as an on/off toggle.
 */
public class SettingsActivity extends BaseActivity {

    public static final String PREFS_NAME = "smart_pantry_preferences";
    public static final String PREF_EXPIRY_ALERTS = "expiry_alerts";
    public static final String PREF_UNIT = "preferred_unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch expirySwitch = findViewById(R.id.switchExpiryAlerts);
        Spinner unitSpinner = findViewById(R.id.spinnerPreferredUnit);
        TextView message = findViewById(R.id.tvSettingsMessage);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load previously saved settings. Defaults are used on first launch.
        expirySwitch.setChecked(preferences.getBoolean(PREF_EXPIRY_ALERTS, true));

        String[] unitChoices = {"Metric (g / kg / ml / L)", "Count (pieces / cans)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                unitChoices
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        String savedUnit = preferences.getString(PREF_UNIT, unitChoices[0]);
        for (int i = 0; i < unitChoices.length; i++) {
            if (unitChoices[i].equals(savedUnit)) {
                unitSpinner.setSelection(i);
                break;
            }
        }

        // Every toggle change is immediately saved.
        expirySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(PREF_EXPIRY_ALERTS, isChecked).apply();
            MessageUtils.showSuccess(message, "Expiry alert setting saved.");
        });

        // Spinner selection is also saved immediately.
        unitSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view,
                                        int position, long id) {
                preferences.edit().putString(PREF_UNIT, unitChoices[position]).apply();
                MessageUtils.showSuccess(message, "Preferred unit setting saved.");
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // No action is needed when nothing is selected.
            }
        });

        setupBottomNavigation(R.id.nav_settings);
    }
}
