package com.gestify.backend.dto.response;

import com.gestify.backend.model.Product;
import lombok.*;

@Data @Builder @AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long idNegocio;
    private String name;
    private String category;
    private Double price;
    private Integer stock;
    private Boolean isActive;
    private String imageUri;

    public static ProductResponse fromEntity(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .idNegocio(p.getBusiness().getId())
                .name(p.getName())
                .category(p.getCategory())
                .price(p.getPrice())
                .stock(p.getStock())
                .isActive(p.getIsActive())
                .imageUri(p.getImageUri())
                .build();
    }
}