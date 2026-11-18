package com.gestify.backend.dto.response;

import com.gestify.backend.model.SupplierOrder;
import com.gestify.backend.model.SupplierOrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SupplierOrderResponse {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Double shippingCost;
    private Double subtotalProducts;
    private Double totalCost;
    private String status;
    private String orderDate;
    private String receivedDate;
    private String notes;
    private List<OrderItemDto> items;

    @Data
    @Builder
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private String pricingTier;
        private Integer packQuantity;
        private Integer totalUnits;
        private Double tierPrice;
        private Double realUnitCost;
        private Double subtotal;

        public static OrderItemDto fromEntity(SupplierOrderItem item) {
            return OrderItemDto.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .pricingTier(item.getPricingTier())
                    .packQuantity(item.getPackQuantity())
                    .totalUnits(item.getTotalUnits())
                    .tierPrice(item.getTierPrice())
                    .realUnitCost(item.getRealUnitCost())
                    .subtotal(item.getSubtotal())
                    .build();
        }
    }

    public static SupplierOrderResponse fromEntity(SupplierOrder order) {
        return SupplierOrderResponse.builder()
                .id(order.getId())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplierName())
                .shippingCost(order.getShippingCost())
                .subtotalProducts(order.getSubtotalProducts())
                .totalCost(order.getTotalCost())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .receivedDate(order.getReceivedDate())
                .notes(order.getNotes())
                .items(order.getItems().stream().map(OrderItemDto::fromEntity).toList())
                .build();
    }
}