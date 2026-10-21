package com.lebogangramabu.smartpantry.util;

import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * IngredientMatcher contains the core business rule of the assignment.
 *
 * A recipe is suggested ONLY when every required ingredient is available
 * in enough quantity. Missing one ingredient means the whole recipe fails.
 */
public class IngredientMatcher {

    /**
     * Returns only recipes that the user can make right now.
     */
    public static List<Recipe> getStrictMatches(List<PantryItem> pantry, List<Recipe> recipes) {
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (canMakeRecipe(recipe, pantry)) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    /**
     * Checks EVERY ingredient in one recipe.
     * If even one required ingredient fails, false is returned immediately.
     */
    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantry) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            double available = getAvailableAmount(required, pantry);

            if (available < required.getRequiredQuantity()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a recipe has at least one matching ingredient in the pantry.
     */
    public static boolean hasAnyIngredientInPantry(Recipe recipe, List<PantryItem> pantry) {
        if (pantry == null || pantry.isEmpty() || recipe == null) {
            return false;
        }

        for (RecipeIngredient required : recipe.getIngredients()) {
            if (getAvailableAmount(required, pantry) > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns potential matches (recipes with at least 1 ingredient in pantry,
     * or example recipes if pantry matches are fewer than limit).
     */
    public static List<Recipe> getPotentialMatches(List<PantryItem> pantry,
                                                   List<Recipe> recipes,
                                                   List<Recipe> strictMatches,
                                                   int limit) {
        List<Recipe> potentialList = new ArrayList<>();
        if (recipes == null || limit <= 0) {
            return potentialList;
        }

        // Pass 1: add recipes with at least 1 matching ingredient in pantry
        for (Recipe recipe : recipes) {
            if (strictMatches != null && strictMatches.contains(recipe)) {
                continue;
            }

            if (hasAnyIngredientInPantry(recipe, pantry)) {
                potentialList.add(recipe);
                if (potentialList.size() == limit) {
                    return potentialList;
                }
            }
        }

        // Pass 2: fill remaining slots with example recipes so at least limit recipes appear
        for (Recipe recipe : recipes) {
            if (strictMatches != null && strictMatches.contains(recipe)) {
                continue;
            }

            if (!potentialList.contains(recipe)) {
                potentialList.add(recipe);
                if (potentialList.size() == limit) {
                    return potentialList;
                }
            }
        }

        return potentialList;
    }

    /**
     * Formats doubles cleanly (e.g. 200.0 -> "200", 0.5 -> "0.5").
     */
    public static String formatQuantity(double val) {
        if (val == (long) val) {
            return String.format(Locale.ROOT, "%d", (long) val);
        } else {
            return String.format(Locale.ROOT, "%.1f", val);
        }
    }

    /**
     * Adds together all pantry rows that match the ingredient name and
     * have a compatible unit group.
     */
    public static double getAvailableAmount(RecipeIngredient required, List<PantryItem> pantry) {
        double total = 0;

        for (PantryItem item : pantry) {
            if (!normalizeName(item.getIngredientName()).equals(normalizeName(required.getIngredientName()))) {
                continue;
            }

            if (!areUnitsCompatible(item.getUnit(), required.getUnit())) {
                continue;
            }

            double converted = convert(item.getQuantity(), item.getUnit(), required.getUnit());
            total += converted;
        }

        return total;
    }

    /**
     * Normalises simple names such as "Tomato" and "tomatoes" so that
     * trivial singular/plural differences do not break recipe matching.
     */
    public static String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        String value = name.trim().toLowerCase(Locale.ROOT);
        value = value.replaceAll("[^a-z0-9 ]", "");
        value = value.replaceAll("\\s+", " ");

        Map<String, String> specialPlurals = new HashMap<>();
        specialPlurals.put("tomatoes", "tomato");
        specialPlurals.put("potatoes", "potato");
        specialPlurals.put("onions", "onion");
        specialPlurals.put("carrots", "carrot");
        specialPlurals.put("peppers", "pepper");
        specialPlurals.put("eggs", "egg");
        specialPlurals.put("bananas", "banana");
        specialPlurals.put("apples", "apple");
        specialPlurals.put("cloves", "clove");
        specialPlurals.put("slices", "slice");
        specialPlurals.put("tortillas", "tortilla");

        if (specialPlurals.containsKey(value)) {
            return specialPlurals.get(value);
        }

        // Simple fallback for ordinary plurals.
        if (value.endsWith("s") && value.length() > 3 && !value.endsWith("ss")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String value = unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "grams":
            case "gram":
            case "g":
                return "g";
            case "kilograms":
            case "kilogram":
            case "kg":
                return "kg";
            case "millilitres":
            case "milliliters":
            case "millilitre":
            case "milliliter":
            case "ml":
                return "ml";
            case "litres":
            case "liters":
            case "litre":
            case "liter":
            case "l":
                return "l";
            case "pieces":
            case "piece":
            case "pcs":
            case "pc":
            case "items":
            case "item":
            case "can":
            case "cans":
            case "clove":
            case "slice":
                return "count";
            default:
                return value;
        }
    }

    private static String unitGroup(String unit) {
        String normalized = normalizeUnit(unit);

        if (normalized.equals("g") || normalized.equals("kg")) {
            return "weight";
        }

        if (normalized.equals("ml") || normalized.equals("l")) {
            return "volume";
        }

        if (normalized.equals("count")) {
            return "count";
        }

        return "other";
    }

    private static boolean areUnitsCompatible(String pantryUnit, String requiredUnit) {
        return unitGroup(pantryUnit).equals(unitGroup(requiredUnit));
    }

    /**
     * Converts the pantry amount into the unit used by the recipe.
     */
    private static double convert(double quantity, String fromUnit, String toUnit) {
        String from = normalizeUnit(fromUnit);
        String to = normalizeUnit(toUnit);

        if (from.equals(to)) {
            return quantity;
        }

        if (unitGroup(from).equals("weight")) {
            double grams = from.equals("kg") ? quantity * 1000 : quantity;
            return to.equals("kg") ? grams / 1000 : grams;
        }

        if (unitGroup(from).equals("volume")) {
            double millilitres = from.equals("l") ? quantity * 1000 : quantity;
            return to.equals("l") ? millilitres / 1000 : millilitres;
        }

        // Count units such as pieces, slices, cloves and cans are treated as
        // countable items. No conversion is needed for the assignment scope.
        return quantity;
    }
}
