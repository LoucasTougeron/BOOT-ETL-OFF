package com.etloff.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an ingredient.
 * <p>
 * Each ingredient has a unique name.
 * </p>
 */
@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Constructs an Ingredient with the given name.
     *
     * @param name the ingredient name
     */
    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient() {
    }
}
