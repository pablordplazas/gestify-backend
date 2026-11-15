package com.gestify.backend.service;

import com.gestify.backend.dto.request.PreferencesRequest;
import com.gestify.backend.model.Business;
import com.gestify.backend.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    @Transactional
    public Map<String, Object> updatePreferences(Long id, PreferencesRequest request) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado."));

        business.setThemeColor(request.getThemeColor().toUpperCase());
        business.setIsDarkMode(request.getIsDarkMode());
        businessRepository.save(business);

        return Map.of(
                "idNegocio", business.getId(),
                "themeColor", business.getThemeColor(),
                "isDarkMode", business.getIsDarkMode()
        );
    }
}