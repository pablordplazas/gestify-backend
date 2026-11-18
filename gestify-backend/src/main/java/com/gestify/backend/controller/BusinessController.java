package com.gestify.backend.controller;

import com.gestify.backend.dto.request.PreferencesRequest;
import com.gestify.backend.security.AuthUser;
import com.gestify.backend.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PutMapping("/me/preferences")
    public ResponseEntity<?> updatePreferences(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody PreferencesRequest request
    ) {
        return ResponseEntity.ok(businessService.updatePreferences(user.businessId(), request));
    }
}