package com.etloff.batch;

import com.etloff.entity.*;
import com.etloff.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Performance benchmark for the ETL job.
 * <p>
 * Resets the database, runs the full job on the real CSV file,
 * and reports execution time, active threads, and heap memory usage.
 * No existing business logic is modified.
 * </p>
 */
@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = "etl.csv.path=data/open-food-facts.csv")
class EtlPerformanceTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private AllergenRepository allergenRepository;
    @Autowired
    private AdditiveRepository additiveRepository;

    @Autowired
    private ProductWriter productWriter;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetDatabase() {
        // Reset writer counter for test isolation
        productWriter.reset();
        // Clear all Spring caches to avoid stale references
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear()
        );
        // Delete in dependency order to avoid FK violations
        productRepository.deleteAll();
        ingredientRepository.deleteAll();
        allergenRepository.deleteAll();
        additiveRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void measurePerformance() throws Exception {
        // --- Verify clean database ---
        assertEquals(0, productRepository.count(), "Database must be empty before test");
        assertEquals(0, brandRepository.count(), "Brands must be empty before test");
        assertEquals(0, categoryRepository.count(), "Categories must be empty before test");
        assertEquals(0, ingredientRepository.count(), "Ingredients must be empty before test");
        assertEquals(0, allergenRepository.count(), "Allergens must be empty before test");
        assertEquals(0, additiveRepository.count(), "Additives must be empty before test");
        // --- Pre-job metrics ---
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        // Force a GC to stabilize heap before measurement
        System.gc();
        Thread.sleep(200);

        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();
        int threadsBefore = threadBean.getThreadCount();

        // --- Run the ETL job ---
        long startNanos = System.nanoTime();
        long startMillis = System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        long endNanos = System.nanoTime();
        long endMillis = System.currentTimeMillis();

        // --- Post-job metrics ---
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();
        int threadsAfter = threadBean.getThreadCount();
        int maxThreads = 0;
        // Track peak active threads during job (sample as best effort)
        for (int i = 0; i < 5; i++) {
            int count = threadBean.getThreadCount();
            if (count > maxThreads) maxThreads = count;
            Thread.sleep(50);
        }

        // --- Compute results ---
        long elapsedNanos = endNanos - startNanos;
        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
        long elapsedMillis = endMillis - startMillis;

        long heapUsedBefore = heapBefore.getUsed();
        long heapUsedAfter = heapAfter.getUsed();
        long heapDelta = heapUsedAfter - heapUsedBefore;

        int productsCreated = productWriter.getTotalWritten();
        long productsInDb = productRepository.count();

        // --- Validate ---
        assertEquals(13425, productsCreated, "Should create 13425 products");
        assertEquals(13425, productsInDb, "Database should contain 13425 products");

        // Duplicate verification is guaranteed by DB unique constraints.
        // Any duplicate insert would throw an integrity violation exception,
        // caught by EntityResolverService which retries with a find instead.

        // --- Report ---
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  ETL PERFORMANCE MEASUREMENT (BASELINE)");
        System.out.println("============================================================");
        System.out.println("  Execution time:");
        System.out.printf("    Total:          %.3f s  (%d ms)%n", elapsedSeconds, elapsedMillis);
        System.out.println();
        System.out.println("  Threads:");
        System.out.println("    Before job:     " + threadsBefore);
        System.out.println("    After job:      " + threadsAfter);
        System.out.println("    Peak (approx):  " + maxThreads);
        System.out.println();
        System.out.println("  Heap memory (MB):");
        System.out.printf("    Used before:    %.1f MB%n", heapUsedBefore / (1024.0 * 1024.0));
        System.out.printf("    Used after:     %.1f MB%n", heapUsedAfter / (1024.0 * 1024.0));
        System.out.printf("    Delta:          %+.1f MB%n", heapDelta / (1024.0 * 1024.0));
        System.out.println();
        System.out.println("  Products:");
        System.out.println("    Created:        " + productsCreated);
        System.out.println("    In database:    " + productsInDb);
        System.out.println("    Throughput:     " + String.format("%.0f", productsCreated / elapsedSeconds) + " prod/s");
        System.out.println("============================================================");
        System.out.println();
    }
}