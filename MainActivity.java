package com.lebogangramabu.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lebogangramabu.smartpantry.adapter.PantryAdapter;
import com.lebogangramabu.smartpantry.data.PantryRepository;
import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity is the Pantry List screen.
 *
 * This screen demonstrates the READ part of CRUD and connects the database
 * data to a custom ListView Adapter.
 */
public class MainActivity extends BaseActivity {

    private PantryRepository pantryRepository;
    private PantryAdapter pantryAdapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private ListView pantryListView;
    private TextView emptyMessage;
    private TextView expiryAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the XML layout for the pantry screen.
        setContentView(R.layout.activity_main);

        // Connect Java variables to the XML controls.
        pantryListView = findViewById(R.id.pantryListView);
        emptyMessage = findViewById(R.id.tvPantryEmpty);
        expiryAlert = findViewById(R.id.tvExpiryAlert);
        Button addButton = findViewById(R.id.btnAddIngredient);

        // Create the repository that talks to SQLite.
        pantryRepository = new PantryRepository(this);

        // Create our custom Adapter and connect it to the ListView.
        pantryAdapter = new PantryAdapter(
                this,
                pantryItems,
                pantryRepository,
                this::loadPantry
        );
        pantryListView.setAdapter(pantryAdapter);

        // The Add button opens the Add/Edit screen using an Intent.
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        // Set up Pantry / Recipes / Settings bottom navigation.
        setupBottomNavigation(R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onResume runs whenever this screen becomes active again.
        // Reloading here makes new or edited pantry records appear immediately.
        loadPantry();
    }

    /**
     * Reads current pantry records from SQLite and updates the ListView.
     */
    private void loadPantry() {
        if (pantryRepository == null) {
            return;
        }

        pantryItems.clear();
        pantryItems.addAll(pantryRepository.getAll());
        pantryAdapter.notifyDataSetChanged();

        boolean pantryIsEmpty = pantryItems.isEmpty();
        emptyMessage.setVisibility(pantryIsEmpty ? View.VISIBLE : View.GONE);
        pantryListView.setVisibility(pantryIsEmpty ? View.GONE : View.VISIBLE);

        updateExpiryAlert();
    }

    /**
     * Uses the setting saved by SettingsActivity to show expiry warnings.
     */
    private void updateExpiryAlert() {
        boolean alertsEnabled = getSharedPreferences(
                SettingsActivity.PREFS_NAME,
                MODE_PRIVATE
        ).getBoolean(SettingsActivity.PREF_EXPIRY_ALERTS, true);

        if (!alertsEnabled) {
            expiryAlert.setVisibility(View.GONE);
            return;
        }

        int alertCount = 0;
        for (PantryItem item : pantryItems) {
            if (DateUtils.isExpiringWithinDays(item.getExpiryDate(), 3)) {
                alertCount++;
            }
        }

        if (alertCount > 0) {
            expiryAlert.setText(alertCount + " pantry item(s) expire within 3 days.");
            expiryAlert.setVisibility(View.VISIBLE);
        } else {
            expiryAlert.setVisibility(View.GONE);
        }
    }
}
