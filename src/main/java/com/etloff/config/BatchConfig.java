package com.etloff.config;

import com.etloff.batch.JobCompletionNotificationListener;
import com.etloff.batch.ProductProcessor;
import com.etloff.batch.ProductWriter;
import com.etloff.entity.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch configuration for the Open Food Facts ETL job.
 * <p>
 * Reads the CSV file, parses/cleans/resolves each line into a Product entity,
 * and persists it to the database.
 * </p>
 */
@Configuration
public class BatchConfig {

    @Value("${etl.csv.path}")
    private String csvPath;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ProductProcessor productProcessor;
    private final ProductWriter productWriter;
    private final JobCompletionNotificationListener listener;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       ProductProcessor productProcessor,
                       ProductWriter productWriter,
                       JobCompletionNotificationListener listener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.productProcessor = productProcessor;
        this.productWriter = productWriter;
        this.listener = listener;
    }

    /**
     * Reads the CSV file as raw Strings, skipping the header line.
     */
    @Bean
    public FlatFileItemReader<String> csvReader() {
        return new FlatFileItemReaderBuilder<String>()
                .name("csvReader")
                .resource(new FileSystemResource(csvPath))
                .lineMapper((line, lineNumber) -> line)
                .linesToSkip(1)
                .build();
    }

    /**
     * ETL step: reads chunks of 100 lines, processes them into Products,
     * and writes them to the database.
     */
    @Bean
    public Step etlStep() {
        return new StepBuilder("etlStep", jobRepository)
                .<String, Product>chunk(100, transactionManager)
                .reader(csvReader())
                .processor(productProcessor)
                .writer(productWriter)
                .build();
    }

    /**
     * ETL job with a run ID incrementer for restartability.
     */
    @Bean
    public Job etlJob() {
        return new JobBuilder("etlJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(etlStep())
                .build();
    }
}