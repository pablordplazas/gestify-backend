package com.gestify.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SupplierRequest {
    @NotNull(message = "El ID del negocio es obligatorio")
    private Long idNegocio;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String name;

    private String contactName;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s-]{7,20}$", message = "El teléfono debe contener mínimo 7 dígitos")
    private String phone;

    private String category;

    private List<Long> productIds = new ArrayList<>();
}