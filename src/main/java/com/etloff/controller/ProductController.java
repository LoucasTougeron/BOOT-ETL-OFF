package com.etloff.controller;

import com.etloff.dto.ProductDTO;
import com.etloff.entity.Product;
import com.etloff.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for product-related endpoints.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * GET /products/top-by-brand?brand=X&limit=N
     * Returns the top N products for a given brand.
     */
    @GetMapping("/top-by-brand")
    public List<ProductDTO> getTopByBrand(@RequestParam String brand, @RequestParam(defaultValue = "10") int limit) {
        if (brand == null || brand.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'brand' is required");
        }
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return productRepository.findTopByBrandName(brand, limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /products/top-by-category?category=X&limit=N
     * Returns the top N products for a given category.
     */
    @GetMapping("/top-by-category")
    public List<ProductDTO> getTopByCategory(@RequestParam String category, @RequestParam(defaultValue = "10") int limit) {
        if (category == null || category.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'category' is required");
        }
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return productRepository.findTopByCategoryName(category, limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /products/top-by-brand-category?brand=X&category=Y&limit=N
     * Returns the top N products for a given brand and category.
     */
    @GetMapping("/top-by-brand-category")
    public List<ProductDTO> getTopByBrandAndCategory(@RequestParam String brand,
                                                     @RequestParam String category,
                                                     @RequestParam(defaultValue = "10") int limit) {
        if (brand == null || brand.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'brand' is required");
        }
        if (category == null || category.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'category' is required");
        }
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return productRepository.findTopByBrandNameAndCategoryName(brand, category, limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getNutritionScore(),
                product.getEnergyPer100g(),
                product.getFatPer100g(),
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}