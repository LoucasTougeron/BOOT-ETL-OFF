package com.etloff.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Centralized service for cleaning raw string data from Open Food Facts.
 * <p>
 * All cleaning logic is reusable and isolated in this single service.
 * </p>
 */
@Service
public class CleaningService {

    private static final Pattern PARASITES = Pattern.compile("[*_]+");
    private static final Pattern PARENTHESES = Pattern.compile("\\([^)]*\\)");
    private static final Pattern PERCENTAGE = Pattern.compile("\\s*\\d+\\s*%\\s*");
    private static final Pattern SEPARATORS = Pattern.compile("\\s*[,;]\\s*|\\s*-\\s+");
    private static final Pattern MULTISPACE = Pattern.compile("\\s{2,}");

    /**
     * Cleans a single raw item by removing parasites, parentheses content,
     * percentages and normalizing spaces.
     *
     * @param raw the raw string to clean
     * @return the cleaned string, or an empty string if null
     */
    public String cleanItem(String raw) {
        if (raw == null) {
            return "";
        }

        String cleaned = raw.trim();

        // Remove parasites characters (*, _)
        cleaned = PARASITES.matcher(cleaned).replaceAll("");

        // Remove parentheses and their content
        cleaned = PARENTHESES.matcher(cleaned).replaceAll("");

        // Remove percentages (e.g. "15%", "50 %")
        cleaned = PERCENTAGE.matcher(cleaned).replaceAll("");

        // Normalize spaces
        cleaned = MULTISPACE.matcher(cleaned).replaceAll(" ").trim();

        return cleaned;
    }

    /**
     * Splits a raw list string into individual cleaned items.
     * <p>
     * Handles multiple separators: comma, semicolon, or dash followed by space.
     * Parentheses content is removed before splitting to avoid incorrect splits.
     *
     * @param raw the raw list string (e.g. "Sucre, farine, _Maïs_")
     * @return a list of cleaned, non-empty items
     */
    public List<String> splitList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }

        // Remove parentheses content first, before splitting
        String withoutParentheses = PARENTHESES.matcher(raw).replaceAll("");

        return Arrays.stream(SEPARATORS.split(withoutParentheses))
                .map(this::cleanItem)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}