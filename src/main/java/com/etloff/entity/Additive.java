package com.etloff.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a food additive.
 * <p>
 * Each additive has a unique name.
 * </p>
 */
@Entity
@Table(name = "additives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Additive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Constructs an Additive with the given name.
     *
     * @param name the additive name
     */
    public Additive(String name) {
        this.name = name;
    }
}