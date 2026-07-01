package com.etloff.parser;

import com.etloff.dto.ProductCsvLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CsvLineParser}.
 */
class CsvLineParserTest {

    private CsvLineParser parser;

    @BeforeEach
    void setUp() {
        parser = new CsvLineParser();
    }

    @Test
    void parseLine_shouldParseValidLine() {
        // Build a line with exactly 31 fields (30 data + 1 trailing empty from final "|")
        // A 31-element array joined with "|" produces 30 pipes = 31 fields
        String[] columns = new String[31];
        for (int i = 0; i < 31; i++) {
            columns[i] = "";
        }
        columns[0] = "Desserts";
        columns[1] = "Ferrero";
        columns[2] = "Nutella";
        columns[3] = "E";
        columns[4] = "Sucre, Huile de palme, Noisettes";
        columns[5] = "2252";
        columns[6] = "30,5";
        columns[28] = "Lait, Noisettes";
        columns[29] = "E322, E500";

        String line = String.join("|", columns);
        ProductCsvLine result = parser.parseLine(line);

        assertEquals("Desserts", result.getCategory());
        assertEquals("Ferrero", result.getBrand());
        assertEquals("Nutella", result.getProductName());
        assertEquals("E", result.getNutritionScore());
        assertEquals("Sucre, Huile de palme, Noisettes", result.getIngredients());
        assertEquals("2252", result.getEnergyPer100g());
        assertEquals("30,5", result.getFatPer100g());
        assertEquals("Lait, Noisettes", result.getAllergens());
        assertEquals("E322, E500", result.getAdditives());
    }

    @Test
    void parseLine_shouldThrowForTooFewColumns() {
        // Only 5 columns instead of 31
        String line = "cat|brand|name|A|ingredients";
        MalformedCsvLineException ex = assertThrows(MalformedCsvLineException.class, () -> parser.parseLine(line));
        assertTrue(ex.getMessage().contains("31"));
        assertTrue(ex.getMessage().contains("5"));
    }

    @Test
    void parseLine_shouldThrowForEmptyLine() {
        assertThrows(MalformedCsvLineException.class, () -> parser.parseLine(""));
    }

    @Test
    void parseLine_shouldThrowForBlankLine() {
        assertThrows(MalformedCsvLineException.class, () -> parser.parseLine("   "));
    }

    @Test
    void parseLine_shouldThrowForNull() {
        assertThrows(MalformedCsvLineException.class, () -> parser.parseLine(null));
    }

    @Test
    void parseLine_shouldHandleEmptyFields() {
        // Build a line with exactly 31 fields, some empty
        String[] columns = new String[31];
        for (int i = 0; i < 31; i++) {
            columns[i] = "";
        }
        columns[0] = "Boissons";
        columns[1] = "Coca-Cola";
        columns[2] = "Coca Zero";
        columns[3] = "B";
        columns[28] = "";
        columns[29] = "E950, E951";

        String line = String.join("|", columns);
        ProductCsvLine result = parser.parseLine(line);

        assertEquals("Boissons", result.getCategory());
        assertEquals("Coca-Cola", result.getBrand());
        assertEquals("Coca Zero", result.getProductName());
        assertEquals("B", result.getNutritionScore());
        assertEquals("", result.getIngredients());
        assertEquals("", result.getEnergyPer100g());
        assertEquals("", result.getFatPer100g());
        assertEquals("", result.getAllergens());
        assertEquals("E950, E951", result.getAdditives());
    }

    @Test
    void parseLine_shouldThrowForTooManyColumns() {
        // 32 or 33 columns (from unescaped "|" in ingredients) -> invalid
        String[] columns = new String[33];
        for (int i = 0; i < 33; i++) {
            columns[i] = "col" + i;
        }
        String line = String.join("|", columns);
        MalformedCsvLineException ex = assertThrows(MalformedCsvLineException.class, () -> parser.parseLine(line));
        assertTrue(ex.getMessage().contains("31"));
        assertTrue(ex.getMessage().contains("33"));
    }
}