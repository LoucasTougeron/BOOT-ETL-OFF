package com.etloff.batch;

import com.etloff.entity.*;
import com.etloff.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Investigation test: runs the ETL job, then finds the 5 longest name values
 * across all reference entities.
 */
@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = "etl.csv.path=data/open-food-facts.csv")
class LongNamesInvestigationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

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

    @Test
    void showTop5LongestNames() throws Exception {
        // Run the ETL job to populate the database
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        System.out.println("Job status: " + jobExecution.getStatus());

        // Now query the longest names
        List<NamedEntry> all = new ArrayList<>();

        for (Brand b : brandRepository.findAll()) {
            java.lang.reflect.Field f = Brand.class.getDeclaredField("name");
            f.setAccessible(true);
            String name = (String) f.get(b);
            all.add(new NamedEntry("brand", name, name.length()));
        }
        for (Category c : categoryRepository.findAll()) {
            java.lang.reflect.Field f = Category.class.getDeclaredField("name");
            f.setAccessible(true);
            String name = (String) f.get(c);
            all.add(new NamedEntry("category", name, name.length()));
        }
        for (Ingredient i : ingredientRepository.findAll()) {
            java.lang.reflect.Field f = Ingredient.class.getDeclaredField("name");
            f.setAccessible(true);
            String name = (String) f.get(i);
            all.add(new NamedEntry("ingredient", name, name.length()));
        }
        for (Allergen a : allergenRepository.findAll()) {
            java.lang.reflect.Field f = Allergen.class.getDeclaredField("name");
            f.setAccessible(true);
            String name = (String) f.get(a);
            all.add(new NamedEntry("allergen", name, name.length()));
        }
        for (Additive a : additiveRepository.findAll()) {
            java.lang.reflect.Field f = Additive.class.getDeclaredField("name");
            f.setAccessible(true);
            String name = (String) f.get(a);
            all.add(new NamedEntry("additive", name, name.length()));
        }

        all.sort(Comparator.comparingInt(NamedEntry::length).reversed());

        System.out.println("=== TOP 5 LONGEST NAMES ACROSS ALL TABLES ===");
        all.stream().limit(5).forEach(System.out::println);
    }

    private record NamedEntry(String table, String name, int length) {
        @Override
        public String toString() {
            return table + " | length=" + length + " | " + name;
        }
    }
}
