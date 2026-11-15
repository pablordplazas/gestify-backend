package com.gestify.backend.controller;

import com.gestify.backend.dto.request.PreferencesRequest;
import com.gestify.backend.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PutMapping("/{id}/preferences")
    public ResponseEntity updatePreferences(
            @PathVariable Long id,
            @Valid @RequestBody PreferencesRequest request
    ) {
        return ResponseEntity.ok(businessService.updatePreferences(id, request));
    }
}