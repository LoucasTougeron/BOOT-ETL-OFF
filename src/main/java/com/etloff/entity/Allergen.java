package com.etloff.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an allergen.
 * <p>
 * Each allergen has a unique name.
 * </p>
 */
@Entity
@Table(name = "allergens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Allergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "allergens")
    private java.util.Set<Product> products = new java.util.HashSet<>();

    /**
     * Constructs an Allergen with the given name.
     *
     * @param name the allergen name
     */
    public Allergen(String name) {
        this.name = name;
    }

    public Allergen() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public java.util.Set<Product> getProducts() {
        return products;
    }
}
