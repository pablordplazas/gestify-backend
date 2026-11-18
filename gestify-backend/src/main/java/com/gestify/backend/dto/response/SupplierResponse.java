package com.gestify.backend.dto.response;

import com.gestify.backend.model.Product;
import com.gestify.backend.model.Supplier;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data @Builder @AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private Long idNegocio;
    private String name;
    private String contactName;
    private String phone;
    private String category;
    private List<Long> productIds;
    private List<String> extraItems;
    private Boolean isActive;

    public static SupplierResponse fromEntity(Supplier s) {
        return SupplierResponse.builder()
                .id(s.getId())
                .idNegocio(s.getBusiness().getId())
                .name(s.getName())
                .contactName(s.getContactName())
                .phone(s.getPhone())
                .category(s.getCategory())
                .productIds(s.getProducts().stream().map(Product::getId).toList())
                .extraItems(new ArrayList<>(s.getExtraItems()))
                .isActive(s.getIsActive())
                .build();
    }
}