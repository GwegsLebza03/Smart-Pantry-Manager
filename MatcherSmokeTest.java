import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeIngredient;
import com.lebogangramabu.smartpantry.util.IngredientMatcher;

import java.util.Arrays;
import java.util.List;

public class MatcherSmokeTest {
    private static Recipe recipe() {
        Recipe r = new Recipe(1, "Chicken Rice", "test");
        r.addIngredient(new RecipeIngredient(1, "chicken", 500, "g"));
        r.addIngredient(new RecipeIngredient(1, "tomato", 2, "piece"));
        return r;
    }

    public static void main(String[] args) {
        Recipe r = recipe();

        List<PantryItem> good = Arrays.asList(
                new PantryItem("CHICKEN", 0.6, "kg", ""),
                new PantryItem("TOMATOES", 2, "pcs", "")
        );
        if (!IngredientMatcher.canMakeRecipe(r, good)) {
            throw new AssertionError("Good pantry should match recipe");
        }

        List<PantryItem> missing = Arrays.asList(
                new PantryItem("chicken", 500, "g", "")
        );
        if (IngredientMatcher.canMakeRecipe(r, missing)) {
            throw new AssertionError("Missing ingredient must reject recipe");
        }

        List<PantryItem> insufficient = Arrays.asList(
                new PantryItem("chicken", 450, "g", ""),
                new PantryItem("tomato", 2, "piece", "")
        );
        if (IngredientMatcher.canMakeRecipe(r, insufficient)) {
            throw new AssertionError("Insufficient quantity must reject recipe");
        }

        List<PantryItem> plural = Arrays.asList(
                new PantryItem("chickens", 500, "g", ""),
                new PantryItem("tomatoes", 2, "piece", "")
        );
        // "chickens" is intentionally normalised by the simple plural fallback.
        if (!IngredientMatcher.canMakeRecipe(r, plural)) {
            throw new AssertionError("Simple plural normalisation should match");
        }

        Recipe volume = new Recipe(2, "Milk Test", "test");
        volume.addIngredient(new RecipeIngredient(2, "milk", 250, "ml"));
        if (!IngredientMatcher.canMakeRecipe(volume,
                Arrays.asList(new PantryItem("milk", 0.25, "L", "")))) {
            throw new AssertionError("L to ml conversion should match");
        }

        System.out.println("ALL MATCHER TESTS PASSED");
    }
}
