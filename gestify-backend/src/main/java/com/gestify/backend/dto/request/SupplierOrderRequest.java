package com.gestify.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SupplierOrderRequest {

    @NotNull(message = "El proveedor es obligatorio.")
    private Long supplierId;

    @Min(value = 0, message = "El envío no puede ser negativo.")
    private Double shippingCost = 0.0;

    private String notes;

    /** Si es true, suma el stock inmediatamente al crear el pedido */
    private Boolean receiveImmediately = false;

    @NotEmpty(message = "Agrega al menos un producto al pedido.")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "El producto es obligatorio.")
        private Long productId;

        /** UNIDAD, DECENA, DOCENA */
        @NotNull(message = "La escala de precio es obligatoria.")
        private String pricingTier;

        @NotNull(message = "La cantidad es obligatoria.")
        @Min(value = 1, message = "La cantidad debe ser al menos 1.")
        private Integer packQuantity;

        @NotNull(message = "El precio del proveedor es obligatorio.")
        @Min(value = 0, message = "El precio no puede ser negativo.")
        private Double tierPrice;
    }
}