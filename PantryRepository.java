package com.lebogangramabu.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lebogangramabu.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * PantryRepository contains the actual CRUD operations for pantry items.
 * Keeping database calls here makes the Activities easier to understand.
 */
public class PantryRepository {

    private final DatabaseHelper dbHelper;

    public PantryRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /** CREATE - adds a new pantry record. */
    public long add(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("ingredient_name", item.getIngredientName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());
        return dbHelper.insert(DatabaseHelper.TABLE_PANTRY, values);
    }

    /** READ - returns all pantry records. */
    public List<PantryItem> getAll() {
        List<PantryItem> pantryItems = new ArrayList<>();

        Cursor cursor = dbHelper.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                null,
                null,
                "ingredient_name COLLATE NOCASE ASC"
        );

        try {
            while (cursor.moveToNext()) {
                pantryItems.add(new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        } finally {
            cursor.close();
        }

        return pantryItems;
    }

    /** READ ONE - finds one pantry record by ID for editing. */
    public PantryItem getById(int id) {
        Cursor cursor = dbHelper.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                );
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    /** UPDATE - changes an existing pantry record. */
    public int update(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("ingredient_name", item.getIngredientName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiry_date", item.getExpiryDate());

        return dbHelper.update(
                DatabaseHelper.TABLE_PANTRY,
                values,
                "id = ?",
                new String[]{String.valueOf(item.getId())}
        );
    }

    /** DELETE - removes a pantry record. */
    public int delete(int id) {
        return dbHelper.delete(
                DatabaseHelper.TABLE_PANTRY,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }
}
