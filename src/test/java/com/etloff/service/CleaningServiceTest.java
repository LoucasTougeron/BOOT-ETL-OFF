package com.etloff.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for {@link CleaningService}.
 */
class CleaningServiceTest {

    private CleaningService cleaningService;

    @BeforeEach
    void setUp() {
        cleaningService = new CleaningService();
    }

    @Test
    void cleanItem_shouldRemoveParasites() {
        String result = cleaningService.cleanItem("Sucre*");
        assertEquals("Sucre", result);

        result = cleaningService.cleanItem("_Maïs_");
        assertEquals("Maïs", result);
    }

    @Test
    void cleanItem_shouldRemoveParenthesesContent() {
        String result = cleaningService.cleanItem("Pâte (Farine 50%, Sucre 20%, Œufs 30%)");
        assertEquals("Pâte", result);
    }

    @Test
    void cleanItem_shouldRemovePercentages() {
        String result = cleaningService.cleanItem("Sucre 15%");
        assertEquals("Sucre", result);

        result = cleaningService.cleanItem("farine 50 %");
        assertEquals("farine", result);
    }

    @Test
    void cleanItem_shouldNormalizeSpaces() {
        String result = cleaningService.cleanItem("  Sucre    farine   ");
        assertEquals("Sucre farine", result);
    }

    @Test
    void cleanItem_shouldHandleNull() {
        String result = cleaningService.cleanItem(null);
        assertEquals("", result);
    }

    @Test
    void cleanItem_shouldHandleEmptyString() {
        String result = cleaningService.cleanItem("");
        assertEquals("", result);
    }

    @Test
    void splitList_shouldHandleParasites() {
        List<String> result = cleaningService.splitList("Sucre*, farine, _Maïs_");
        assertEquals(List.of("Sucre", "farine", "Maïs"), result);
    }

    @Test
    void splitList_shouldHandlePercentages() {
        List<String> result = cleaningService.splitList("Sucre 15%, farine 50%, Maïs 35%");
        assertEquals(List.of("Sucre", "farine", "Maïs"), result);
    }

    @Test
    void splitList_shouldHandleParentheses() {
        List<String> result = cleaningService.splitList("Sucre, banane, Pâte (Farine 50%, Sucre 20%, Œufs 30%)");
        assertEquals(List.of("Sucre", "banane", "Pâte"), result);
    }

    @Test
    void splitList_shouldHandleMultipleSeparators() {
        List<String> result = cleaningService.splitList("Sucre; farine, Maïs - Pâte");
        assertEquals(List.of("Sucre", "farine", "Maïs", "Pâte"), result);
    }

    @Test
    void splitList_shouldReturnEmptyForNull() {
        List<String> result = cleaningService.splitList(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void splitList_shouldReturnEmptyForBlank() {
        List<String> result = cleaningService.splitList("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void splitList_combinedScenario() {
        List<String> result = cleaningService.splitList(
                "Sucre 15%*, farine 50%, _Maïs_ 35%, Pâte (Farine 20%, Œufs 10%)"
        );
        assertEquals(List.of("Sucre", "farine", "Maïs", "Pâte"), result);
    }
}