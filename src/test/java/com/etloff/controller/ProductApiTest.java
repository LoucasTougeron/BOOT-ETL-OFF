package com.etloff.controller;

import com.etloff.batch.JobCompletionNotificationListener;
import com.etloff.entity.Brand;
import com.etloff.entity.Category;
import com.etloff.entity.Product;
import com.etloff.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the REST API endpoints.
 * <p>
 * Runs the ETL job first to populate the database, then tests all 6 endpoints.
 * </p>
 */
@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = "etl.csv.path=data/open-food-facts.csv")
class ProductApiTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        // Run ETL job to populate database
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncherTestUtils.launchJob(jobParameters);

        // Setup MockMvc
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // --- Product endpoints ---

    @Test
    void getTopByBrand_shouldReturnProducts() throws Exception {
        // Use unique names to avoid conflicts with ETL data
        String uniqueBrand = "TestBrand_" + System.currentTimeMillis();
        
        Brand brand = new Brand(uniqueBrand);
        brandRepository.save(brand);
        Category category = new Category("TestCategory_" + System.currentTimeMillis());
        categoryRepository.save(category);
        
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setBrand(brand);
        product1.setCategory(category);
        
        Product product2 = new Product();
        product2.setName("Product2");
        product2.setBrand(brand);
        product2.setCategory(category);
        
        productRepository.save(product1);
        productRepository.save(product2);

        mockMvc.perform(get("/products/top-by-brand")
                .param("brand", uniqueBrand)
                .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brandName").value(uniqueBrand))
                .andExpect(jsonPath("$[1].brandName").value(uniqueBrand));
    }

    @Test
    void getTopByBrand_shouldRejectMissingBrand() throws Exception {
        mockMvc.perform(get("/products/top-by-brand")
                .param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTopByBrand_shouldRejectNegativeLimit() throws Exception {
        mockMvc.perform(get("/products/top-by-brand")
                .param("brand", "TestBrand")
                .param("limit", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTopByCategory_shouldReturnProducts() throws Exception {
        String uniqueCategory = "TestCategory_" + System.currentTimeMillis();
        Category category = new Category(uniqueCategory);
        categoryRepository.save(category);
        Brand brand = new Brand("TestBrand_" + System.currentTimeMillis());
        brandRepository.save(brand);
        
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setBrand(brand);
        product1.setCategory(category);
        
        productRepository.save(product1);

        mockMvc.perform(get("/products/top-by-category")
                .param("category", uniqueCategory)
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value(uniqueCategory));
    }

    @Test
    void getTopByBrandAndCategory_shouldReturnProducts() throws Exception {
        String uniqueBrand = "TestBrand_" + System.currentTimeMillis();
        String uniqueCategory = "TestCategory_" + System.currentTimeMillis();
        Brand brand = new Brand(uniqueBrand);
        brandRepository.save(brand);
        Category category = new Category(uniqueCategory);
        categoryRepository.save(category);
        
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setBrand(brand);
        product1.setCategory(category);
        
        productRepository.save(product1);

        mockMvc.perform(get("/products/top-by-brand-category")
                .param("brand", uniqueBrand)
                .param("category", uniqueCategory)
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brandName").value(uniqueBrand))
                .andExpect(jsonPath("$[0].categoryName").value(uniqueCategory));
    }

    // --- Reference entity endpoints ---

    @Test
    void getTopIngredients_shouldReturnIngredients() throws Exception {
        mockMvc.perform(get("/ingredients/top")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].productCount").isNumber());
    }

    @Test
    void getTopAllergens_shouldReturnAllergens() throws Exception {
        mockMvc.perform(get("/allergens/top")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].productCount").isNumber());
    }

    @Test
    void getTopAdditives_shouldReturnAdditives() throws Exception {
        mockMvc.perform(get("/additives/top")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].productCount").isNumber());
    }

    @Test
    void getTopIngredients_shouldRejectNegativeLimit() throws Exception {
        mockMvc.perform(get("/ingredients/top")
                .param("limit", "-1"))
                .andExpect(status().isBadRequest());
    }
}