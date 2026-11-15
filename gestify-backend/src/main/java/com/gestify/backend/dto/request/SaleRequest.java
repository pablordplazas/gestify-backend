package com.gestify.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SaleRequest {
    @NotNull(message = "El ID del negocio es obligatorio")
    private Long idNegocio;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad vendida debe ser al menos 1")
    private Integer quantity;
}