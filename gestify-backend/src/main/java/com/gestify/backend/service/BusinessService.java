package com.gestify.backend.service;

import com.gestify.backend.dto.request.PreferencesRequest;
import com.gestify.backend.exception.ResourceNotFoundException;
import com.gestify.backend.model.Business;
import com.gestify.backend.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BusinessService {

    public static final Set<String> ALLOWED_THEMES = Set.of("BLUE", "ORANGE", "TEAL", "WINE", "AMBER", "BLACK");
    public static final String DEFAULT_THEME = "BLUE";

    private final BusinessRepository businessRepository;

    public static String normalizeTheme(String theme) {
        if (theme == null) return DEFAULT_THEME;
        String upper = theme.trim().toUpperCase();
        return ALLOWED_THEMES.contains(upper) ? upper : DEFAULT_THEME;
    }

    @Transactional
    public Map<String, Object> updatePreferences(Long businessId, PreferencesRequest request) {
        String theme = request.getThemeColor().trim().toUpperCase();
        if (!ALLOWED_THEMES.contains(theme)) {
            throw new IllegalArgumentException("Color de tema no válido: " + request.getThemeColor());
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado."));

        business.setThemeColor(theme);
        business.setIsDarkMode(request.getIsDarkMode());
        businessRepository.save(business);

        return Map.of(
                "idNegocio", business.getId(),
                "themeColor", business.getThemeColor(),
                "isDarkMode", business.getIsDarkMode()
        );
    }
}