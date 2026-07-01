package com.etloff.repository;

import com.etloff.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Brand} entities.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Finds a brand by its unique name.
     *
     * @param name the brand name
     * @return an {@link Optional} containing the brand if found
     */
    Optional<Brand> findByName(String name);
}