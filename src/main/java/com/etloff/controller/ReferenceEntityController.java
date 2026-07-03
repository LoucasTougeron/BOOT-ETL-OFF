package com.etloff.controller;

import com.etloff.dto.ReferenceEntityDTO;
import com.etloff.entity.Ingredient;
import com.etloff.entity.Allergen;
import com.etloff.entity.Additive;
import com.etloff.repository.IngredientRepository;
import com.etloff.repository.AllergenRepository;
import com.etloff.repository.AdditiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for reference entity endpoints (ingredients, allergens, additives).
 */
@RestController
public class ReferenceEntityController {

    private final IngredientRepository ingredientRepository;
    private final AllergenRepository allergenRepository;
    private final AdditiveRepository additiveRepository;

    public ReferenceEntityController(IngredientRepository ingredientRepository,
                                     AllergenRepository allergenRepository,
                                     AdditiveRepository additiveRepository) {
        this.ingredientRepository = ingredientRepository;
        this.allergenRepository = allergenRepository;
        this.additiveRepository = additiveRepository;
    }

    /**
     * GET /ingredients/top?limit=N
     * Returns the top N most common ingredients by product count.
     */
    @GetMapping("/ingredients/top")
    public List<ReferenceEntityDTO> getTopIngredients(@RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return ingredientRepository.findTopByProductCount(limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /allergens/top?limit=N
     * Returns the top N most common allergens by product count.
     */
    @GetMapping("/allergens/top")
    public List<ReferenceEntityDTO> getTopAllergens(@RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return allergenRepository.findTopByProductCount(limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /additives/top?limit=N
     * Returns the top N most common additives by product count.
     */
    @GetMapping("/additives/top")
    public List<ReferenceEntityDTO> getTopAdditives(@RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'limit' must be positive");
        }
        return additiveRepository.findTopByProductCount(limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ReferenceEntityDTO toDTO(Ingredient ingredient) {
        return new ReferenceEntityDTO(ingredient.getId(), ingredient.getName(), ingredient.getProducts().size());
    }

    private ReferenceEntityDTO toDTO(Allergen allergen) {
        return new ReferenceEntityDTO(allergen.getId(), allergen.getName(), allergen.getProducts().size());
    }

    private ReferenceEntityDTO toDTO(Additive additive) {
        return new ReferenceEntityDTO(additive.getId(), additive.getName(), additive.getProducts().size());
    }
}