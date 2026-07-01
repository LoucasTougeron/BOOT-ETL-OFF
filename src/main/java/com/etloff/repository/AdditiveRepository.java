package com.etloff.repository;

import com.etloff.entity.Additive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}