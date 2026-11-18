package com.gestify.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaleRequest {

    // Compatibilidad con formato de un solo producto
    private Long productId;
    private Integer quantity;

    // Nuevo formato de carrito multi-producto
    @Valid
    private List<Item> items;

    // Datos opcionales del cliente
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private String customerEmail;

    @Data
    public static class Item {
        @NotNull(message = "El producto es obligatorio.")
        private Long productId;

        @NotNull(message = "La cantidad es obligatoria.")
        @Min(value = 1, message = "La cantidad debe ser al menos 1.")
        private Integer quantity;
    }
}