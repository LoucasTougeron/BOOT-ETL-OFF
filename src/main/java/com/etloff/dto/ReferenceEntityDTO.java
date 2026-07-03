package com.etloff.dto;

/**
 * Simple DTO for reference entities (ingredients, allergens, additives).
 */
public class ReferenceEntityDTO {
    private Long id;
    private String name;
    private long productCount;

    public ReferenceEntityDTO() {
    }

    public ReferenceEntityDTO(Long id, String name, long productCount) {
        this.id = id;
        this.name = name;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getProductCount() {
        return productCount;
    }
}