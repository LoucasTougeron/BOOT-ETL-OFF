package com.etloff.service;

import com.etloff.entity.*;
import com.etloff.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EntityResolverService}.
 * <p>
 * Uses real entity instances (created via constructors) and mocked repositories.
 * Entity classes are not mocked to avoid Byte Buddy / Java 25 compatibility issues.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class EntityResolverServiceTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private AllergenRepository allergenRepository;
    @Mock
    private AdditiveRepository additiveRepository;

    private EntityResolverService resolver;

    @BeforeEach
    void setUp() {
        resolver = new EntityResolverService(
                brandRepository, categoryRepository, ingredientRepository,
                allergenRepository, additiveRepository
        );
    }

    // --- Brand ---

    @Test
    void resolveBrand_shouldReturnExisting() {
        Brand existing = new Brand("Ferrero");
        when(brandRepository.findByName("Ferrero")).thenReturn(Optional.of(existing));

        Brand result = resolver.resolveBrand("Ferrero");

        assertSame(existing, result);
        verify(brandRepository).findByName("Ferrero");
        verifyNoMoreInteractions(brandRepository);
    }

    @Test
    void resolveBrand_shouldCreateIfNotFound() {
        when(brandRepository.findByName("Ferrero")).thenReturn(Optional.empty());
        Brand saved = new Brand("Ferrero");
        when(brandRepository.save(any())).thenReturn(saved);

        Brand result = resolver.resolveBrand("Ferrero");

        assertSame(saved, result);
        verify(brandRepository).findByName("Ferrero");
        verify(brandRepository).save(any());
    }

    // --- Category ---

    @Test
    void resolveCategory_shouldReturnExisting() {
        Category existing = new Category("Desserts");
        when(categoryRepository.findByName("Desserts")).thenReturn(Optional.of(existing));

        Category result = resolver.resolveCategory("Desserts");

        assertSame(existing, result);
        verify(categoryRepository).findByName("Desserts");
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void resolveCategory_shouldCreateIfNotFound() {
        when(categoryRepository.findByName("Desserts")).thenReturn(Optional.empty());
        Category saved = new Category("Desserts");
        when(categoryRepository.save(any())).thenReturn(saved);

        Category result = resolver.resolveCategory("Desserts");

        assertSame(saved, result);
        verify(categoryRepository).findByName("Desserts");
        verify(categoryRepository).save(any());
    }

    // --- Ingredient ---

    @Test
    void resolveIngredient_shouldReturnExisting() {
        Ingredient existing = new Ingredient("Sucre");
        when(ingredientRepository.findByName("Sucre")).thenReturn(Optional.of(existing));

        Ingredient result = resolver.resolveIngredient("Sucre");

        assertSame(existing, result);
        verify(ingredientRepository).findByName("Sucre");
        verifyNoMoreInteractions(ingredientRepository);
    }

    @Test
    void resolveIngredient_shouldCreateIfNotFound() {
        when(ingredientRepository.findByName("Sucre")).thenReturn(Optional.empty());
        Ingredient saved = new Ingredient("Sucre");
        when(ingredientRepository.save(any())).thenReturn(saved);

        Ingredient result = resolver.resolveIngredient("Sucre");

        assertSame(saved, result);
        verify(ingredientRepository).findByName("Sucre");
        verify(ingredientRepository).save(any());
    }

    // --- Allergen ---

    @Test
    void resolveAllergen_shouldReturnExisting() {
        Allergen existing = new Allergen("Lait");
        when(allergenRepository.findByName("Lait")).thenReturn(Optional.of(existing));

        Allergen result = resolver.resolveAllergen("Lait");

        assertSame(existing, result);
        verify(allergenRepository).findByName("Lait");
        verifyNoMoreInteractions(allergenRepository);
    }

    @Test
    void resolveAllergen_shouldCreateIfNotFound() {
        when(allergenRepository.findByName("Lait")).thenReturn(Optional.empty());
        Allergen saved = new Allergen("Lait");
        when(allergenRepository.save(any())).thenReturn(saved);

        Allergen result = resolver.resolveAllergen("Lait");

        assertSame(saved, result);
        verify(allergenRepository).findByName("Lait");
        verify(allergenRepository).save(any());
    }

    // --- Additive ---

    @Test
    void resolveAdditive_shouldReturnExisting() {
        Additive existing = new Additive("E322");
        when(additiveRepository.findByName("E322")).thenReturn(Optional.of(existing));

        Additive result = resolver.resolveAdditive("E322");

        assertSame(existing, result);
        verify(additiveRepository).findByName("E322");
        verifyNoMoreInteractions(additiveRepository);
    }

    @Test
    void resolveAdditive_shouldCreateIfNotFound() {
        when(additiveRepository.findByName("E322")).thenReturn(Optional.empty());
        Additive saved = new Additive("E322");
        when(additiveRepository.save(any())).thenReturn(saved);

        Additive result = resolver.resolveAdditive("E322");

        assertSame(saved, result);
        verify(additiveRepository).findByName("E322");
        verify(additiveRepository).save(any());
    }

    // --- Max length filter (applies to all entity types) ---

    @Test
    void resolveBrand_shouldReturnNullIfNameTooLong() {
        String longName = "X".repeat(EntityResolverService.MAX_NAME_LENGTH + 1);

        Brand result = resolver.resolveBrand(longName);

        assertNull(result);
        verifyNoInteractions(brandRepository);
    }

    @Test
    void resolveCategory_shouldReturnNullIfNameTooLong() {
        String longName = "X".repeat(EntityResolverService.MAX_NAME_LENGTH + 1);

        Category result = resolver.resolveCategory(longName);

        assertNull(result);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void resolveIngredient_shouldReturnNullIfNameTooLong() {
        String longName = "X".repeat(EntityResolverService.MAX_NAME_LENGTH + 1);

        Ingredient result = resolver.resolveIngredient(longName);

        assertNull(result);
        verifyNoInteractions(ingredientRepository);
    }

    @Test
    void resolveAllergen_shouldReturnNullIfNameTooLong() {
        String longName = "X".repeat(EntityResolverService.MAX_NAME_LENGTH + 1);

        Allergen result = resolver.resolveAllergen(longName);

        assertNull(result);
        verifyNoInteractions(allergenRepository);
    }

    @Test
    void resolveAdditive_shouldReturnNullIfNameTooLong() {
        String longName = "X".repeat(EntityResolverService.MAX_NAME_LENGTH + 1);

        Additive result = resolver.resolveAdditive(longName);

        assertNull(result);
        verifyNoInteractions(additiveRepository);
    }

    @Test
    void resolveBrand_shouldAcceptNameAtMaxLength() {
        String exactMax = "X".repeat(EntityResolverService.MAX_NAME_LENGTH);
        Brand existing = new Brand(exactMax);
        when(brandRepository.findByName(exactMax)).thenReturn(Optional.of(existing));

        Brand result = resolver.resolveBrand(exactMax);

        assertSame(existing, result);
        verify(brandRepository).findByName(exactMax);
        verifyNoMoreInteractions(brandRepository);
    }

    @Test
    void resolveIngredient_shouldReturnNullIfNameNull() {
        Ingredient result = resolver.resolveIngredient(null);

        assertNull(result);
        verifyNoInteractions(ingredientRepository);
    }
}