package com.gestify.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreferencesRequest {

    @NotBlank(message = "El color de tema es obligatorio")
    private String themeColor;

    @NotNull(message = "El modo oscuro es obligatorio")
    private Boolean isDarkMode;
}