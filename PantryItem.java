package com.lebogangramabu.smartpantry.model;

/**
 * PantryItem represents one ingredient stored by the user.
 *
 * Think of this class as a small "box" that carries one pantry record
 * between the Activity, Adapter and SQLite database code.
 */
public class PantryItem {

    private int id;
    private String ingredientName;
    private double quantity;
    private String unit;
    private String expiryDate;

    public PantryItem() {
    }

    public PantryItem(int id, String ingredientName, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public PantryItem(String ingredientName, double quantity, String unit, String expiryDate) {
        this(0, ingredientName, quantity, unit, expiryDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
