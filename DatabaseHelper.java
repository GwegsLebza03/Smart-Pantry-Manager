package com.lebogangramabu.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper is responsible for creating and upgrading the SQLite database.
 *
 * It follows the SQLiteOpenHelper approach covered in the module guide:
 * - onCreate creates the tables.
 * - onUpgrade changes the database when its version increases.
 * - seedRecipes adds the starter recipes only once.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    private static DatabaseHelper instance;

    /**
     * Singleton pattern means the app reuses one helper object instead of
     * creating many database helpers unnecessarily.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Foreign keys keep recipe_ingredients connected to existing recipes.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table storing the user's own pantry records.
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ingredient_name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT" +
                ");");

        // Table storing the recipe name and the method.
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL" +
                ");");

        // Table connecting each recipe to its required ingredients.
        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES " + TABLE_RECIPES + "(id) ON DELETE CASCADE" +
                ");");

        // The app starts with an empty pantry but a ready-made recipe collection.
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future database changes would be added here using the version number.
        // We leave version 1 intact so existing user data is not accidentally lost.
    }

    /**
     * Adds 20 recipes the first time the database is created.
     */
    private void seedRecipes(SQLiteDatabase db) {
        List<SeedRecipe> recipes = new ArrayList<>();

        recipes.add(new SeedRecipe(1, "Chicken and Rice",
                "1. Season the chicken.\n2. Cook the chicken until done.\n3. Cook the rice.\n4. Fry the onion and combine everything.\n5. Serve hot.",
                new String[][]{{"chicken", "500", "g"}, {"rice", "200", "g"}, {"onion", "1", "piece"}, {"cooking oil", "15", "ml"}, {"salt", "5", "g"}}));

        recipes.add(new SeedRecipe(2, "Tomato Pasta",
                "1. Boil the pasta.\n2. Fry onion and garlic in oil.\n3. Add tomatoes and cook into a simple sauce.\n4. Toss with pasta and serve.",
                new String[][]{{"pasta", "200", "g"}, {"tomato", "3", "piece"}, {"onion", "1", "piece"}, {"garlic", "2", "clove"}, {"cooking oil", "15", "ml"}}));

        recipes.add(new SeedRecipe(3, "Vegetable Omelette",
                "1. Beat the eggs.\n2. Chop onion and tomato.\n3. Cook the vegetables briefly.\n4. Add the eggs and cheese.\n5. Fold and serve.",
                new String[][]{{"egg", "3", "piece"}, {"onion", "1", "piece"}, {"tomato", "2", "piece"}, {"cheese", "50", "g"}}));

        recipes.add(new SeedRecipe(4, "Tuna Salad",
                "1. Chop lettuce, tomato and cucumber.\n2. Add tuna.\n3. Mix with mayonnaise.\n4. Serve chilled.",
                new String[][]{{"tuna", "1", "can"}, {"lettuce", "100", "g"}, {"tomato", "2", "piece"}, {"cucumber", "1", "piece"}, {"mayonnaise", "30", "ml"}}));

        recipes.add(new SeedRecipe(5, "Pancakes",
                "1. Mix flour, milk, egg and sugar into a smooth batter.\n2. Heat a pan.\n3. Pour small amounts of batter.\n4. Cook both sides and serve.",
                new String[][]{{"flour", "200", "g"}, {"milk", "250", "ml"}, {"egg", "2", "piece"}, {"sugar", "30", "g"}}));

        recipes.add(new SeedRecipe(6, "Grilled Cheese Sandwich",
                "1. Butter the bread.\n2. Add cheese between two slices.\n3. Grill or pan-fry until golden and the cheese melts.",
                new String[][]{{"bread", "4", "slice"}, {"cheese", "100", "g"}, {"butter", "20", "g"}}));

        recipes.add(new SeedRecipe(7, "Beef Stir Fry",
                "1. Slice the beef and vegetables.\n2. Stir-fry the beef.\n3. Add onion, pepper and carrot.\n4. Add soy sauce and cook until tender.",
                new String[][]{{"beef", "400", "g"}, {"pepper", "1", "piece"}, {"onion", "1", "piece"}, {"carrot", "1", "piece"}, {"soy sauce", "30", "ml"}}));

        recipes.add(new SeedRecipe(8, "Rice and Beans",
                "1. Cook the rice.\n2. Fry onion and tomato.\n3. Add beans and heat through.\n4. Serve with rice.",
                new String[][]{{"rice", "200", "g"}, {"beans", "1", "can"}, {"onion", "1", "piece"}, {"tomato", "2", "piece"}}));

        recipes.add(new SeedRecipe(9, "Chicken Wrap",
                "1. Cook and slice the chicken.\n2. Warm the tortillas.\n3. Add chicken, lettuce, tomato and mayonnaise.\n4. Wrap tightly and serve.",
                new String[][]{{"chicken", "300", "g"}, {"tortilla", "4", "piece"}, {"lettuce", "80", "g"}, {"tomato", "2", "piece"}, {"mayonnaise", "30", "ml"}}));

        recipes.add(new SeedRecipe(10, "French Toast",
                "1. Whisk egg, milk and sugar.\n2. Dip bread in the mixture.\n3. Fry both sides until golden.\n4. Serve warm.",
                new String[][]{{"bread", "4", "slice"}, {"egg", "2", "piece"}, {"milk", "100", "ml"}, {"sugar", "15", "g"}}));

        recipes.add(new SeedRecipe(11, "Simple Spaghetti Bolognese",
                "1. Brown the mince.\n2. Add chopped tomato and onion.\n3. Simmer the sauce.\n4. Cook spaghetti separately.\n5. Combine and serve.",
                new String[][]{{"spaghetti", "200", "g"}, {"beef mince", "400", "g"}, {"tomato", "4", "piece"}, {"onion", "1", "piece"}}));

        recipes.add(new SeedRecipe(12, "Potato Omelette",
                "1. Slice the potato and onion.\n2. Cook until tender.\n3. Beat the eggs and pour over the vegetables.\n4. Cook until set.",
                new String[][]{{"potato", "3", "piece"}, {"egg", "4", "piece"}, {"onion", "1", "piece"}, {"cooking oil", "15", "ml"}}));

        recipes.add(new SeedRecipe(13, "Tomato Soup",
                "1. Fry onion and garlic.\n2. Add tomatoes and water.\n3. Add the stock cube.\n4. Simmer and blend or mash until smooth.",
                new String[][]{{"tomato", "5", "piece"}, {"onion", "1", "piece"}, {"garlic", "2", "clove"}, {"water", "500", "ml"}, {"stock cube", "1", "piece"}}));

        recipes.add(new SeedRecipe(14, "Chicken Fried Rice",
                "1. Cook the chicken.\n2. Fry the egg.\n3. Add rice and peas.\n4. Add soy sauce and chicken.\n5. Stir-fry until hot.",
                new String[][]{{"chicken", "300", "g"}, {"rice", "300", "g"}, {"egg", "2", "piece"}, {"peas", "100", "g"}, {"soy sauce", "30", "ml"}}));

        recipes.add(new SeedRecipe(15, "Banana Pancakes",
                "1. Mash the banana.\n2. Mix with flour, milk and egg.\n3. Fry small pancakes on a heated pan.\n4. Serve warm.",
                new String[][]{{"banana", "2", "piece"}, {"flour", "150", "g"}, {"milk", "200", "ml"}, {"egg", "1", "piece"}}));

        recipes.add(new SeedRecipe(16, "Egg Fried Rice",
                "1. Fry the egg.\n2. Add cooked rice and peas.\n3. Add soy sauce.\n4. Stir-fry until everything is hot.",
                new String[][]{{"rice", "250", "g"}, {"egg", "2", "piece"}, {"peas", "100", "g"}, {"soy sauce", "20", "ml"}}));

        recipes.add(new SeedRecipe(17, "Peanut Butter Toast",
                "1. Toast the bread.\n2. Spread peanut butter.\n3. Slice banana over the top.\n4. Serve.",
                new String[][]{{"bread", "2", "slice"}, {"peanut butter", "40", "g"}, {"banana", "1", "piece"}}));

        recipes.add(new SeedRecipe(18, "Greek Salad",
                "1. Chop cucumber and tomato.\n2. Add feta and olives.\n3. Toss gently and serve.",
                new String[][]{{"cucumber", "1", "piece"}, {"tomato", "3", "piece"}, {"feta", "100", "g"}, {"olives", "50", "g"}}));

        recipes.add(new SeedRecipe(19, "Creamy Chicken Pasta",
                "1. Cook pasta.\n2. Cook chicken until done.\n3. Add cream and garlic.\n4. Stir in cheese.\n5. Mix with pasta and serve.",
                new String[][]{{"chicken", "300", "g"}, {"pasta", "200", "g"}, {"cream", "100", "ml"}, {"garlic", "2", "clove"}, {"cheese", "50", "g"}}));

        recipes.add(new SeedRecipe(20, "Vegetable Rice Bowl",
                "1. Cook the rice.\n2. Cook carrot, peas and corn until tender.\n3. Add soy sauce.\n4. Serve the vegetables over rice.",
                new String[][]{{"rice", "250", "g"}, {"carrot", "1", "piece"}, {"peas", "100", "g"}, {"corn", "100", "g"}, {"soy sauce", "30", "ml"}}));

        for (SeedRecipe recipe : recipes) {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("id", recipe.id);
            recipeValues.put("name", recipe.name);
            recipeValues.put("instructions", recipe.instructions);
            db.insert(TABLE_RECIPES, null, recipeValues);

            for (String[] ingredient : recipe.ingredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put("recipe_id", recipe.id);
                ingredientValues.put("ingredient_name", ingredient[0]);
                ingredientValues.put("required_quantity", Double.parseDouble(ingredient[1]));
                ingredientValues.put("unit", ingredient[2]);
                db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
            }
        }
    }

    private static class SeedRecipe {
        final int id;
        final String name;
        final String instructions;
        final String[][] ingredients;

        SeedRecipe(int id, String name, String instructions, String[][] ingredients) {
            this.id = id;
            this.name = name;
            this.instructions = instructions;
            this.ingredients = ingredients;
        }
    }

    // Small database utility methods used by the repository classes.
    public long insert(String table, ContentValues values) {
        return getWritableDatabase().insert(table, null, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update(table, values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(table, whereClause, whereArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                        String orderBy) {
        return getReadableDatabase().query(table, columns, selection, selectionArgs,
                null, null, orderBy);
    }
}
