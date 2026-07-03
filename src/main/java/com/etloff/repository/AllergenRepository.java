package com.etloff.repository;

import com.etloff.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Returns the top allergens ordered by the number of associated products.
     *
     * @param limit maximum number of results
     * @return list of allergens with their product count, ordered by descending frequency
     */
    @Query("SELECT a FROM Allergen a JOIN a.products p GROUP BY a.id ORDER BY COUNT(p.id) DESC")
    List<Allergen> findTopByProductCount(int limit);
}