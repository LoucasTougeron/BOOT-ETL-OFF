package com.etloff.batch;

import com.etloff.entity.Product;
import com.etloff.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Writes {@link Product} entities to the database via {@link ProductRepository}.
 */
@Component
public class ProductWriter implements ItemWriter<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductWriter.class);

    private final ProductRepository productRepository;

    private int totalWritten = 0;

    public ProductWriter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void write(Chunk<? extends Product> chunk) {
        productRepository.saveAll(chunk.getItems());
        totalWritten += chunk.size();
        log.debug("Saved chunk of {} products (total: {})", chunk.size(), totalWritten);
    }

    /**
     * Returns the total number of products written so far.
     *
     * @return total product count
     */
    public int getTotalWritten() {
        return totalWritten;
    }

    /**
     * Resets the internal counter. Intended for test setup only.
     */
    public void reset() {
        this.totalWritten = 0;
    }
}
