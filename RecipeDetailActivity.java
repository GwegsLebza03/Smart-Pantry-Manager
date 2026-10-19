package com.lebogangramabu.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.lebogangramabu.smartpantry.data.RecipeRepository;
import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeIngredient;

/**
 * RecipeDetailActivity displays all ingredients and the preparation method.
 */
public class RecipeDetailActivity extends Activity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Button backButton = findViewById(R.id.btnBackFromRecipe);
        TextView nameText = findViewById(R.id.tvRecipeDetailName);
        TextView ingredientsText = findViewById(R.id.tvRecipeIngredients);
        TextView instructionsText = findViewById(R.id.tvRecipeInstructions);

        backButton.setOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        RecipeRepository repository = new RecipeRepository(this);
        Recipe recipe = repository.getRecipeById(recipeId);

        if (recipe == null) {
            nameText.setText("Recipe not found");
            ingredientsText.setText("The selected recipe could not be loaded.");
            instructionsText.setText("");
            return;
        }

        nameText.setText(recipe.getName());
        ingredientsText.setText(buildIngredientText(recipe));
        instructionsText.setText(recipe.getInstructions());
    }

    /** Converts recipe ingredient objects into readable text for the user. */
    private String buildIngredientText(Recipe recipe) {
        StringBuilder builder = new StringBuilder();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            builder.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" — ")
                    .append(ingredient.getRequiredQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        return builder.toString();
    }
}
