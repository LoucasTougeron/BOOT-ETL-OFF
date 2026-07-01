package com.etloff.repository;

import com.etloff.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Ingredient} entities.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Finds an ingredient by its unique name.
     *
     * @param name the ingredient name
     * @return an {@link Optional} containing the ingredient if found
     */
    Optional<Ingredient> findByName(String name);
}