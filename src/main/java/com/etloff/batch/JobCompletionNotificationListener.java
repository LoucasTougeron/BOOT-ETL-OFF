package com.etloff.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Listener that logs a summary when the ETL job completes.
 */
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final ProductWriter productWriter;

    public JobCompletionNotificationListener(ProductWriter productWriter) {
        this.productWriter = productWriter;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        int productsWritten = productWriter.getTotalWritten();
        // rejected = total input - written (since processor returns null for rejected lines)
        long readCount = jobExecution.getStepExecutions().stream()
                .findFirst()
                .map(se -> se.getReadCount())
                .orElse(0L);
        int rejected = (int) readCount - productsWritten;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("ETL job completed successfully. Products created: {}, lines rejected (malformed): {}",
                    productsWritten, rejected);
        } else {
            log.warn("ETL job finished with status: {}", jobExecution.getStatus());
        }
    }
}
