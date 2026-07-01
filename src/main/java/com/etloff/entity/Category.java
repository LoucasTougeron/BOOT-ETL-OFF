package com.etloff.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a product category.
 * <p>
 * Each category has a unique name.
 * </p>
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Constructs a Category with the given name.
     *
     * @param name the category name
     */
    public Category(String name) {
        this.name = name;
    }
}