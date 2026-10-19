package com.lebogangramabu.smartpantry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lebogangramabu.smartpantry.data.PantryRepository;
import com.lebogangramabu.smartpantry.model.PantryItem;
import com.lebogangramabu.smartpantry.util.DateUtils;
import com.lebogangramabu.smartpantry.util.MessageUtils;

import java.util.Calendar;

/**
 * AddEditIngredientActivity handles both CREATE and UPDATE operations.
 */
public class AddEditIngredientActivity extends Activity {

    public static final String EXTRA_PANTRY_ID = "pantry_id";

    private EditText ingredientNameInput;
    private EditText quantityInput;
    private Spinner unitSpinner;
    private TextView selectedDateText;
    private TextView messageText;
    private Button clearDateButton;

    private PantryRepository pantryRepository;
    private int pantryId = -1;
    private String selectedExpiryDate = "";

    private final String[] units = {
            "g", "kg", "ml", "L", "piece", "slice", "clove", "can"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        ingredientNameInput = findViewById(R.id.etIngredientName);
        quantityInput = findViewById(R.id.etQuantity);
        unitSpinner = findViewById(R.id.spinnerUnit);
        selectedDateText = findViewById(R.id.tvSelectedDate);
        messageText = findViewById(R.id.tvIngredientMessage);
        clearDateButton = findViewById(R.id.btnClearDate);

        Button pickDateButton = findViewById(R.id.btnPickDate);
        Button saveButton = findViewById(R.id.btnSaveIngredient);
        Button backButton = findViewById(R.id.btnBackFromIngredient);
        TextView title = findViewById(R.id.tvIngredientTitle);

        pantryRepository = new PantryRepository(this);

        // Spinner is the drop-down list of units.
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        // Check whether this screen was opened for editing.
        pantryId = getIntent().getIntExtra(EXTRA_PANTRY_ID, -1);
        if (pantryId != -1) {
            title.setText("Edit Ingredient");
            saveButton.setText("Update Ingredient");
            loadIngredientForEditing();
        }

        backButton.setOnClickListener(v -> finish());
        pickDateButton.setOnClickListener(v -> openDatePicker());
        clearDateButton.setOnClickListener(v -> clearExpiryDate());
        saveButton.setOnClickListener(v -> saveIngredient());
    }

    /** Loads the selected pantry record into the form. */
    private void loadIngredientForEditing() {
        PantryItem item = pantryRepository.getById(pantryId);

        if (item == null) {
            MessageUtils.showError(messageText, "The pantry item could not be found.");
            return;
        }

        ingredientNameInput.setText(item.getIngredientName());
        quantityInput.setText(String.valueOf(item.getQuantity()));
        setSpinnerToUnit(item.getUnit());

        selectedExpiryDate = item.getExpiryDate() == null ? "" : item.getExpiryDate();
        updateSelectedDateText();
    }

    /** Selects the matching unit in the Spinner. */
    private void setSpinnerToUnit(String unit) {
        if (unit == null) {
            return;
        }

        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(unit)) {
                unitSpinner.setSelection(i);
                return;
            }
        }
    }

    /** Opens Android's built-in calendar picker. */
    private void openDatePicker() {
        Calendar today = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedExpiryDate = DateUtils.formatForStorage(year, month, dayOfMonth);
                    updateSelectedDateText();
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void clearExpiryDate() {
        selectedExpiryDate = "";
        updateSelectedDateText();
    }

    private void updateSelectedDateText() {
        if (selectedExpiryDate.isEmpty()) {
            selectedDateText.setText("No expiry date selected");
            clearDateButton.setVisibility(View.GONE);
        } else {
            selectedDateText.setText("Selected: " + DateUtils.formatForDisplay(selectedExpiryDate));
            clearDateButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validates the user's form and then creates or updates the database record.
     */
    private void saveIngredient() {
        String name = ingredientNameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem().toString();

        // Check the ingredient name first.
        if (name.isEmpty()) {
            MessageUtils.showError(messageText, "Please enter an ingredient name.");
            ingredientNameInput.requestFocus();
            return;
        }

        // A quantity is required and must be a valid positive number.
        if (quantityText.isEmpty()) {
            MessageUtils.showError(messageText, "Please enter a quantity.");
            quantityInput.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            MessageUtils.showError(messageText, "Quantity must be a valid number.");
            quantityInput.requestFocus();
            return;
        }

        if (quantity <= 0) {
            MessageUtils.showError(messageText, "Quantity must be greater than zero.");
            quantityInput.requestFocus();
            return;
        }

        // Build the Java object that represents the database record.
        PantryItem item = new PantryItem(
                pantryId,
                name,
                quantity,
                unit,
                selectedExpiryDate
        );

        if (pantryId == -1) {
            // CREATE operation.
            long newId = pantryRepository.add(item);

            if (newId != -1) {
                MessageUtils.showSuccess(messageText, "Ingredient added successfully.");
                clearForm();
            } else {
                MessageUtils.showError(messageText, "The ingredient could not be saved.");
            }
        } else {
            // UPDATE operation.
            int rowsUpdated = pantryRepository.update(item);

            if (rowsUpdated > 0) {
                MessageUtils.showSuccess(messageText, "Ingredient updated successfully.");
            } else {
                MessageUtils.showError(messageText, "The ingredient could not be updated.");
            }
        }
    }

    /** Clears the form after a successful CREATE operation. */
    private void clearForm() {
        ingredientNameInput.setText("");
        quantityInput.setText("");
        unitSpinner.setSelection(0);
        selectedExpiryDate = "";
        updateSelectedDateText();
    }
}
