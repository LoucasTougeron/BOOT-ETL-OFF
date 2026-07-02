package com.etloff.service;

import com.etloff.entity.*;
import com.etloff.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

/**
 * Service for resolving reference entities (Brand, Category, Ingredient, Allergen, Additive)
 * by name. If the entity already exists in the database, it is returned.
 * Otherwise, a new entity is created and saved.
 * <p>
 * Names exceeding {@link #MAX_NAME_LENGTH} characters are considered non-exploitable
 * (e.g. free-text descriptions that ended up in the ingredients column) and are silently
 * skipped (returns null).
 * </p>
 */
@Service
public class EntityResolverService {

    private static final Logger log = LoggerFactory.getLogger(EntityResolverService.class);

    /**
     * Maximum allowed length for a reference entity name.
     * Values longer than this are considered non-exploitable and ignored.
     */
    public static final int MAX_NAME_LENGTH = 150;

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final AllergenRepository allergenRepository;
    private final AdditiveRepository additiveRepository;

    public EntityResolverService(BrandRepository brandRepository,
                                 CategoryRepository categoryRepository,
                                 IngredientRepository ingredientRepository,
                                 AllergenRepository allergenRepository,
                                 AdditiveRepository additiveRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.allergenRepository = allergenRepository;
        this.additiveRepository = additiveRepository;
    }

    /**
     * Resolves a brand by its cleaned name.
     *
     * @param name the cleaned brand name
     * @return the existing or newly created Brand, or null if the name is too long
     */
    @Transactional
    public Brand resolveBrand(String name) {
        return resolve(
                name,
                brandRepository::findByName,
                Brand::new,
                brandRepository::save
        );
    }

    /**
     * Resolves a category by its cleaned name.
     *
     * @param name the cleaned category name
     * @return the existing or newly created Category, or null if the name is too long
     */
    @Transactional
    public Category resolveCategory(String name) {
        return resolve(
                name,
                categoryRepository::findByName,
                Category::new,
                categoryRepository::save
        );
    }

    /**
     * Resolves an ingredient by its cleaned name.
     *
     * @param name the cleaned ingredient name
     * @return the existing or newly created Ingredient, or null if the name is too long
     */
    @Transactional
    public Ingredient resolveIngredient(String name) {
        return resolve(
                name,
                ingredientRepository::findByName,
                Ingredient::new,
                ingredientRepository::save
        );
    }

    /**
     * Resolves an allergen by its cleaned name.
     *
     * @param name the cleaned allergen name
     * @return the existing or newly created Allergen, or null if the name is too long
     */
    @Transactional
    public Allergen resolveAllergen(String name) {
        return resolve(
                name,
                allergenRepository::findByName,
                Allergen::new,
                allergenRepository::save
        );
    }

    /**
     * Resolves an additive by its cleaned name.
     *
     * @param name the cleaned additive name
     * @return the existing or newly created Additive, or null if the name is too long
     */
    @Transactional
    public Additive resolveAdditive(String name) {
        return resolve(
                name,
                additiveRepository::findByName,
                Additive::new,
                additiveRepository::save
        );
    }

    /**
     * Generic resolution logic: look up by name, create and save if not found.
     * <p>
     * Names longer than {@link #MAX_NAME_LENGTH} are ignored (returns null).
     *
     * @param name    the cleaned name
     * @param finder  function to look up an entity by name (returns Optional)
     * @param creator function to create a new entity from a name
     * @param saver   function to persist a new entity
     * @param <T>     the entity type
     * @return the existing or newly created entity, or null if the name is too long
     */
    private <T> T resolve(String name,
                          Function<String, Optional<T>> finder,
                          Function<String, T> creator,
                          Function<T, T> saver) {
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            if (name != null) {
                log.debug("Skipping name too long ({} chars, max {}): {}",
                        name.length(), MAX_NAME_LENGTH, name.substring(0, Math.min(50, name.length())));
            }
            return null;
        }
        return finder.apply(name)
                .orElseGet(() -> saver.apply(creator.apply(name)));
    }
}