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
import com.lebogangramabu.smartpantry.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * SuggestedRecipesActivity runs the assignment's core strict-matching rule.
 */
public class SuggestedRecipesActivity extends BaseActivity {

    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;

    private final List<Recipe> matchingRecipes = new ArrayList<>();
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

        recipeAdapter = new RecipeAdapter(this, matchingRecipes);
        recipeListView.setAdapter(recipeAdapter);

        setupBottomNavigation(R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /**
     * Reads pantry + recipes and asks IngredientMatcher which recipes qualify.
     */
    private void loadSuggestions() {
        List<PantryItem> pantry = pantryRepository.getAll();
        List<Recipe> allRecipes = recipeRepository.getAllRecipesWithIngredients();

        matchingRecipes.clear();
        matchingRecipes.addAll(IngredientMatcher.getStrictMatches(pantry, allRecipes));
        recipeAdapter.notifyDataSetChanged();

        if (matchingRecipes.isEmpty()) {
            statusText.setText("No recipes match your pantry yet. Add more ingredients to unlock recipes.");
            recipeListView.setVisibility(View.GONE);
        } else {
            statusText.setText(matchingRecipes.size() + " recipe(s) can be made right now.");
            recipeListView.setVisibility(View.VISIBLE);
        }
    }
}
