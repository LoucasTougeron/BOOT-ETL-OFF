package com.etloff.dto;

/**
 * Simple DTO for Product responses to avoid serialization issues with JPA entities.
 */
public class ProductDTO {
    private Long id;
    private String name;
    private String nutritionScore;
    private Double energyPer100g;
    private Double fatPer100g;
    private String brandName;
    private String categoryName;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String nutritionScore, Double energyPer100g, Double fatPer100g,
                      String brandName, String categoryName) {
        this.id = id;
        this.name = name;
        this.nutritionScore = nutritionScore;
        this.energyPer100g = energyPer100g;
        this.fatPer100g = fatPer100g;
        this.brandName = brandName;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNutritionScore() {
        return nutritionScore;
    }

    public Double getEnergyPer100g() {
        return energyPer100g;
    }

    public Double getFatPer100g() {
        return fatPer100g;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}