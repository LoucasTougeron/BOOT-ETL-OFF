package com.etloff.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Temporary writer that counts processed lines and logs the total at the end.
 * <p>
 * Used during the initial ETL setup before connecting real persistence.
 * </p>
 */
@Component
public class LogLineWriter implements ItemWriter<String> {

    private static final Logger log = LoggerFactory.getLogger(LogLineWriter.class);

    private int totalLines = 0;

    @Override
    public void write(Chunk<? extends String> chunk) {
        totalLines += chunk.size();
        log.debug("Processed chunk of {} lines (total: {})", chunk.size(), totalLines);
    }

    /**
     * Returns the total number of lines processed so far.
     *
     * @return total line count
     */
    public int getTotalLines() {
        return totalLines;
    }
}