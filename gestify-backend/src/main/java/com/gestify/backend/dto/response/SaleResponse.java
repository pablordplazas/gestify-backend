package com.gestify.backend.dto.response;

import com.gestify.backend.model.Sale;
import com.gestify.backend.model.SaleLineItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private String customerEmail;
    private List<LineItemDto> items;

    @Data
    @Builder
    public static class LineItemDto {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;

        public static LineItemDto fromEntity(SaleLineItem item) {
            return LineItemDto.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build();
        }
    }

    public static SaleResponse fromEntity(Sale sale) {
        List<LineItemDto> mappedItems = sale.getItems() == null || sale.getItems().isEmpty()
                ? List.of(LineItemDto.builder()
                .productId(sale.getProduct() != null ? sale.getProduct().getId() : 0L)
                .productName(sale.getProductName())
                .quantity(sale.getQuantity())
                .unitPrice(sale.getUnitPrice() != null ? sale.getUnitPrice() : 0.0)
                .subtotal(sale.getTotal())
                .build())
                : sale.getItems().stream().map(LineItemDto::fromEntity).toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .idNegocio(sale.getBusiness().getId())
                .productId(sale.getProduct() != null ? sale.getProduct().getId() : (mappedItems.isEmpty() ? 0L : mappedItems.get(0).getProductId()))
                .productName(sale.getProductName())
                .quantity(sale.getQuantity())
                .unitPrice(sale.getUnitPrice())
                .total(sale.getTotal())
                .time(sale.getTime())
                .date(sale.getDate())
                .customerName(sale.getCustomerName())
                .customerDocument(sale.getCustomerDocument())
                .customerPhone(sale.getCustomerPhone())
                .customerEmail(sale.getCustomerEmail())
                .items(mappedItems)
                .build();
    }
}