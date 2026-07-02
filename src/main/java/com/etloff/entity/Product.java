package com.etloff.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a food product from Open Food Facts.
 * <p>
 * Contains nutritional information and relationships to brand, category,
 * ingredients, allergens, and additives.
 * </p>
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String name;

    /**
     * Nutritional score from A (best) to F (worst).
     */
    @Column(name = "nutrition_score", length = 1)
    @Pattern(regexp = "[A-Fa-f]", message = "Nutrition score must be between A and F")
    private String nutritionScore;

    @Column(name = "energy_per_100g")
    private Double energyPer100g;

    @Column(name = "fat_per_100g")
    private Double fatPer100g;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_allergens",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "product_additives",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "additive_id")
    )
    private Set<Additive> additives = new HashSet<>();

    public Product() {
    }

    public Product(Long id, String name, String nutritionScore, Double energyPer100g, Double fatPer100g,
                   Brand brand, Category category, Set<Ingredient> ingredients,
                   Set<Allergen> allergens, Set<Additive> additives) {
        this.id = id;
        this.name = name;
        this.nutritionScore = nutritionScore;
        this.energyPer100g = energyPer100g;
        this.fatPer100g = fatPer100g;
        this.brand = brand;
        this.category = category;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.additives = additives;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNutritionScore() {
        return nutritionScore;
    }

    public void setNutritionScore(String nutritionScore) {
        this.nutritionScore = nutritionScore;
    }

    public Double getEnergyPer100g() {
        return energyPer100g;
    }

    public void setEnergyPer100g(Double energyPer100g) {
        this.energyPer100g = energyPer100g;
    }

    public Double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(Double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }

    public Set<Additive> getAdditives() {
        return additives;
    }

    public void setAdditives(Set<Additive> additives) {
        this.additives = additives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}