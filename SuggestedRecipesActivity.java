package com.lebogangramabu.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lebogangramabu.smartpantry.adapter.RecipeAdapter;
import com.lebogangramabu.smartpantry.data.PantryRepository;
import com.lebogangramabu.smartpantry.data.RecipeRepository;
import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeDisplayItem;
import com.lebogangramabu.smartpantry.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * SuggestedRecipesActivity shows suggested recipes (strict matches)
 * and up to 3 potential recipes (partial matches or example recipes).
 */
public class SuggestedRecipesActivity extends BaseActivity {

    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;

    private final List<RecipeDisplayItem> displayItems = new ArrayList<>();
    private RecipeAdapter recipeAdapter;
    private ListView recipeListView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recipeListView = findViewById(R.id.recipeListView);
        statusText = findViewById(R.id.tvRecipeStatus);

        pantryRepository = new PantryRepository(this);
        recipeRepository = new RecipeRepository(this);

        recipeAdapter = new RecipeAdapter(this, displayItems);
        recipeListView.setAdapter(recipeAdapter);

        setupBottomNavigation(R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /**
     * Reads pantry + recipes, finds strict matches and potential matches (up to 3).
     */
    private void loadSuggestions() {
        List<PantryItem> pantry = pantryRepository.getAll();
        List<Recipe> allRecipes = recipeRepository.getAllRecipesWithIngredients();

        List<Recipe> strictMatches = IngredientMatcher.getStrictMatches(pantry, allRecipes);
        List<Recipe> potentialMatches = IngredientMatcher.getPotentialMatches(pantry, allRecipes, strictMatches, 3);

        displayItems.clear();

        for (Recipe recipe : strictMatches) {
            displayItems.add(new RecipeDisplayItem(recipe, true));
        }

        for (Recipe recipe : potentialMatches) {
            displayItems.add(new RecipeDisplayItem(recipe, false));
        }

        recipeAdapter.updatePantry(pantry);
        recipeAdapter.notifyDataSetChanged();

        int strictCount = strictMatches.size();
        int potentialCount = potentialMatches.size();

        if (strictCount > 0 && potentialCount > 0) {
            statusText.setText(strictCount + " suggested recipe(s) ready to make, " + potentialCount + " potential recipe(s).");
        } else if (strictCount > 0) {
            statusText.setText(strictCount + " recipe(s) can be made right now.");
        } else if (potentialCount > 0) {
            statusText.setText("Showing " + potentialCount + " potential recipe(s) based on your pantry & example recipes.");
        } else {
            statusText.setText("No recipes available.");
        }

        recipeListView.setVisibility(displayItems.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
