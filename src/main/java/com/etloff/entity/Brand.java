package com.etloff.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a product brand.
 * <p>
 * Each brand has a unique name.
 * </p>
 */
@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Constructs a Brand with the given name.
     *
     * @param name the brand name
     */
    public Brand(String name) {
        this.name = name;
    }

    public Brand() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
