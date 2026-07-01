package com.etloff.parser;

import com.etloff.dto.ProductCsvLine;
import org.springframework.stereotype.Component;

/**
 * Parser for a single line of the Open Food Facts CSV file.
 * <p>
 * The CSV uses "|" as delimiter. Each line has 30 data columns followed by a
 * trailing "|" which produces an empty 31st field with split("\\|", -1).
 * This parser extracts only the columns needed for the ETL pipeline.
 * No cleaning is performed here.
 * </p>
 */
@Component
public class CsvLineParser {

    /** Expected number of columns in a valid CSV line (30 data + 1 trailing empty). */
    public static final int EXPECTED_COLUMNS = 31;

    /**
     * Parses a raw CSV line into a {@link ProductCsvLine}.
     * <p>
     * The first line (header) should be skipped by the caller.
     *
     * @param rawLine the raw CSV line (data line, not header)
     * @return a {@link ProductCsvLine} with raw values
     * @throws MalformedCsvLineException if the line does not have exactly 31 columns
     */
    public ProductCsvLine parseLine(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            throw new MalformedCsvLineException("CSV line is null or blank");
        }

        String[] columns = rawLine.split("\\|", -1);

        if (columns.length != EXPECTED_COLUMNS) {
            throw new MalformedCsvLineException(0, columns.length, EXPECTED_COLUMNS);
        }

        return new ProductCsvLine(
                columns[0],   // category
                columns[1],   // brand
                columns[2],   // product name
                columns[3],   // nutrition score
                columns[4],   // ingredients
                columns[5],   // energy per 100g
                columns[6],   // fat per 100g
                columns[28],  // allergens
                columns[29]   // additives
        );
    }
}
