package com.gestify.backend.controller;

import com.gestify.backend.dto.request.SaleRequest;
import com.gestify.backend.dto.response.SaleResponse;
import com.gestify.backend.security.AuthUser;
import com.gestify.backend.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getSales(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(saleService.getSalesByBusiness(user.businessId()));
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody SaleRequest request
    ) {
        return ResponseEntity.ok(saleService.registerSale(user.businessId(), request));
    }
}