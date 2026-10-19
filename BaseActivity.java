package com.lebogangramabu.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * BaseActivity contains the shared bottom navigation used by the main screens.
 * This avoids copying the same navigation code into every Activity.
 */
public abstract class BaseActivity extends Activity {

    /**
     * Connects the three navigation buttons to their Activities.
     * selectedId is highlighted by the XML background and disabled while active.
     */
    protected void setupBottomNavigation(int selectedId) {
        Button pantryButton = findViewById(R.id.nav_pantry);
        Button recipesButton = findViewById(R.id.nav_recipes);
        Button settingsButton = findViewById(R.id.nav_settings);

        if (pantryButton == null || recipesButton == null || settingsButton == null) {
            return;
        }

        pantryButton.setOnClickListener(v -> openScreen(MainActivity.class));
        recipesButton.setOnClickListener(v -> openScreen(SuggestedRecipesActivity.class));
        settingsButton.setOnClickListener(v -> openScreen(SettingsActivity.class));

        View selected = findViewById(selectedId);
        if (selected != null) {
            selected.setEnabled(false);
            selected.setAlpha(0.55f);
        }
    }

    /**
     * Starts another Activity using an explicit Intent.
     */
    private void openScreen(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
