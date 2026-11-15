package com.gestify.backend.dto.response;

import lombok.*;

@Data @Builder @AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long idNegocio;
    private String businessName;
    private String email;
    private String themeColor;
    private Boolean isDarkMode;
}