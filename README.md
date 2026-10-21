# Smart Pantry Manager

**Student:** Lebogang Ramabu  
**Student Number:** 402307388  
**Module:** Mobile App Development 700  
**Application:** Smart Pantry Manager

## 1. Application Description

Smart Pantry Manager is a Java Android application designed to reduce food waste. A user records the ingredients available at home, and the application suggests only recipes that can be made using the ingredients currently in the pantry.

The central rule is strict matching: **every recipe ingredient must be present in the pantry in at least the required quantity**. A recipe is not suggested when one required ingredient is missing or when the available quantity is insufficient.

## 2. Technology Choice

- Language: Java
- IDE: Android Studio
- UI: XML layouts with native Android Views
- Database: SQLite using `SQLiteOpenHelper`
- Dynamic list: `ListView` with custom `BaseAdapter`
- Navigation: Android Activities and explicit Intents
- Settings: `SharedPreferences`
- Testing: Android Emulator / physical Android device

SQLite was selected because the assignment permits it, the application is primarily local/offline, and the module guide teaches SQLite persistence through a database helper and data-source style classes.

## 3. Main Screens

1. Pantry List
2. Add/Edit Ingredient
3. Suggested Recipes
4. Recipe Detail
5. Settings

## 4. Database

### PANTRY
Stores user-owned ingredients.

- `id` - primary key
- `ingredient_name` - ingredient name
- `quantity` - amount available
- `unit` - unit such as g, kg, ml or piece
- `expiry_date` - optional expiry date

### RECIPES
Stores the seeded recipe collection.

- `id` - recipe ID
- `name` - recipe name
- `instructions` - preparation method

### RECIPE_INGREDIENTS
Connects a recipe to its required ingredients.

- `id` - primary key
- `recipe_id` - foreign key to RECIPES
- `ingredient_name` - required ingredient
- `required_quantity` - quantity needed
- `unit` - required unit

Twenty recipes are seeded the first time the database is created.

## 5. Folder Structure

```text
SmartPantryManager/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/lebogangramabu/smartpantry/
│       │   ├── MainActivity.java
│       │   ├── BaseActivity.java
│       │   ├── AddEditIngredientActivity.java
│       │   ├── SuggestedRecipesActivity.java
│       │   ├── RecipeDetailActivity.java
│       │   ├── SettingsActivity.java
│       │   ├── adapter/
│       │   │   ├── PantryAdapter.java
│       │   │   └── RecipeAdapter.java
│       │   ├── data/
│       │   │   ├── DatabaseHelper.java
│       │   │   ├── PantryRepository.java
│       │   │   └── RecipeRepository.java
│       │   ├── model/
│       │   │   ├── PantryItem.java
│       │   │   ├── Recipe.java
│       │   │   └── RecipeIngredient.java
│       │   └── util/
│       │       ├── IngredientMatcher.java
│       │       ├── DateUtils.java
│       │       └── MessageUtils.java
│       └── res/
│           ├── layout/
│           └── values/
├── docs/
├── tests/
├── build.gradle
├── settings.gradle
└── README.md
```

## 6. Setup and Run

1. Install a current stable Android Studio release.
2. Open the `SmartPantryManager` folder in Android Studio.
3. Let Android Studio download/sync the Gradle and Android SDK components required by the project.
4. Ensure an Android SDK Platform supported by the project is installed.
5. Create one Android Virtual Device (AVD), or connect a physical Android phone with USB debugging enabled.
6. Press **Run** and select the emulator/device.
7. On first launch, the SQLite database is created and twenty recipes are seeded automatically.
8. Add pantry ingredients, open **Suggested Recipes**, and verify the strict matching behaviour.

## 7. CRUD Test Flow

- Create: add an ingredient.
- Read: verify it appears in the Pantry list.
- Update: edit the ingredient quantity/name/unit.
- Delete: delete the ingredient and verify it disappears.
- Persistence: close and reopen the application and verify saved pantry records remain.

## 8. Strict-Matching Demo

Example:

```text
Recipe requires:
Chicken 500 g
Tomato 2 pieces

Pantry contains:
Chicken 600 g
Tomatoes 2 pieces

Result: Recipe IS suggested.
```

Removing the tomatoes causes the same recipe to disappear from Suggested Recipes. Reducing chicken to 400 g also causes it to disappear.

## 9. Build Tooling

The project is configured for the current Android Gradle tooling line used for the project preparation. Android Studio may prompt for SDK or Gradle components when the project is opened.

The command-line Gradle wrapper JAR is not bundled in this source package because it was not available in the build environment used for verification. Android Studio can still open/sync the project, and the required Gradle distribution is recorded in `gradle/wrapper/gradle-wrapper.properties`.

