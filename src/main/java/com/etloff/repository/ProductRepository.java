package com.etloff.repository;

import com.etloff.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Product} entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Returns the top products for a given brand, ordered by id.
     *
     * @param brandName the brand name
     * @param limit     maximum number of results
     * @return list of products for the brand
     */
    @Query("SELECT p FROM Product p WHERE p.brand.name = :brandName ORDER BY p.id")
    List<Product> findTopByBrandName(String brandName, int limit);

    /**
     * Returns the top products for a given category, ordered by id.
     *
     * @param categoryName the category name
     * @param limit        maximum number of results
     * @return list of products for the category
     */
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName ORDER BY p.id")
    List<Product> findTopByCategoryName(String categoryName, int limit);

    /**
     * Returns the top products for a given brand and category, ordered by id.
     *
     * @param brandName    the brand name
     * @param categoryName the category name
     * @param limit        maximum number of results
     * @return list of products for the brand and category
     */
    @Query("SELECT p FROM Product p WHERE p.brand.name = :brandName AND p.category.name = :categoryName ORDER BY p.id")
    List<Product> findTopByBrandNameAndCategoryName(String brandName, String categoryName, int limit);
}