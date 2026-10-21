package com.lebogangramabu.smartpantry.model;

/**
 * RecipeIngredient represents one ingredient required by a recipe.
 */
public class RecipeIngredient {

    private int id;
    private int recipeId;
    private String ingredientName;
    private double requiredQuantity;
    private String unit;

    public RecipeIngredient(int id, int recipeId, String ingredientName,
                            double requiredQuantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public RecipeIngredient(int recipeId, String ingredientName,
                            double requiredQuantity, String unit) {
        this(0, recipeId, ingredientName, requiredQuantity, unit);
    }

    public int getId() {
        return id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }
}
