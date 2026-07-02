package com.etloff.batch;

import com.etloff.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test that runs the ETL job against the real CSV file
 * and verifies the expected number of products created (13432 data lines).
 */
@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = "etl.csv.path=data/open-food-facts.csv")
class EtlJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ProductWriter productWriter;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void jobShouldProcessAllCsvLines() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus(),
                "Job should complete successfully");

        int productsWritten = productWriter.getTotalWritten();
        long productsInDb = productRepository.count();

        System.out.println("Products written by writer: " + productsWritten);
        System.out.println("Products in database: " + productsInDb);

        assertEquals(13425, productsWritten,
                "Should process exactly 13425 valid data lines (13433 total - 1 header - 7 malformed)");
        assertEquals(13425, productsInDb,
                "Database should contain exactly 13425 products");
    }
}
