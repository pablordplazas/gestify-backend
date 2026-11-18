package com.gestify.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SupplierRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 60, message = "El nombre del proveedor no puede superar 60 caracteres")
    private String name;

    @Size(max = 50, message = "El contacto no puede superar 50 caracteres")
    private String contactName;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9\\s-]{7,20}$", message = "El teléfono debe contener mínimo 7 dígitos")
    private String phone;

    @Size(max = 40, message = "La categoría no puede superar 40 caracteres")
    private String category;

    private List<Long> productIds = new ArrayList<>();

    private List<String> extraItems = new ArrayList<>();
}