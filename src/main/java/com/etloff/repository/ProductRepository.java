package com.etloff.repository;

import com.etloff.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Product} entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}