package com.lebogangramabu.smartpantry.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import android.view.LayoutInflater;

import com.lebogangramabu.smartpantry.R;
import com.lebogangramabu.smartpantry.RecipeDetailActivity;
import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.model.Recipe;
import com.lebogangramabu.smartpantry.model.RecipeDisplayItem;
import com.lebogangramabu.smartpantry.model.RecipeIngredient;
import com.lebogangramabu.smartpantry.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Adapter for the Suggested & Potential Recipes ListView.
 */
public class RecipeAdapter extends BaseAdapter {

    private final Context context;
    private final List<RecipeDisplayItem> displayItems;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    public RecipeAdapter(Context context, List<RecipeDisplayItem> displayItems) {
        this.context = context;
        this.displayItems = displayItems;
    }

    public void updatePantry(List<PantryItem> pantry) {
        this.pantryItems.clear();
        if (pantry != null) {
            this.pantryItems.addAll(pantry);
        }
    }

    @Override
    public int getCount() {
        return displayItems.size();
    }

    @Override
    public RecipeDisplayItem getItem(int position) {
        return displayItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayItems.get(position).getRecipe().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        }

        RecipeDisplayItem item = getItem(position);
        Recipe recipe = item.getRecipe();

        TextView name = row.findViewById(R.id.tvRecipeName);
        TextView badge = row.findViewById(R.id.tvRecipeBadge);
        TextView subtitle = row.findViewById(R.id.tvRecipeSubtitle);

        name.setText(recipe.getName());

        if (item.isStrictMatch()) {
            badge.setText("SUGGESTED RECIPE");
            badge.setTextColor(context.getColor(R.color.badge_suggested_text));
            badge.setBackgroundColor(context.getColor(R.color.badge_suggested_bg));

            subtitle.setText("All " + recipe.getIngredients().size() + " ingredients available in your pantry.");
        } else {
            badge.setText("POTENTIAL RECIPE");
            badge.setTextColor(context.getColor(R.color.badge_potential_text));
            badge.setBackgroundColor(context.getColor(R.color.badge_potential_bg));

            subtitle.setText(buildPotentialSubtitle(recipe));
        }

        row.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            context.startActivity(intent);
        });

        return row;
    }

    /**
     * Builds detailed breakdown for potential recipes showing available vs needed quantity.
     * e.g., "• chicken: 200 g available (300 g needed)"
     */
    private String buildPotentialSubtitle(Recipe recipe) {
        StringBuilder builder = new StringBuilder();

        for (RecipeIngredient req : recipe.getIngredients()) {
            double available = IngredientMatcher.getAvailableAmount(req, pantryItems);
            double required = req.getRequiredQuantity();
            String unit = req.getUnit();

            String availStr = IngredientMatcher.formatQuantity(available);
            String reqStr = IngredientMatcher.formatQuantity(required);

            builder.append("• ")
                    .append(req.getIngredientName())
                    .append(": ")
                    .append(availStr)
                    .append(" ")
                    .append(unit)
                    .append(" available (")
                    .append(reqStr)
                    .append(" ")
                    .append(unit)
                    .append(" needed)\n");
        }

        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '\n') {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }
}
