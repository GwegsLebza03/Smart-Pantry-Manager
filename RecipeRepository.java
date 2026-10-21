package com.lebogangramabu.smartpantry.data;

import android.content.Context;
import android.database.Cursor;

import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * RecipeRepository reads the seeded recipe collection from SQLite.
 */
public class RecipeRepository {

    private final DatabaseHelper dbHelper;

    public RecipeRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Reads every recipe and attaches its ingredient rows to the Recipe object.
     */
    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();

        Cursor recipeCursor = dbHelper.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                null,
                null,
                "name COLLATE NOCASE ASC"
        );

        try {
            while (recipeCursor.moveToNext()) {
                int recipeId = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow("id"));
                String name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("name"));
                String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("instructions"));

                Recipe recipe = new Recipe(recipeId, name, instructions);
                addIngredientsToRecipe(recipe);
                recipes.add(recipe);
            }
        } finally {
            recipeCursor.close();
        }

        return recipes;
    }

    /** READ ONE - returns one recipe for the detail screen. */
    public Recipe getRecipeById(int id) {
        Cursor cursor = dbHelper.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null
        );

        try {
            if (cursor.moveToFirst()) {
                Recipe recipe = new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                );
                addIngredientsToRecipe(recipe);
                return recipe;
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    private void addIngredientsToRecipe(Recipe recipe) {
        Cursor cursor = dbHelper.query(
                DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipe.getId())},
                "id ASC"
        );

        try {
            while (cursor.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("recipe_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("required_quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        } finally {
            cursor.close();
        }
    }
}
