package com.gestify.backend.dto.response;

import com.gestify.backend.model.Sale;
import lombok.*;

@Data @Builder @AllArgsConstructor
public class SaleResponse {
    private Long id;
    private Long idNegocio;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
    private String time;
    private String date;

    public static SaleResponse fromEntity(Sale s) {
        double unitPrice = s.getUnitPrice() != null
                ? s.getUnitPrice()
                : (s.getQuantity() != null && s.getQuantity() > 0 ? s.getTotal() / s.getQuantity() : 0.0);

        return SaleResponse.builder()
                .id(s.getId())
                .idNegocio(s.getBusiness().getId())
                .productId(s.getProduct().getId())
                .productName(s.getProductName())
                .quantity(s.getQuantity())
                .unitPrice(unitPrice)
                .total(s.getTotal())
                .time(s.getTime())
                .date(s.getDate())
                .build();
    }
}