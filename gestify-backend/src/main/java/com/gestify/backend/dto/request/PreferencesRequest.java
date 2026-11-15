package com.gestify.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreferencesRequest {
    @NotBlank
    private String themeColor;

    @NotNull
    private Boolean isDarkMode;
}