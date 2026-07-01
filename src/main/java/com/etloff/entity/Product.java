package com.etloff.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
}