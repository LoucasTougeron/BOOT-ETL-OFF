package com.etloff.repository;

import com.etloff.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Returns the top ingredients ordered by the number of associated products.
     *
     * @param limit maximum number of results
     * @return list of ingredients with their product count, ordered by descending frequency
     */
    @Query("SELECT i FROM Ingredient i JOIN i.products p GROUP BY i.id ORDER BY COUNT(p.id) DESC")
    List<Ingredient> findTopByProductCount(int limit);
}