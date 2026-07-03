package com.etloff.repository;

import com.etloff.entity.Additive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Additive} entities.
 */
@Repository
public interface AdditiveRepository extends JpaRepository<Additive, Long> {

    /**
     * Finds an additive by its unique name.
     *
     * @param name the additive name
     * @return an {@link Optional} containing the additive if found
     */
    Optional<Additive> findByName(String name);

    /**
     * Returns the top additives ordered by the number of associated products.
     *
     * @param limit maximum number of results
     * @return list of additives with their product count, ordered by descending frequency
     */
    @Query("SELECT a FROM Additive a JOIN a.products p GROUP BY a.id ORDER BY COUNT(p.id) DESC")
    List<Additive> findTopByProductCount(int limit);
}