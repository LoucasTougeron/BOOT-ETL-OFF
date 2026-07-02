package com.etloff.batch;

import com.etloff.dto.ProductCsvLine;
import com.etloff.entity.*;
import com.etloff.parser.CsvLineParser;
import com.etloff.parser.MalformedCsvLineException;
import com.etloff.service.CleaningService;
import com.etloff.service.EntityResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes a raw CSV line into a persisted {@link Product} entity.
 * <p>
 * Pipeline: parse -> clean -> resolve references -> build Product.
 * Malformed lines are logged and skipped (return null).
 * </p>
 */
@Component
public class ProductProcessor implements ItemProcessor<String, Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductProcessor.class);

    private final CsvLineParser csvLineParser;
    private final CleaningService cleaningService;
    private final EntityResolverService entityResolverService;

    public ProductProcessor(CsvLineParser csvLineParser,
                            CleaningService cleaningService,
                            EntityResolverService entityResolverService) {
        this.csvLineParser = csvLineParser;
        this.cleaningService = cleaningService;
        this.entityResolverService = entityResolverService;
    }

    @Override
    public Product process(String rawLine) {
        // Step 1: Parse
        ProductCsvLine csvLine;
        try {
            csvLine = csvLineParser.parseLine(rawLine);
        } catch (MalformedCsvLineException e) {
            log.warn("Skipping malformed line: {}", e.getMessage());
            return null;
        }

        // Step 2: Resolve brand and category
        Brand brand = entityResolverService.resolveBrand(csvLine.getBrand());
        Category category = entityResolverService.resolveCategory(csvLine.getCategory());

        // Step 3: Clean and resolve ingredients, allergens, additives
        Set<Ingredient> ingredients = resolveSet(csvLine.getIngredients(), entityResolverService::resolveIngredient);
        Set<Allergen> allergens = resolveSet(csvLine.getAllergens(), entityResolverService::resolveAllergen);
        Set<Additive> additives = resolveSet(csvLine.getAdditives(), entityResolverService::resolveAdditive);

        // Step 4: Parse numeric fields
        Double energy = parseDouble(csvLine.getEnergyPer100g());
        Double fat = parseDouble(csvLine.getFatPer100g());

        // Step 5: Build Product
        Product product = new Product();
        product.setName(csvLine.getProductName());
        product.setNutritionScore(csvLine.getNutritionScore());
        product.setEnergyPer100g(energy);
        product.setFatPer100g(fat);
        product.setBrand(brand);
        product.setCategory(category);
        product.setIngredients(ingredients);
        product.setAllergens(allergens);
        product.setAdditives(additives);

        return product;
    }

    /**
     * Cleans a raw list string and resolves each item into an entity.
     * Null results (e.g. names exceeding max length) are filtered out.
     */
    private <T> Set<T> resolveSet(String raw, java.util.function.Function<String, T> resolver) {
        return cleaningService.splitList(raw).stream()
                .map(resolver)
                .filter(e -> e != null)
                .collect(Collectors.toSet());
    }

    /**
     * Parses a numeric string (may use comma as decimal separator).
     */
    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            log.debug("Cannot parse numeric value: '{}'", value);
            return null;
        }
    }
}