package com.etloff.repository;

import com.etloff.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Allergen} entities.
 */
@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {

    /**
     * Finds an allergen by its unique name.
     *
     * @param name the allergen name
     * @return an {@link Optional} containing the allergen if found
     */
    Optional<Allergen> findByName(String name);
}