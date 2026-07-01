package com.etloff.dto;

/**
 * DTO representing a raw (uncleaned) line from the Open Food Facts CSV file.
 * <p>
 * Contains only the fields needed for the ETL pipeline.
 * No cleaning is performed at this stage.
 * </p>
 */
public class ProductCsvLine {

    private String category;
    private String brand;
    private String productName;
    private String nutritionScore;
    private String ingredients;
    private String energyPer100g;
    private String fatPer100g;
    private String allergens;
    private String additives;

    /**
     * Constructs a ProductCsvLine with all fields.
     *
     * @param category       category name
     * @param brand          brand name
     * @param productName    product name
     * @param nutritionScore nutrition score A-F
     * @param ingredients    raw ingredient list
     * @param energyPer100g  energy per 100g
     * @param fatPer100g     fat per 100g
     * @param allergens      raw allergen list
     * @param additives      raw additive list
     */
    public ProductCsvLine(String category, String brand, String productName, String nutritionScore,
                          String ingredients, String energyPer100g, String fatPer100g,
                          String allergens, String additives) {
        this.category = category;
        this.brand = brand;
        this.productName = productName;
        this.nutritionScore = nutritionScore;
        this.ingredients = ingredients;
        this.energyPer100g = energyPer100g;
        this.fatPer100g = fatPer100g;
        this.allergens = allergens;
        this.additives = additives;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getProductName() {
        return productName;
    }

    public String getNutritionScore() {
        return nutritionScore;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getEnergyPer100g() {
        return energyPer100g;
    }

    public String getFatPer100g() {
        return fatPer100g;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getAdditives() {
        return additives;
    }
}