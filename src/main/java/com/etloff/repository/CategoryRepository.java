package com.etloff.repository;

import com.etloff.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its unique name.
     *
     * @param name the category name
     * @return an {@link Optional} containing the category if found
     */
    Optional<Category> findByName(String name);
}