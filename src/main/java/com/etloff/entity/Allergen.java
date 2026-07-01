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

    /**
     * Constructs an Allergen with the given name.
     *
     * @param name the allergen name
     */
    public Allergen(String name) {
        this.name = name;
    }
}