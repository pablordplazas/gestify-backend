package com.gestify.backend.service;

import com.gestify.backend.dto.request.LoginRequest;
import com.gestify.backend.dto.request.RegisterRequest;
import com.gestify.backend.dto.response.AuthResponse;
import com.gestify.backend.model.Business;
import com.gestify.backend.repository.BusinessRepository;
import com.gestify.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (businessRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("El correo ya está registrado en Gestify.");
        }

        Business business = Business.builder()
                .businessName(request.getBusinessName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .themeColor("AZUL")
                .isDarkMode(false)
                .build();

        Business saved = businessRepository.save(business);
        String token = jwtService.generateToken(saved.getId(), saved.getEmail());

        return buildAuthResponse(saved, token);
    }

    public AuthResponse login(LoginRequest request) {
        Business business = businessRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));

        if (!passwordEncoder.matches(request.getPassword(), business.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = jwtService.generateToken(business.getId(), business.getEmail());
        return buildAuthResponse(business, token);
    }

    private AuthResponse buildAuthResponse(Business b, String token) {
        return AuthResponse.builder()
                .token(token)
                .idNegocio(b.getId())
                .businessName(b.getBusinessName())
                .email(b.getEmail())
                .themeColor(b.getThemeColor())
                .isDarkMode(b.getIsDarkMode())
                .build();
    }
}